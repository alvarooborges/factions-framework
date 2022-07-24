package net.hyze.factions.framework.echo;

import dev.utils.echo.buffer.EchoByteBufferInput;
import dev.utils.echo.buffer.EchoByteBufferOutput;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.lands.Claim;

import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FactionsPacketSerializeUtil {

    public static void writeFaction(EchoByteBufferOutput buffer, Faction faction) {
        if (faction != null) {
            buffer.writeInt(faction.getId());
        } else {
            buffer.writeInt(-1);
        }
    }

    public static Faction readFaction(EchoByteBufferInput buffer) {
        int id = buffer.readInt();

        if (id > 0) {
            return FactionsProvider.Cache.Local.FACTIONS.provide().get(id);
        }

        return null;
    }

    public static void writeClaim(EchoByteBufferOutput buffer, Claim claim) {
        buffer.writeInt(claim.getFactionId());
        buffer.writeLong(claim.getCreatedAt().getTime());
        buffer.writeBoolean(claim.isTemporary());
        buffer.writeString(claim.getAppId());
        buffer.writeInt(claim.getChunkX());
        buffer.writeInt(claim.getChunkZ());

        boolean isContested = claim.isContested();

        buffer.writeBoolean(isContested);

        if (isContested) {
            buffer.writeInt(claim.getContestantId());
            buffer.writeLong(claim.getContestedAt().getTime());
        }
    }

    public static Claim readClaim(EchoByteBufferInput buffer) {
        Claim claim = new Claim(
                buffer.readInt(),
                new Date(buffer.readLong()),
                buffer.readBoolean(),
                buffer.readString(),
                buffer.readInt(),
                buffer.readInt()
        );

        if (buffer.readBoolean()) {
            claim.setContestantId(buffer.readInt());
            claim.setContestedAt(new Date(buffer.readLong()));
        }

        return claim;
    }
}
