package net.hyze.factions.framework.echo.packets.permission;

import dev.utils.echo.buffer.EchoByteBufferInput;
import dev.utils.echo.buffer.EchoByteBufferOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.hyze.core.shared.echo.api.ServerEchoPacket;
import net.hyze.factions.framework.echo.FactionsPacketSerializeUtil;
import net.hyze.factions.framework.faction.Faction;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FactionUserPermissionUpdatedPacket extends ServerEchoPacket {

    public Faction faction;
    public Integer userId;
    public int value;

    @Override
    public void write(EchoByteBufferOutput buffer) {
        FactionsPacketSerializeUtil.writeFaction(buffer, this.faction);
        buffer.writeInt(userId);
        buffer.writeInt(this.value);
    }

    @Override
    public void read(EchoByteBufferInput buffer) {
        this.faction = FactionsPacketSerializeUtil.readFaction(buffer);
        this.userId = buffer.readInt();
        this.value = buffer.readInt();
    }
}
