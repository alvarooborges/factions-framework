package net.hyze.factions.framework.echo.packets;

import dev.utils.echo.BufferIO;
import dev.utils.echo.buffer.EchoByteBufferInput;
import dev.utils.echo.buffer.EchoByteBufferOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.hyze.core.shared.echo.api.ServerEchoPacket;
import net.hyze.factions.framework.echo.FactionsPacketSerializeUtil;
import net.hyze.factions.framework.lands.Claim;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ClaimUpdatePacket extends ServerEchoPacket {

    private Integer userId;
    private Claim claim;
    private Reason reason;

    @Override
    public void write(EchoByteBufferOutput buffer) {
        buffer.writeInt(userId);
        FactionsPacketSerializeUtil.writeClaim(buffer, claim);
        BufferIO.writeEnum(buffer, reason);
    }

    @Override
    public void read(EchoByteBufferInput buffer) {
        userId = buffer.readInt();
        claim = FactionsPacketSerializeUtil.readClaim(buffer);
        reason = BufferIO.readEnum(buffer, Reason.class);
    }

    public static enum Reason {
        CLAIM, UNCLAIM, CONTEST;
    }
}
