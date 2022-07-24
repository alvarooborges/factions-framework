package net.hyze.factions.framework.echo.packets.permission;

import dev.utils.echo.BufferIO;
import dev.utils.echo.buffer.EchoByteBufferInput;
import dev.utils.echo.buffer.EchoByteBufferOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.hyze.core.shared.echo.api.ServerEchoPacket;
import net.hyze.factions.framework.echo.FactionsPacketSerializeUtil;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.relation.user.FactionRole;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FactionRolePermissionUpdatedPacket extends ServerEchoPacket {

    public Faction faction;
    public FactionRole role;
    public int value;

    @Override
    public void write(EchoByteBufferOutput buffer) {
        FactionsPacketSerializeUtil.writeFaction(buffer, this.faction);
        BufferIO.writeEnum(buffer, this.role);
        buffer.writeInt(this.value);
    }

    @Override
    public void read(EchoByteBufferInput buffer) {
        this.faction = FactionsPacketSerializeUtil.readFaction(buffer);
        this.role = BufferIO.readEnum(buffer, FactionRole.class);
        this.value = buffer.readInt();
    }
}
