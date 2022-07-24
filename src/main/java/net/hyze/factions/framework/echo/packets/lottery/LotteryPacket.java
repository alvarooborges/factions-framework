package net.hyze.factions.framework.echo.packets.lottery;

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
public class LotteryPacket extends ServerEchoPacket {

    private Integer userId;

    @Override
    public void write(EchoByteBufferOutput buffer) {
        buffer.writeInt(userId);
    }

    @Override
    public void read(EchoByteBufferInput buffer) {
        userId = buffer.readInt();
    }

}
