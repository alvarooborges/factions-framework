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
public class UserPowerUpdatedPacket extends ServerEchoPacket {

    private Integer userId;
    private int oldPower;
    private int newPower;

    @Override
    public void write(EchoByteBufferOutput buffer) {
        buffer.writeInt(this.userId);
        buffer.writeInt(this.oldPower);
        buffer.writeInt(this.newPower);
    }

    @Override
    public void read(EchoByteBufferInput buffer) {
        this.userId = buffer.readInt();
        this.oldPower = buffer.readInt();
        this.newPower = buffer.readInt();
    }
}
