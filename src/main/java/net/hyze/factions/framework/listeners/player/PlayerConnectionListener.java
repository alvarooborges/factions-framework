package net.hyze.factions.framework.listeners.player;

import com.google.common.collect.Sets;
import dev.utils.echo.packet.impl.HyzeChangeServerPacket;
import dev.utils.shared.concurrent.NamedThreadFactory;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.shared.world.location.SerializedLocation;
import net.hyze.core.spigot.CoreSpigotConstants;
import net.hyze.core.spigot.CoreSpigotSettings;
import net.hyze.core.spigot.misc.combat.CombatManager;
import net.hyze.core.spigot.misc.notification.actionbar.ActionBarRunnable;
import net.hyze.factions.framework.FactionsPlugin;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.connect.FactionsConnectManager;
import net.hyze.factions.framework.misc.scoreboard.ScoreboardManager;
import net.hyze.factions.framework.misc.utils.FactionUserUtils;
import net.hyze.factions.framework.user.FactionUser;
import net.hyze.factions.framework.user.FactionsActionBar;
import net.hyze.factions.framework.user.healthpoints.HealthPointsUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerInitialSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;
import java.util.concurrent.*;

public class PlayerConnectionListener implements Listener {

    //private ExecutorService executor = Executors.newCachedThreadPool(new NamedThreadFactory("HyzeServerJoinExecutor", true));

    private static Set<String> PENDING_JOIN = Sets.newHashSet();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(AsyncPlayerPreLoginEvent event) {
        /*
         * Buscando usuário no cache
         *
         * Neste ponto o cache de usuário já foi atualizado pelo Core-Spigot
         */
        User handle = CoreProvider.Cache.Local.USERS.provide().get(event.getName());

        if (handle == null) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Usuário não encontrado.");
            return;
        }

//        try {
//            PENDING_JOIN.add(event.getName());
//
//            Future<Void> future = executor.submit(() -> {
//
//                CoreProvider.Redis.ECHO.provide().publish(new HyzeChangeServerPacket(event.getName()), response -> {
//                    PENDING_JOIN.remove(event.getName());
//                });
//
//                while (!PENDING_JOIN.contains(event.getName())) ;
//
//                return null;
//            });
//
//            future.get(5, TimeUnit.SECONDS);
//        } catch (InterruptedException | ExecutionException | TimeoutException e) {
//            PENDING_JOIN.remove(event.getName());
//            e.printStackTrace();
//            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Algo de errado aconteceu.");
//            return;
//        }

        /*
         * Removendo do cache
         */
        FactionsProvider.Cache.Local.USERS.provide().remove(handle);

        /*
         * Buscando dados do jogador
         */
        FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(handle);

        FactionsProvider.Cache.Local.USERS_RELATIONS.provide().refreshByUser(user);

        if (!user.getHandle().hasGroup(Group.MANAGER)) {
            user.getOptions().setAdminModeEnabled(false);

            user.getOptions().sync();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        FactionUser factionUser = FactionUserUtils.getUser(player.getName());

        if (player.getGameMode().equals(GameMode.CREATIVE) && !factionUser.getHandle().hasGroup(Group.MANAGER)) {
            player.setGameMode(GameMode.SURVIVAL);
        }

        if (AppType.FACTIONS_VIP.isCurrent()) {

            Bukkit.getScheduler().runTaskLater(
                    FactionsPlugin.getInstance(),
                    () -> {
                        event.getPlayer().setAllowFlight(true);
                        event.getPlayer().setFlying(true);
                    },
                    1L
            );

        } else if (!factionUser.getHandle().hasGroup(Group.MODERATOR)) {
            event.getPlayer().setAllowFlight(false);
            event.getPlayer().setFlying(false);
        }

        if (FactionsProvider.getSettings().getActionBarEnabledAt().contains(CoreProvider.getApp().getType())) {
            factionUser.setActionBarNotification(new FactionsActionBar(factionUser));
        }

        Float absorptionHearts = ((CraftPlayer) player).getHandle().getAbsorptionHearts();

        if (Float.isNaN(absorptionHearts)) {
            player.setHealth(1);
            player.setMaxHealth(20);
            ((CraftPlayer) player).getHandle().setAbsorptionHearts(0);
        }

        if (!FactionsProvider.getSettings().getDefaultScoreboardDisabledAt().contains(CoreProvider.getApp().getType())) {
            ScoreboardManager.setup(factionUser);
        }
    }

    @EventHandler
    public void on(PlayerInitialSpawnEvent event) {
        User user = CoreProvider.Cache.Local.USERS.provide().get(event.getPlayer().getName());

        FactionsConnectManager.JoinBag joinBag = FactionsConnectManager.getJoinBag(user);

        event.setSpawnLocation(SerializedLocation.of(
                CoreProvider.getApp().getId(),
                CoreSpigotSettings.getInstance().getAppTypeWorldNames().getOrDefault(CoreProvider.getApp().getType(), "world"),
                joinBag.getPosition()
        ).parser(CoreSpigotConstants.LOCATION_PARSER));

        if (joinBag.getWelcomeMessage() != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (event.getPlayer().isOnline() && joinBag.getWelcomeMessage() != null) {
                        event.getPlayer().sendMessage(joinBag.getWelcomeMessage());
                    }
                }
            }.runTaskLater(FactionsPlugin.getInstance(), 1);
        }

        FactionsConnectManager.resetJoinBag(user);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerQuitEvent event) {
        PENDING_JOIN.remove(event.getPlayer().getName());

        event.setQuitMessage(null);

        Player player = event.getPlayer();

        FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(player.getName());

        if (CombatManager.isTagged(user.getHandle())) {
            player.setHealth(0);
        }
    }
}
