package net.hyze.factions.framework.echo.packets;

import dev.utils.echo.BufferIO;
import dev.utils.echo.buffer.EchoByteBufferInput;
import dev.utils.echo.buffer.EchoByteBufferOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.hyze.core.shared.echo.api.ServerEchoPacket;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserSpokePacket extends ServerEchoPacket {

    private Integer userId;
    private Chat chat;
    private String message;
    protected BaseComponent[] components;

    @Override
    public void write(EchoByteBufferOutput buffer) {
        buffer.writeInt(userId);
        BufferIO.writeEnum(buffer, chat);
        buffer.writeString(message);
        buffer.writeString(ComponentSerializer.toString(components));
    }

    @Override
    public void read(EchoByteBufferInput buffer) {
        userId = buffer.readInt();
        chat = BufferIO.readEnum(buffer, Chat.class);
        message = buffer.readString();
        components = ComponentSerializer.parse(buffer.readString());
    }

    public enum Chat {
        GLOBAL, FACTION, ALLIANCE;
    }
}
