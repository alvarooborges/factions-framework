package net.hyze.factions.framework.misc.supportblocks;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import dev.utils.shared.Printer;
import net.hyze.core.spigot.events.HyzePreStopEvent;
import net.hyze.core.spigot.misc.blockdrops.BlockDropsManager;
import net.hyze.core.spigot.misc.io.chunk.ChunkWrapperFileStorage;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.misc.supportblocks.io.SupportBlockFileStorage;
import net.hyze.obsidiandestroyer.events.DurabilityDamageEvent;
import net.hyze.obsidiandestroyer.events.GetMaxDurabilityEvent;
import net.hyze.obsidiandestroyer.events.GetResetTimeEvent;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.util.LongHash;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public final class SupportBlockManager implements Listener {

    private static SupportBlockManager instance;

    private final File storageDirectory;
    private final Map<Long, SupportBlockFileStorage> chunks = Maps.newConcurrentMap();
    private final Plugin plugin;

    public SupportBlockManager(Plugin plugin) {
        instance = this;

        this.plugin = plugin;

        this.storageDirectory = new File(this.plugin.getDataFolder(), "data" + File.separator + "block_protection");

        if (!this.storageDirectory.exists()) {
            this.storageDirectory.mkdirs();
        }
    }

    public Set<SupportBlock> getProtectionBlocks(Location location) {
        Set<SupportBlock> blocks = Sets.newHashSet();

        return blocks;
    }

    public void load() {
        this.chunks.clear();

        for (World world : this.plugin.getServer().getWorlds()) {
            for (Chunk chunk : world.getLoadedChunks()) {
                loadChunk(chunk);
            }
        }
    }

    public SupportBlockFileStorage getChunkWrapper(Chunk chunk) {
        long hash = LongHash.toLong(chunk.getX(), chunk.getZ());

        if (!this.chunks.containsKey(hash)) {
            this.loadChunk(chunk);
        }

        return this.chunks.get(hash);
    }

    public boolean containsBlock(Location location) {
        Chunk chunk = location.getChunk();

        ChunkWrapperFileStorage wrapper = getChunkWrapper(chunk);

        Vector vector = location.getBlock().getLocation().toVector();

        return wrapper.getMap().containsKey(vector);
    }

    public void addBlock(Location location, SupportBlock block) {
        Chunk chunk = location.getChunk();

        SupportBlockFileStorage wrapper = getChunkWrapper(chunk);

        Vector vector = location.getBlock().getLocation().toVector();

        wrapper.getMap().put(vector, block);
    }

    public SupportBlock removeBlock(Location location) {
        Chunk chunk = location.getChunk();

        SupportBlockFileStorage wrapper = getChunkWrapper(chunk);

        Vector vector = location.getBlock().getLocation().toVector();

        return wrapper.getMap().remove(vector);
    }

    public SupportBlock getBlock(Location location) {
        Chunk chunk = location.getChunk();

        SupportBlockFileStorage wrapper = getChunkWrapper(chunk);
        Vector vector = location.getBlock().getLocation().toVector();

        Block originalBlock = location.getBlock();
        SupportBlock block = wrapper.getMap().get(vector);

        if (block != null) {
            MaterialData data = block.getData();

            if (data.getItemType() != originalBlock.getType() || data.getData() != originalBlock.getData()) {
                removeBlock(location);

                return null;
            }
        }

        return block;
    }

    public void loadChunk(Chunk chunk) {
        long hash = LongHash.toLong(chunk.getX(), chunk.getZ());

        SupportBlockFileStorage wrapper = new SupportBlockFileStorage(
                chunk.getX(),
                chunk.getZ(),
                chunk.getWorld().getName(),
                this.storageDirectory
        );

        wrapper.load();

        this.chunks.put(hash, wrapper);
    }

    public void unloadChunk(Chunk chunk) {
        long hash = LongHash.toLong(chunk.getX(), chunk.getZ());

        ChunkWrapperFileStorage wrapper = this.chunks.get(hash);

        if (wrapper != null) {
            wrapper.save();
            this.chunks.remove(hash);
        }
    }

    public static synchronized SupportBlockManager getInstance() {
        return instance;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void on(BlockBreakEvent event) {

        if (SupportBlockManager.getInstance() == null) {
            return;
        }

        Location location = event.getBlock().getLocation();

        if (SupportBlockManager.getInstance().containsBlock(location)) {
            SupportBlock protectionBlock = SupportBlockManager.getInstance().getBlock(location);

            if (protectionBlock != null) {
                Block block = event.getBlock();
                Player player = event.getPlayer();

                Collection<ItemStack> collectionsOfDrops = BlockDropsManager.getDrops(block, player, player.getItemInHand());

                event.setCancelled(true);

                SupportBlockManager.getInstance().removeBlock(location);

                block.setType(Material.AIR);

                collectionsOfDrops.forEach((stack) -> {
                    location.getWorld().dropItemNaturally(location, stack);
                });

                Message.INFO.send(event.getPlayer(), "Você removeu um " + protectionBlock.getDisplayName() + ".");
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(ChunkLoadEvent event) {
        SupportBlockManager.getInstance().loadChunk(event.getChunk());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(ChunkUnloadEvent event) {
        SupportBlockManager.getInstance().unloadChunk(event.getChunk());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(HyzePreStopEvent event) {
        Printer.INFO.coloredPrint("&e[SupportBlock] PreStopEvent handler");
        this.chunks.values().forEach(SupportBlockFileStorage::save);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(GetMaxDurabilityEvent event) {
        Location location = event.getLocation();

        SupportBlock block = getBlock(location);

        if (block != null) {
            event.setMaxDurability(block.getMaxDurability());
            return;
        }

        if (event.getMaxDurability() == 0) {
            return;
        }

        Vector vec = SupportBlock.REINFORCEMENT.getArea();

        Location min = location.clone().subtract(vec.getBlockX(), vec.getBlockY(), vec.getBlockZ());
        Location max = location.clone().add(vec.getBlockX(), vec.getBlockY(), vec.getBlockZ());

        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    Location supportLocation = new Location(location.getWorld(), x, y, z);

                    if (supportLocation.getBlock().getType() != Material.SLIME_BLOCK) {
                        continue;
                    }

                    SupportBlock support = getBlock(supportLocation);

                    if (support != null && support == SupportBlock.REINFORCEMENT) {
                        double newMaxDurabiliry = event.getMaxDurability() * 1.5;

                        event.setMaxDurability((int) Math.round(newMaxDurabiliry));
                        return;
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(GetResetTimeEvent event) {
        Location location = event.getLocation();

        SupportBlock block = getBlock(location);

        if (block != null) {
            return;
        }

        Vector vec = SupportBlock.REGENERATION.getArea();

        Location min = location.clone().subtract(vec.getBlockX(), vec.getBlockY(), vec.getBlockZ());
        Location max = location.clone().add(vec.getBlockX(), vec.getBlockY(), vec.getBlockZ());

        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    Location supportLocation = new Location(location.getWorld(), x, y, z);

                    if (supportLocation.getBlock().getType() != Material.REDSTONE_BLOCK) {
                        continue;
                    }

                    SupportBlock support = getBlock(supportLocation);

                    if (support != null && support == SupportBlock.REGENERATION) {
                        event.setResetTime(10000);
                        return;
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void on(DurabilityDamageEvent event) {
        Location location = event.getBlock().getLocation();

        SupportBlock supportBlock = getBlock(location);

        if (supportBlock != null) {
            return;
        }

        Vector vec = SupportBlock.ABSORPTION.getArea();

        Location min = location.clone().subtract(vec.getBlockX(), vec.getBlockY(), vec.getBlockZ());
        Location max = location.clone().add(vec.getBlockX(), vec.getBlockY(), vec.getBlockZ());

        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {

                    Location supportLocation = new Location(location.getWorld(), x, y, z);

                    if (supportLocation.getBlock().getType() != Material.PRISMARINE) {
                        continue;
                    }

                    SupportBlock support = getBlock(supportLocation);

                    if (support != null && support == SupportBlock.ABSORPTION) {
                        double r = Math.random();
                        if (r <= 0.2) {
                            event.setCancelled(true);
                        }

                        return;
                    }
                }
            }
        }
    }

}
