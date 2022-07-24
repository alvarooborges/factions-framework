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
public class UserAdditionalMaxPowerUpdatedPacket extends ServerEchoPacket {

    private Integer userId;
    private int oldAdditionalMaxPower;
    private int newAdditionalMaxPower;

    @Override
    public void write(EchoByteBufferOutput buffer) {
        buffer.writeInt(userId);
        buffer.writeInt(this.oldAdditionalMaxPower);
        buffer.writeInt(this.newAdditionalMaxPower);
    }

    @Override
    public void read(EchoByteBufferInput buffer) {
        this.userId = buffer.readInt();
        this.oldAdditionalMaxPower = buffer.readInt();
        this.newAdditionalMaxPower = buffer.readInt();
    }
}
