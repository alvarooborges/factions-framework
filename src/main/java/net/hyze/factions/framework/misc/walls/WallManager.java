package net.hyze.factions.framework.misc.walls;

import com.google.common.collect.Sets;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.MCEditSchematicFormat;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.hyze.core.spigot.misc.utils.TreeUtil;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.util.LongHash;
import org.bukkit.material.MaterialData;

import java.io.IOException;
import java.util.Set;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WallManager {

    public static Boolean GENERATING = false;
    public static String WORLD = "world";

    private static final Set<Long> CHUNK_CACHE = Sets.newHashSet();

    public synchronized static void build(Chunk chunk, WallSchematic schem, WallOrientation position, MaterialData base) {

        System.out.println("Wall debug");

        World world = chunk.getWorld();

        long chunkLong = LongHash.toLong(chunk.getX(), chunk.getZ());

        if (CHUNK_CACHE.contains(chunkLong)) {
            return;
        }

        CHUNK_CACHE.add(chunkLong);

        int minX = chunk.getX() << 4;
        int minZ = chunk.getZ() << 4;

        if (world.getBlockAt(minX, 0, minZ).getType() == Material.DIRT) {
            return;
        }

        Function<Block, Boolean> isValidGround = block -> {
            boolean out = !block.getType().isTransparent() && !block.getType().isFlammable();

            return out;
        };

        try {
            WorldEditPlugin worldEditPlugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
            EditSession session = worldEditPlugin.getWorldEdit().getEditSessionFactory().getEditSession(new BukkitWorld(world), 10000);

            CuboidClipboard clipboard = MCEditSchematicFormat.getFormat(schem.getFile()).load(schem.getFile());
            clipboard.rotate2D(position.getAngle());
            clipboard.setOrigin(Vector.ZERO);
            clipboard.setOffset(position.getOffset());

            int sumY = 0;
            int totalY = 0;

            for (int x = minX; x < minX + clipboard.getSize().getBlockX(); x++) {
                for (int z = minZ; z < minZ + clipboard.getSize().getBlockZ(); z++) {

                    Block highestBlock = world.getHighestBlockAt(x, z);

                    if (isValidGround.apply(highestBlock)) {
                        sumY += highestBlock.getY();
                        totalY++;
                    } else {

                        for (int j = 0; j < 10; j++) {
                            highestBlock = highestBlock.getRelative(BlockFace.DOWN);

                            if (isValidGround.apply(highestBlock)) {
                                sumY += highestBlock.getY();
                                totalY++;
                                break;
                            }
                        }
                    }
                }
            }

            int avgY = Math.round((float) sumY / (float) totalY);

            try {
                for (int x = minX; x <= minX + 15; x++) {
                    for (int z = minZ; z <= minZ + 15; z++) {
                        for (int y = avgY; y < avgY + 10; y++) {
                            TreeUtil.removeTree(world.getBlockAt(x, y, z));
                        }
                    }
                }
            } catch (StackOverflowError error) {

            }

            for (int x = minX; x <= minX + 15; x++) {
                for (int z = minZ; z <= minZ + 15; z++) {
                    for (int y = avgY; y < avgY + 10; y++) {
                        Block block = world.getBlockAt(x, y, z);

                        block.setType(Material.AIR);
                    }
                }
            }

            for (int x = minX; x <= minX + 15; x++) {
                for (int z = minZ; z <= minZ + 15; z++) {
                    for (int y = avgY; y > avgY - 10; y--) {
                        Block block = world.getBlockAt(x, y, z);

                        block.setType(base.getItemType());
                        block.setData(base.getData());
                    }
                }
            }

            clipboard.paste(session, new Vector(minX, avgY + 1, minZ), false);

            world.getBlockAt(minX, 0, minZ).setType(Material.DIRT);
        } catch (IOException | DataException | MaxChangedBlocksException e) {
            e.printStackTrace();
        }
    }
}
