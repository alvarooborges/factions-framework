package net.hyze.factions.framework;

import com.comphenix.protocol.ProtocolLibrary;
import dev.utils.shared.setup.Setup;
import dev.utils.shared.setup.SetupRegistry;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.hyze.beacon.attributes.AttributeRegistry;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.echo.packets.user.connect.ConnectReason;
import net.hyze.core.shared.echo.packets.user.connect.UserConnectHandShakePacket;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.CoreSpigotConstants;
import net.hyze.core.spigot.CoreSpigotPlugin;
import net.hyze.core.spigot.CustomPlugin;
import net.hyze.core.spigot.misc.combat.CombatManager;
import net.hyze.core.spigot.misc.customcraft.CustomCraft;
import net.hyze.core.spigot.misc.tpa.commands.TPAcceptCommand;
import net.hyze.factions.framework.beacon.FactionBeaconAttribute;
import net.hyze.factions.framework.connect.ConnectSetup;
import net.hyze.factions.framework.misc.customitem.CustomItemSetup;
import net.hyze.factions.framework.misc.economy.FactionsEconomy;
import net.hyze.factions.framework.misc.lostfortress.LostFortressSetup;
import net.hyze.factions.framework.misc.offers.OfferRunnable;
import net.hyze.factions.framework.misc.power.PowerScheduler;
import net.hyze.factions.framework.misc.scoreboard.FactionsScoreboard;
import net.hyze.factions.framework.misc.tablist.TabListManager;
import net.hyze.factions.framework.misc.utils.FactionUserUtils;
import net.hyze.factions.framework.misc.utils.FactionUtils;
import net.hyze.factions.framework.ranking.RankingFactory;
import net.hyze.factions.framework.setups.AuctionSetup;
import net.hyze.factions.framework.setups.CommandsSetup;
import net.hyze.factions.framework.setups.EchoSetup;
import net.hyze.factions.framework.setups.ListenersSetup;
import net.hyze.factions.framework.spawners.SpawnersPacketListener;
import net.hyze.factions.framework.spawners.SpawnersSetup;
import net.hyze.factions.framework.user.FactionUser;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

@Getter
public class FactionsPlugin extends CustomPlugin {

    @Getter
    private static FactionsPlugin instance;

    @NonNull
    @Setter
    private Function<Location, String> ownerAppResolver = location -> null;

    @Getter
    private final SetupRegistry<JavaPlugin, Setup<JavaPlugin>> setupRegistry;

    private final TabListManager tabListManager = new TabListManager();

    public FactionsPlugin() {
        super(false);
        instance = this;
        setupRegistry = new SetupRegistry<>(this);
    }

    @Override
    public void onLoad() {
        super.onLoad();

        Class<CombatManager> combatManagerClass = CombatManager.class;

        CoreSpigotPlugin.setEconomy(new FactionsEconomy());

        MinecraftServer.getServer().getPlayerList().playerFileData = new PlayerFileData();

        TPAcceptCommand.registerFilter("f", "Aceitar pedidos dos membros da sua facção", (target, requesterId) -> {
            FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(target);

            if (user.getRelation() != null) {
                User requester = CoreProvider.Cache.Local.USERS.provide().get(requesterId);

                return FactionUtils.isMember(requester, user.getRelation().getFaction());
            }

            return false;
        });

        setupRegistry.register(new ListenersSetup());
        setupRegistry.register(new CommandsSetup());
        setupRegistry.register(new ConnectSetup());
        setupRegistry.register(new EchoSetup());
        setupRegistry.register(new SpawnersSetup());
        setupRegistry.register(new CustomItemSetup());
        setupRegistry.register(new AuctionSetup());

        setupRegistry.load(
                throwable -> {
                    throwable.printStackTrace();
                    Bukkit.shutdown();

                });
    }

    @Override
    public void onEnable() {
        super.onEnable();

        CoreSpigotPlugin.setScoreboardCreator(user -> {
            FactionUser factionUser = FactionsProvider.Cache.Local.USERS.provide().get(user);
            return new FactionsScoreboard(factionUser);
        });

        setupRegistry.enable(
                throwable -> {
                    throwable.printStackTrace();
                    Bukkit.shutdown();

                });

        PowerScheduler.start();

        /*
         * Base Perdida.
         */
        LostFortressSetup.setup();

        /*
         * Beacon.
         */
        AttributeRegistry.registerAttributes(FactionBeaconAttribute.values());
        AttributeRegistry.registerAttributeKeys();

        if (FactionsProvider.getSettings().isHandleQueue()) {
            Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
                Set<Integer> ids = CoreProvider.Cache.Redis.QUEUES.provide().range(FactionsProvider.getServer(), 6);

                int maxOnlineCount = FactionsProvider.getServer().getMaxPlayers();
                long onlineCount = CoreProvider.Cache.Local.SERVERS.provide().getOnlineCount(FactionsProvider.getServer());

                ids.stream()
                        .map(id -> CoreProvider.Cache.Local.USERS.provide().get(id))
                        .filter(Objects::nonNull)
                        .filter(user -> {

                            if (onlineCount > maxOnlineCount) {
                                return user.hasGroup(Group.ARCANE);
                            }

                            return true;
                        })
                        .limit(2)
                        .forEach(user -> {
                            if (user.isLogged()) {
                                CoreProvider.Redis.ECHO.provide().publish(new UserConnectHandShakePacket(
                                        user,
                                        CoreProvider.getApp().getId(),
                                        ConnectReason.JOIN
                                ));
                            }

                            CoreProvider.Cache.Redis.QUEUES.provide().remove(user);
                        });

            }, 20, 10);
        }

        CoreSpigotConstants.IS_HOSTILE = (player1, player2) -> {
            return FactionUtils.isHostile(FactionUserUtils.getUser(player1), FactionUserUtils.getUser(player2));
        };

        Bukkit.getScheduler().runTaskTimer(this, new OfferRunnable(), 20L, 5L * 60L * 20L);

        CustomCraft.removeDefaultRecipe(new MaterialData(Material.ITEM_FRAME));

        ProtocolLibrary.getProtocolManager().addPacketListener(new SpawnersPacketListener());

        if (FactionsProvider.getSettings().isGlobalTablistEnabled()) {
            tabListManager.enable();
        }
    }

    @Override
    public void onDisable() {
        RankingFactory.RANKING_EXECUTOR_SERVICE.shutdownNow();

        super.onDisable();

        Bukkit.getScheduler().cancelTasks(this);
    }
}
