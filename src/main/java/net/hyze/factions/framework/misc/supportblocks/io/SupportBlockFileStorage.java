package net.hyze.factions.framework.misc.supportblocks.io;

import com.google.common.base.Enums;
import com.google.common.collect.Sets;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import net.hyze.core.spigot.misc.io.chunk.ChunkWrapperFileStorage;
import net.hyze.factions.framework.misc.supportblocks.SupportBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.Objects;
import java.util.Set;

public class SupportBlockFileStorage extends ChunkWrapperFileStorage<SupportBlock> {

    public SupportBlockFileStorage(int x, int z, String worldName, File directory) {
        super(x, z, worldName, directory);
    }

    @Override
    protected void writeData(SupportBlock data, ByteArrayDataOutput byteArray) {
        byteArray.writeUTF(data.name());
    }

    @Override
    protected SupportBlock readData(ByteArrayDataInput byteArray) {
        String utf = byteArray.readUTF();

        return Enums.getIfPresent(SupportBlock.class, utf).orNull();
    }

    @Override
    public void save() {
        this.getMap().keySet().removeAll(this.findRemovedBlocks());

        super.save();
    }

    private Set<Vector> findRemovedBlocks() {
        Set<Vector> removed = Sets.newHashSet();

        this.getMap().forEach((key, value) -> {
            World world = Bukkit.getWorld(this.worldName);
            Location location = key.toLocation(world);

            MaterialData data = value.getData();
            MaterialData blockData = new MaterialData(location.getBlock().getType(), location.getBlock().getData());

            if (!Objects.equals(data, blockData)) {
                removed.add(key);
            }
        });

        return removed;
    }
}
