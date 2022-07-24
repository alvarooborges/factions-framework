package net.hyze.factions.framework.user.healthpoints.listeners;

import com.google.common.collect.Sets;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.echo.packets.BroadcastMessagePacket;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.messages.MessageUtils;
import net.hyze.factions.framework.FactionsPlugin;
import net.hyze.factions.framework.user.healthpoints.HealthPointsUtils;
import net.hyze.hyzeskills.events.experience.McMMOPlayerLevelUpEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class HealthPointsListeners implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskLater(FactionsPlugin.getInstance(),() -> {
            HealthPointsUtils.updatePlayerMaxHealth(player);
        }, 10L);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void on(McMMOPlayerLevelUpEvent event) {
        Player player = event.getPlayer();

        double maxHealth = player.getMaxHealth();

        HealthPointsUtils.updatePlayerMaxHealth(player);

        if (player.getMaxHealth() > maxHealth) {

            String text = MessageUtils.translateColorCodes(String.format(
                    " &c[❤] &f%s &fcompletou o &eNível %s &fe ganhou &c+0.5 &fpontos de vida.",
                    player.getName(),
                    HealthPointsUtils.getCurrentLevelByPlayer(event.getPlayer()) + 1
            ));

            CoreProvider.Redis.ECHO.provide().publish(
                    new BroadcastMessagePacket(
                            TextComponent.fromLegacyText(text),
                            Sets.newHashSet(Group.DEFAULT),
                            false,
                            CoreProvider.getApp().getServer()
                    )
            );
        }
    }

}
