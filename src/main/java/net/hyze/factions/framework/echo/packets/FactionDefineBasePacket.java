package net.hyze.factions.framework.echo.packets;

import dev.utils.echo.buffer.EchoByteBufferInput;
import dev.utils.echo.buffer.EchoByteBufferOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.hyze.core.shared.echo.api.HyzeBufferIO;
import net.hyze.core.shared.echo.api.ServerEchoPacket;
import net.hyze.core.shared.world.location.SerializedLocation;
import net.hyze.factions.framework.echo.FactionsPacketSerializeUtil;
import net.hyze.factions.framework.faction.Faction;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FactionDefineBasePacket extends ServerEchoPacket {

    private Faction faction;
    private Integer userId;
    private SerializedLocation home;

    @Override
    public void write(EchoByteBufferOutput buffer) {
        FactionsPacketSerializeUtil.writeFaction(buffer, this.faction);
        buffer.writeInt(userId);
        HyzeBufferIO.writeSerializedLocation(buffer, this.home);
    }

    @Override
    public void read(EchoByteBufferInput buffer) {
        this.faction = FactionsPacketSerializeUtil.readFaction(buffer);
        this.userId = buffer.readInt();
        this.home = HyzeBufferIO.readSerializedLocation(buffer);
    }
}
