package net.hyze.factions.framework.echo.packets;

import com.google.common.collect.Maps;
import dev.utils.echo.BufferIO;
import dev.utils.echo.buffer.EchoByteBufferInput;
import dev.utils.echo.buffer.EchoByteBufferOutput;
import dev.utils.echo.packet.AbstractEchoPacketResponse;
import dev.utils.echo.packet.IEchoRespondable;
import lombok.*;
import net.hyze.core.shared.echo.api.ServerEchoPacket;
import net.hyze.factions.framework.echo.FactionsPacketSerializeUtil;
import net.hyze.factions.framework.echo.packets.FactionRemovePlacedSpawnersRequest.FactionRemovePlacedSpawnersResponse;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.spawners.SpawnerType;

import java.util.Map;

@NoArgsConstructor
@RequiredArgsConstructor
public class FactionRemovePlacedSpawnersRequest extends ServerEchoPacket implements IEchoRespondable<FactionRemovePlacedSpawnersResponse> {

    @Getter
    @Setter
    private FactionRemovePlacedSpawnersResponse response;

    @NonNull
    @Getter
    private Faction faction;

    @NonNull
    @Getter
    private Integer userId;

    @Override
    public void write(EchoByteBufferOutput buffer) {
        FactionsPacketSerializeUtil.writeFaction(buffer, this.faction);
        buffer.writeInt(userId);
    }

    @Override
    public void read(EchoByteBufferInput buffer) {
        this.faction = FactionsPacketSerializeUtil.readFaction(buffer);
        this.userId = buffer.readInt();
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class FactionRemovePlacedSpawnersResponse extends AbstractEchoPacketResponse {

        @Getter
        private Map<SpawnerType, Integer> amount = Maps.newHashMap();

        @Override
        public void write(EchoByteBufferOutput buffer) {
            buffer.writeInt(this.amount.size());

            this.amount.forEach((type, amount) -> {
                BufferIO.writeEnum(buffer, type);
                buffer.writeInt(amount);
            });
        }

        @Override
        public void read(EchoByteBufferInput buffer) {
            this.amount = Maps.newHashMap();

            int size = buffer.readInt();
            for (int i = 0; i < size; i++) {
                this.amount.put(BufferIO.readEnum(buffer, SpawnerType.class), buffer.readInt());
            }
        }
    }
}
