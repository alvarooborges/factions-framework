package net.hyze.factions.framework.echo.packets;

import dev.utils.echo.BufferIO;
import dev.utils.echo.buffer.EchoByteBufferInput;
import dev.utils.echo.buffer.EchoByteBufferOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.hyze.core.shared.echo.api.ServerEchoPacket;
import net.hyze.factions.framework.echo.FactionsPacketSerializeUtil;
import net.hyze.factions.framework.faction.Faction;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserLeftFactionPacket extends ServerEchoPacket {

    private Faction faction;
    private Integer userId;
    private Reason reason;

    @Override
    public void write(EchoByteBufferOutput buffer) {
        FactionsPacketSerializeUtil.writeFaction(buffer, this.faction);
        buffer.writeInt(this.userId);
        BufferIO.writeEnum(buffer, this.reason);
    }

    @Override
    public void read(EchoByteBufferInput buffer) {
        this.faction = FactionsPacketSerializeUtil.readFaction(buffer);
        this.userId = buffer.readInt();
        this.reason = BufferIO.readEnum(buffer, Reason.class);
    }

    public enum Reason {
        LEAVE, KICK, DISBAND;
    }
}
