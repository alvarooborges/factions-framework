package net.hyze.factions.framework.connect;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dev.utils.echo.IEchoListener;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.cache.local.utils.CaffeineScheduler;
import net.hyze.core.shared.echo.packets.SendMessagePacket;
import net.hyze.core.shared.echo.packets.user.connect.ConnectReason;
import net.hyze.core.shared.echo.packets.user.connect.UserConnectHandShakeErrorPacket;
import net.hyze.core.shared.echo.packets.user.connect.UserConnectHandShakePacket;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.misc.utils.Position;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.CoreSpigotPlugin;
import net.hyze.core.spigot.CoreSpigotSettings;
import net.hyze.core.spigot.connect.events.AsyncUserConnectHandShakeEvent;
import net.hyze.core.spigot.connect.events.ConnectSide;
import net.hyze.core.spigot.misc.combat.CombatManager;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.TeleportManager;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.lands.Claim;
import net.hyze.factions.framework.misc.utils.FactionDefaultMessage;
import net.hyze.factions.framework.user.FactionUser;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.util.NumberConversions;
import org.greenrobot.eventbus.Subscribe;

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class FactionsConnectManager implements Listener {

    private static final Cache<Integer, JoinBag> JOIN_BAG = Caffeine.newBuilder()
            .scheduler(CaffeineScheduler.getInstance())
            .expireAfterWrite(5, TimeUnit.SECONDS)
            .build();

    @Getter
    @RequiredArgsConstructor
    @ToString
    public static class JoinBag {

        private final Position position;
        private final BaseComponent[] welcomeMessage;
        private final ConnectReason reason;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onHandShakeMonitor(AsyncUserConnectHandShakeEvent event) {
        UserConnectHandShakePacket packet = event.getPacket();

        if (event.getSide() == ConnectSide.TARGET) {
            Position position = FactionsConnectManager.extractLocation(packet);

            if (!FactionsConnectManager.canJoinHere(event.getUser(), packet.getReason(), position)) {
                event.setCancelled(true);
                return;
            }

            JOIN_BAG.put(packet.getUserId(), new JoinBag(
                    position,
                    packet.getWelcomeMessage(),
                    packet.getReason()
            ));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onHandShakeLowest(AsyncUserConnectHandShakeEvent event) {

        if (event.getSide() == ConnectSide.TARGET) {

            if (CoreProvider.getApp().getStatus().isMaintenance() && !event.getUser().hasGroup(Group.MANAGER)) {
                CoreProvider.Redis.ECHO.provide().publish(new SendMessagePacket(
                        Collections.singleton(event.getUser().getId()),
                        TextComponent.fromLegacyText(ChatColor.RED + "Este servidor está em manutenção.")
                ));

                event.setCancelled(true);
            }

        } else {

            if (CombatManager.isTagged(event.getUser())) {

                FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(event.getUser());

                if (TeleportManager.REASONS_BYPASS.contains(event.getPacket().getReason())) {
                    CombatManager.untag(event.getUser());
                } else {
                    Message.ERROR.sendDefault(user.getPlayer(), FactionDefaultMessage.COMBAT_TELEPORT_ERROR);
                    event.setCancelled(true);
                }
            }
        }
    }

    private static boolean canJoinHere(User handle, ConnectReason reason, Position position) {

        if (!handle.hasGroup(Group.MODERATOR)) {
            if (reason != ConnectReason.TPA) {

                Claim claim = FactionsProvider.Cache.Local.LANDS.provide().get(
                        NumberConversions.floor(position.getX()) >> 4,
                        NumberConversions.floor(position.getZ()) >> 4,
                        Claim.class
                );

                if (claim != null) {
                    FactionUserRelation relation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getByUser(handle);

                    return relation != null && Objects.equals(relation.getFaction().getId(), claim.getFactionId());
                }
            }
        }

        return true;
    }

    private static Position extractLocation(UserConnectHandShakePacket packet) {

        User targetUser = CoreProvider.Cache.Local.USERS.provide().get(packet.getTargetId());

        if (targetUser != null) {
            Player target = Bukkit.getPlayerExact(targetUser.getNick());

            if (target != null && target.isOnline()) {
                return new Position(
                        target.getLocation().getX(),
                        target.getLocation().getY(),
                        target.getLocation().getZ(),
                        target.getLocation().getYaw(),
                        target.getLocation().getPitch()
                );
            }
        }

        if (packet.getPosition() != null) {
            return packet.getPosition();
        }

        Position spawn = CoreSpigotSettings.getInstance().getAppSpawnPositions().getOrDefault(
                CoreProvider.getApp().getId(),
                CoreSpigotSettings.getInstance().getSpawnPosition()
        );

        return spawn.clone();
    }


    public static JoinBag getJoinBag(User user) {
        JoinBag bag = JOIN_BAG.getIfPresent(user.getId());

        if (bag != null) {
            return bag;
        }

        Position position = CoreSpigotSettings.getInstance().getAppSpawnPositions().getOrDefault(
                CoreProvider.getApp().getId(),
                CoreSpigotSettings.getInstance().getSpawnPosition()
        );

        return new JoinBag(position, null, ConnectReason.RESPAWN);
    }

    public static void resetJoinBag(User user) {
        JOIN_BAG.invalidate(user.getId());
    }
}
