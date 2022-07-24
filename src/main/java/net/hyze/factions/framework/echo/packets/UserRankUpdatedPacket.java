package net.hyze.factions.framework.echo.packets;

import dev.utils.echo.BufferIO;
import dev.utils.echo.buffer.EchoByteBufferInput;
import dev.utils.echo.buffer.EchoByteBufferOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.hyze.core.shared.echo.api.ServerEchoPacket;
import net.hyze.factions.framework.faction.relation.user.FactionRole;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRankUpdatedPacket extends ServerEchoPacket {

    private Integer userId;
    private Integer promoterId;
    private FactionRole oldRank;
    private FactionRole newRank;
    private Integer factionId;

    @Override
    public void write(EchoByteBufferOutput buffer) {
        buffer.writeInt(userId);
        buffer.writeInt(promoterId);
        BufferIO.writeEnum(buffer, this.oldRank);
        BufferIO.writeEnum(buffer, this.newRank);
        buffer.writeInt(factionId);
    }

    @Override
    public void read(EchoByteBufferInput buffer) {
        this.userId = buffer.readInt();
        this.promoterId = buffer.readInt();
        this.oldRank = BufferIO.readEnum(buffer, FactionRole.class);
        this.newRank = BufferIO.readEnum(buffer, FactionRole.class);
        this.factionId = buffer.readInt();
    }
}
