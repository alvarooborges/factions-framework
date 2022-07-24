package net.hyze.factions.framework.misc.playerheads.io;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import net.hyze.core.spigot.misc.io.chunk.ChunkWrapperFileStorage;
import net.hyze.factions.framework.misc.playerheads.PlayerHeadInfo;

import java.io.File;

public class PlayerHeadsFileStorage  extends ChunkWrapperFileStorage<PlayerHeadInfo> {

    public PlayerHeadsFileStorage(int x, int z, String worldName, File directory) {
        super(x, z, worldName, directory);
    }

    @Override
    protected void writeData(PlayerHeadInfo data, ByteArrayDataOutput byteArray) {
        byteArray.writeUTF(data.getOwner());
        byteArray.writeUTF(data.getKiller());
        byteArray.writeLong(data.getAt());
    }

    @Override
    protected PlayerHeadInfo readData(ByteArrayDataInput byteArray) {
        String owner = byteArray.readUTF();
        String killer = byteArray.readUTF();
        long at = byteArray.readLong();

        return new PlayerHeadInfo(owner, killer, at);
    }
}
