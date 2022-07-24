package net.hyze.factions.framework.listeners;

import net.hyze.obsidiandestroyer.events.GetMaxDurabilityEvent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ObsidianDestroyerListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(GetMaxDurabilityEvent event) {
        Block block = event.getLocation().getBlock();
        if (block.getType() == Material.BEDROCK) {
            BlockFace[] faces = {
                BlockFace.UP,
                BlockFace.DOWN,
                BlockFace.NORTH,
                BlockFace.SOUTH,
                BlockFace.WEST,
                BlockFace.EAST,};

            for (BlockFace face : faces) {
                Block side = block.getRelative(face);

                if (side.getType() == Material.MOB_SPAWNER) {
                    event.setMaxDurability(1);
                    return;
                }
            }
        }
    }
}
