package net.hyze.factions.framework.echo.packets;

import dev.utils.echo.buffer.EchoByteBufferInput;
import dev.utils.echo.buffer.EchoByteBufferOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.hyze.core.shared.echo.api.ServerEchoPacket;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LostFortressTogglePacket extends ServerEchoPacket {

    private Boolean status;

    @Override
    public void write(EchoByteBufferOutput buffer) {
        buffer.writeBoolean(status);
    }

    @Override
    public void read(EchoByteBufferInput buffer) {
        this.status = buffer.readBoolean();
    }

}
