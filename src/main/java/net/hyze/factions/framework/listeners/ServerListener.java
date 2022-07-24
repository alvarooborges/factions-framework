package net.hyze.factions.framework.listeners;

import com.google.common.collect.ImmutableSet;
import net.hyze.core.spigot.CoreSpigotSettings;
import net.hyze.core.spigot.events.HyzePreStopCountdownEvent;
import net.hyze.core.spigot.misc.combat.CombatManager;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.user.FactionUser;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Set;

public class ServerListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPreStopEventLow(HyzePreStopCountdownEvent event) {

        if (event.getCurrentCountdown() <= 10) {

            Set<? extends Player> players = ImmutableSet.copyOf(Bukkit.getOnlinePlayers());

            for (Player player : players) {

                FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(player);

                CombatManager.untag(user.getHandle());

                player.closeInventory();

                if (event.getCurrentCountdown() <= 3) {
                    player.kickPlayer(ChatColor.RED + "Reiniciando...");
                }
            }

            if (event.getCurrentCountdown() == 9) {
                World world = Bukkit.getWorld(CoreSpigotSettings.getInstance().getWorldName());
                world.save();
            }
        }
    }

}
