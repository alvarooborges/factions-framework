package net.hyze.factions.framework.misc.playerheads;

import com.google.common.collect.Maps;
import net.hyze.core.spigot.misc.io.chunk.ChunkWrapperFileStorage;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.misc.playerheads.io.PlayerHeadsFileStorage;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.util.LongHash;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Map;

public class PlayerHeadsListeners implements Listener {

    private final Plugin plugin;
    private final File storageDirectory;
    private final Map<Long, PlayerHeadsFileStorage> chunks = Maps.newConcurrentMap();

    public PlayerHeadsListeners(Plugin plugin) {
        this.plugin = plugin;

        this.storageDirectory = new File(this.plugin.getDataFolder(), "data" + File.separator + "player_heads");

        if (!this.storageDirectory.exists()) {
            this.storageDirectory.mkdirs();
        }
    }

    private PlayerHeadsFileStorage getChunkWrapper(Chunk chunk) {
        long hash = LongHash.toLong(chunk.getX(), chunk.getZ());

        if (!this.chunks.containsKey(hash)) {
            this.loadChunk(chunk);
        }

        return this.chunks.get(hash);
    }

    private void loadChunk(Chunk chunk) {
        long hash = LongHash.toLong(chunk.getX(), chunk.getZ());

        PlayerHeadsFileStorage wrapper = new PlayerHeadsFileStorage(
                chunk.getX(),
                chunk.getZ(),
                chunk.getWorld().getName(),
                this.storageDirectory
        );

        wrapper.load();

        this.chunks.put(hash, wrapper);
    }

    private void unloadChunk(Chunk chunk) {
        long hash = LongHash.toLong(chunk.getX(), chunk.getZ());

        ChunkWrapperFileStorage wrapper = this.chunks.get(hash);

        if (wrapper != null) {
            wrapper.save();
            this.chunks.remove(hash);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(ChunkLoadEvent event) {
//        loadChunk(event.getChunk());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(ChunkUnloadEvent event) {
//        unloadChunk(event.getChunk());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType() != Material.SKULL) {
            return;
        }

        ItemStack inHand = event.getItemInHand();

        ItemBuilder builder = ItemBuilder.of(inHand);

        if (builder.hasNbt("player_head:owner")) {
            event.setCancelled(true);
//            String owner = builder.nbtString("player_head:owner");
//            String killer = builder.nbtString("player_head:killer");
//            long droppedAt = builder.nbtLong("player_head:at");
//
//            PlayerHeadsFileStorage storage = getChunkWrapper(event.getBlockPlaced().getChunk());
//
//            storage.getMap().put(
//                    event.getBlockPlaced().getLocation().toVector(),
//                    new PlayerHeadInfo(owner, killer, droppedAt)
//            );
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(BlockBreakEvent event) {
//        if (event.getBlock().getType() != Material.SKULL) {
//            return;
//        }
//
//        PlayerHeadsFileStorage storage = getChunkWrapper(event.getBlock().getChunk());
//
//        PlayerHeadInfo info;
//        if ((info = storage.getMap().get(event.getBlock().getLocation().toVector())) != null) {
//            event.setCancelled(true);
//            ItemStack drop = PlayerHeadsUtils.make(info.getOwner(), info.getKiller(), info.getAt());
//            event.getBlock().setType(Material.AIR);
//            event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), drop);
//        }
    }
}
