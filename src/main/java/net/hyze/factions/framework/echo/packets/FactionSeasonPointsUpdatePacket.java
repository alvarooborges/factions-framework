package net.hyze.factions.framework.echo.packets;

import com.google.common.collect.Maps;
import dev.utils.echo.buffer.EchoByteBufferInput;
import dev.utils.echo.buffer.EchoByteBufferOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.hyze.core.shared.echo.api.ServerEchoPacket;

import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FactionSeasonPointsUpdatePacket extends ServerEchoPacket {

    private Map<Integer, Integer> points;

    @Override
    public void write(EchoByteBufferOutput buffer) {

        buffer.writeInt(this.points.size());

        this.points.entrySet().forEach(entry -> {
            buffer.writeInt(entry.getKey());
            buffer.writeInt(entry.getValue());
        });

    }

    @Override
    public void read(EchoByteBufferInput buffer) {
        this.points = Maps.newHashMap();

        int size = buffer.readInt();

        for (int i = 0; i < size; i++) {
            this.points.put(buffer.readInt(), buffer.readInt());
        }
    }

}
