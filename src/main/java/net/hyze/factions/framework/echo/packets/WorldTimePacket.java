package net.hyze.factions.framework.echo.packets;

import dev.utils.echo.buffer.EchoByteBufferInput;
import dev.utils.echo.buffer.EchoByteBufferOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.hyze.core.shared.echo.api.ServerEchoPacket;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WorldTimePacket extends ServerEchoPacket {

    private long time;

    @Override
    public void write(EchoByteBufferOutput buffer) {
        buffer.writeLong(this.time);
    }

    @Override
    public void read(EchoByteBufferInput buffer) {
        this.time = buffer.readLong();
    }
}
