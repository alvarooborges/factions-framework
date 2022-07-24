package net.hyze.factions.framework.echo.packets;

import dev.utils.echo.buffer.EchoByteBufferInput;
import dev.utils.echo.buffer.EchoByteBufferOutput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.echo.api.ServerEchoPacket;

import java.util.Date;

@Getter
@NoArgsConstructor
@RequiredArgsConstructor
public class FactionUnderAttackPacket extends ServerEchoPacket {

    @NonNull
    private Integer factionId;

    @NonNull
    private Date underAttackAt;

    @NonNull
    private int chunkX;

    @NonNull
    private int chunkZ;

    @Override
    public void write(EchoByteBufferOutput buffer) {
        buffer.writeInt(this.factionId);
        buffer.writeLong(this.underAttackAt.getTime());
        buffer.writeInt(this.chunkX);
        buffer.writeInt(this.chunkZ);
    }

    @Override
    public void read(EchoByteBufferInput buffer) {
        this.factionId = buffer.readInt();
        this.underAttackAt = new Date(buffer.readLong());
        this.chunkX = buffer.readInt();
        this.chunkZ = buffer.readInt();
    }
}
