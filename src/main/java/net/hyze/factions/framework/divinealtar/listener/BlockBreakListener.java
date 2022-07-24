package net.hyze.factions.framework.divinealtar.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void on(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (block.getType() != Material.ENDER_PORTAL_FRAME) {
            return;
        }

        event.setCancelled(true);
    }

}
