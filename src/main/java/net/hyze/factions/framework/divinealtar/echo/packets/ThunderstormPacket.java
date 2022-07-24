package net.hyze.factions.framework.divinealtar.echo.packets;

import dev.utils.echo.buffer.EchoByteBufferInput;
import dev.utils.echo.buffer.EchoByteBufferOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.hyze.core.shared.echo.api.ServerEchoPacket;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ThunderstormPacket extends ServerEchoPacket {

    /**
     * Id da facção que enviou a chuva de raios.
     */
    private int factionSenderId;

    @Override
    public void write(EchoByteBufferOutput buffer) {
        buffer.writeInt(this.factionSenderId);
    }

    @Override
    public void read(EchoByteBufferInput buffer) {
        this.factionSenderId = buffer.readInt();
    }
    
}
