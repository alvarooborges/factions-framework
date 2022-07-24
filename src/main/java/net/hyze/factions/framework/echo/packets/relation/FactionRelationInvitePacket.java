package net.hyze.factions.framework.echo.packets.relation;

import dev.utils.echo.BufferIO;
import dev.utils.echo.buffer.EchoByteBufferInput;
import dev.utils.echo.buffer.EchoByteBufferOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.hyze.core.shared.echo.api.ServerEchoPacket;
import net.hyze.factions.framework.echo.FactionsPacketSerializeUtil;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.relation.faction.FactionRelation;

@NoArgsConstructor
@AllArgsConstructor
public class FactionRelationInvitePacket extends ServerEchoPacket {

    @Getter
    private Faction sender;

    @Getter
    private Faction target;

    @Getter
    private FactionRelation.Type type;

    @Override
    public void write(EchoByteBufferOutput buffer) {
        FactionsPacketSerializeUtil.writeFaction(buffer, this.sender);
        FactionsPacketSerializeUtil.writeFaction(buffer, this.target);
        BufferIO.writeEnum(buffer, this.type);
    }

    @Override
    public void read(EchoByteBufferInput buffer) {
        this.sender = FactionsPacketSerializeUtil.readFaction(buffer);
        this.target = FactionsPacketSerializeUtil.readFaction(buffer);
        this.type = BufferIO.readEnum(buffer, FactionRelation.Type.class);
    }
}
