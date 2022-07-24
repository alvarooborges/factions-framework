package net.hyze.factions.framework.misc.tablist;

import com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import dev.utils.echo.IEchoListener;
import dev.utils.shared.concurrent.NamedThreadFactory;
import net.citizensnpcs.api.npc.NPC;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.misc.cooldowns.Cooldowns;
import net.hyze.core.shared.misc.cooldowns.UserCooldowns;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.CoreSpigotPlugin;
import net.hyze.core.spigot.echo.packets.PlayerInfoDataUpdatePacket;
import net.hyze.core.spigot.misc.scoreboard.IBoard;
import net.hyze.core.spigot.misc.scoreboard.bukkit.GroupScoreboard;
import net.hyze.end.EndProvider;
import net.hyze.end.user.EndUser;
import net.hyze.factions.framework.FactionsPlugin;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.greenrobot.eventbus.Subscribe;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TabListManager implements IEchoListener {

    private static final ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("TabListExecutor", true));
    public static final ExecutorService SYNC_EXECUTOR = Executors.newFixedThreadPool(10, new NamedThreadFactory("TabListSyncExecutor", true));

    public final BiFunction<Player, String, String> COOLDOWNS_KEY_PARSER = (player, targetName) -> {
        return "player_info_update_" + player.getName() + "||" + targetName;
    };

    public final Function<String, String> COOLDOWNS_LATENCY_KEY_PARSER = (player) -> {
        return "player_info_update_latency_" + player;
    };

    private final Set<User> ONLINE_USERS = Sets.newConcurrentHashSet();
    private final Object LOCK = new Object();

    public final String CUSTOM_METADATA_KEY = "hyze_custom_packet";

    // Itens do tab enviado
    private final Map<Player, Set<User>> SENT = new WeakHashMap<>();

    // Itens do tab para serem removidos
    private final Map<Player, Set<User>> TO_REMOVE = new WeakHashMap<>();

    // Itens do tab para serem adicionados
    private final Map<Player, Set<User>> TO_ADD = new WeakHashMap<>();

    public final Map<UUID, PlayerInfoData> ORIGINALS_PLAYER_INFO = Maps.newConcurrentMap();

    private boolean enabled = false;

    public void enable() {
        if (enabled) {
            return;
        }

        enabled = true;

        registerProtocolListeners();
        registerEchoListeners();
        startUpdaterTask();
    }

    private void startUpdaterTask() {
        EXECUTOR.scheduleAtFixedRate(() -> {

            CompletableFuture<?> future = CompletableFuture.runAsync(() -> {
                Set<User> users = CoreProvider.Cache.Redis.USERS_STATUS.provide().fetchUsersByServer(CoreProvider.getApp().getServer());

                Set<User> allData = users.stream()
                        .sorted(Comparator.comparing(User::getHighestGroup))
                        .collect(Collectors.toCollection(LinkedHashSet::new));

                int overflow = allData.size() - 85;

                if (overflow > 0) {
                    Set<User> toRemove = allData.stream()
                            .skip(85)
                            .filter(user -> {
                                Player player = Bukkit.getPlayerExact(user.getNick());

                                return player == null || !player.isOnline();
                            })
                            .collect(Collectors.toSet());

                    allData.removeAll(toRemove);
                }

                Set<User> copyOfUsers = Sets.newHashSet();

                synchronized (LOCK) {
                    ONLINE_USERS.clear();
                    ONLINE_USERS.addAll(allData);

                    copyOfUsers.addAll(ONLINE_USERS);
                }

                Collection<? extends Player> players = ImmutableList.copyOf(Bukkit.getOnlinePlayers());

                for (Player player : players) {
                    if (player.isOnline()) {
                        process(player, copyOfUsers);
                        TabListManager.SYNC_EXECUTOR.submit(() -> FactionsPlugin.getInstance().getTabListManager().sync(player));
                    }
                }
            });

            try {
                future.get(5, TimeUnit.SECONDS);
            } catch (Throwable ignore) {

            }

        }, 0, 5, TimeUnit.SECONDS);
    }

    private void registerEchoListeners() {
        CoreProvider.Redis.ECHO.provide().registerListener(this);
    }

    private void registerProtocolListeners() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PlayerInfoPacketAdapter(this));
    }

    @Subscribe
    public void on(PlayerInfoDataUpdatePacket packet) {
        ORIGINALS_PLAYER_INFO.put(packet.getProfile().getUUID(), new PlayerInfoData(
                packet.getProfile(),
                packet.getLatency(),
                packet.getGameMode(),
                packet.getDisplayName()
        ));
    }

    public void setup(Player player) {
        if (!enabled) {
            return;
        }

        SENT.remove(player);

        sync(player);
    }

    public void sync(Player player) {
        sync(player, false);
    }

    private void process(Player player, Set<User> tab) {
        User user = CoreProvider.Cache.Local.USERS.provide().getIfPresent(player.getName());

        if (user == null) {
            return;
        }

        Set<User> sent = SENT.computeIfAbsent(player, k -> Sets.newConcurrentHashSet());
        Set<User> toAdd = TO_ADD.computeIfAbsent(player, k -> Sets.newConcurrentHashSet());
        Set<User> toRemove = TO_REMOVE.computeIfAbsent(player, k -> Sets.newConcurrentHashSet());

        if (sent.isEmpty()) {
            toAdd.addAll(tab);
        } else {
            toRemove.addAll(sent.stream()
                    .filter(data -> !tab.contains(data))
                    .collect(Collectors.toSet()));

            toAdd.addAll(tab.stream()
                    .filter(data -> !sent.contains(data))
                    .collect(Collectors.toSet()));
        }
    }

    public void sync(Player player, boolean forceUpdate) {
        if (!enabled || !player.isOnline()) {
            SENT.remove(player);
            return;
        }

        User user = CoreProvider.Cache.Local.USERS.provide().getIfPresent(player.getName());

        if (user == null) {
            return;
        }

        if (!forceUpdate && !UserCooldowns.hasEnded(user, "tab_list_manager_sync")) {
            return;
        }

        UserCooldowns.start(user, "tab_list_manager_sync", 5, TimeUnit.SECONDS);

        if (forceUpdate) {
            synchronized (LOCK) {
                process(player, Sets.newHashSet(ONLINE_USERS));
            }
        }

        Set<User> sent = SENT.computeIfAbsent(player, k -> Sets.newConcurrentHashSet());
        Set<User> toAdd = TO_ADD.computeIfAbsent(player, k -> Sets.newConcurrentHashSet());
        Set<User> toRemove = TO_REMOVE.computeIfAbsent(player, k -> Sets.newConcurrentHashSet());

        if (!toRemove.isEmpty()) {
            sent.removeAll(toRemove);

            WrapperPlayServerPlayerInfo removePacket = new WrapperPlayServerPlayerInfo();

            removePacket.setAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
            removePacket.setData(toRemove.stream()
                    .map(InfoData::fromUser)
                    .map(InfoData::getData)
                    .collect(Collectors.toList())
            );

            toRemove.clear();

            removePacket.getHandle().setMeta(CUSTOM_METADATA_KEY, true);
            removePacket.sendPacket(player);
        }

        if (!sent.contains(user)) {
            toAdd.add(user);
        }

        if (!toAdd.isEmpty()) {
            sent.addAll(toAdd);

            WrapperPlayServerPlayerInfo addPacket = new WrapperPlayServerPlayerInfo();

            addPacket.setAction(EnumWrappers.PlayerInfoAction.ADD_PLAYER);
            addPacket.setData(toAdd.stream()
                    .map(target -> {
                        Cooldowns.start(COOLDOWNS_KEY_PARSER.apply(player, target.getNick()), 5, TimeUnit.SECONDS);

                        return ORIGINALS_PLAYER_INFO.getOrDefault(
                                UUID.nameUUIDFromBytes(("OfflinePlayer:" + target.getNick()).getBytes(Charsets.UTF_8)),
                                InfoData.fromUser(target).getData()
                        );
                    })
                    .collect(Collectors.toList())
            );

            toAdd.clear();

            addPacket.getHandle().setMeta(CUSTOM_METADATA_KEY, true);

            addPacket.sendPacket(player);

            updateScoreboardTeams(user.getNick(), addPacket);
        }

        if (!sent.isEmpty()) {
            List<User> toUpdate = sent.stream()
                    .filter(target -> {
                        boolean canUpdate = Cooldowns.hasEnded(COOLDOWNS_KEY_PARSER.apply(player, target.getNick()));

                        if (canUpdate) {
                            Cooldowns.start(COOLDOWNS_KEY_PARSER.apply(player, target.getNick()), 5, TimeUnit.SECONDS);
                        }

                        return forceUpdate || canUpdate;
                    })
                    .collect(Collectors.toList());

            List<PlayerInfoData> value = toUpdate.stream()
                    .map(target -> ORIGINALS_PLAYER_INFO.getOrDefault(
                            UUID.nameUUIDFromBytes(("OfflinePlayer:" + target.getNick()).getBytes(Charsets.UTF_8)),
                            InfoData.fromUser(target).getData()
                    ))
                    .collect(Collectors.toList());

            {
                WrapperPlayServerPlayerInfo updatePacket = new WrapperPlayServerPlayerInfo();
                updatePacket.setAction(EnumWrappers.PlayerInfoAction.UPDATE_LATENCY);
                updatePacket.setData(value);
                updatePacket.getHandle().setMeta(CUSTOM_METADATA_KEY, true);
                updatePacket.sendPacket(player);
            }

            {
                WrapperPlayServerPlayerInfo updatePacket = new WrapperPlayServerPlayerInfo();
                updatePacket.setAction(EnumWrappers.PlayerInfoAction.UPDATE_GAME_MODE);
                updatePacket.setData(value);
                updatePacket.getHandle().setMeta(CUSTOM_METADATA_KEY, true);
                updatePacket.sendPacket(player);
            }
        }
    }

    public void updateScoreboardTeams(String nick, WrapperPlayServerPlayerInfo wrapper) {
        if (!enabled) {
            return;
        }

        FactionUser factionUser = FactionsProvider.Cache.Local.USERS.provide().getIfPresent(nick);

        if (factionUser != null) {

            IBoard board = factionUser.getBoard();

            if (board == null) {
                if (CoreProvider.getApp().getType() == AppType.FACTIONS_END) {
                    EndUser endUser = EndProvider.Cache.Local.USERS.provide().getIfPresent(factionUser.getId());

                    if (endUser != null) {
                        board = endUser.getBoard();
                    }
                }
            }

            if (!(board instanceof GroupScoreboard)) {
                return;
            }

            GroupScoreboard scoreboard = (GroupScoreboard) board;

            for (NPC npc : CoreSpigotPlugin.getInstance().getNpcRegistry()) {
                if (npc.getEntity() instanceof Player) {
                    scoreboard.registerNPC(npc.getName());
                }
            }

            for (PlayerInfoData data : wrapper.getData()) {
                User target = CoreProvider.Cache.Local.USERS.provide().get(data.getProfile().getName());

                if (target != null) {
                    scoreboard.registerUser(target);
                }
            }
        }
    }

}