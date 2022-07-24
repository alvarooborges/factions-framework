package net.hyze.factions.framework.echo.packets;

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
public class FactionDisbandPacket extends ServerEchoPacket {

    private Faction faction;

    @Override
    public void write(EchoByteBufferOutput buffer) {
        FactionsPacketSerializeUtil.writeFaction(buffer, this.faction);
    }

    @Override
    public void read(EchoByteBufferInput buffer) {
        this.faction = FactionsPacketSerializeUtil.readFaction(buffer);
    }
}
