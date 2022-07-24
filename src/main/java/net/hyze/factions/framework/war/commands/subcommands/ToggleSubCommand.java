package net.hyze.factions.framework.war.commands.subcommands;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.echo.packets.WarTogglePacket;
import net.hyze.factions.framework.war.War;
import org.bukkit.command.CommandSender;

public class ToggleSubCommand extends CustomCommand implements GroupCommandRestrictable {

    public ToggleSubCommand() {
        super("toggle");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if (War.CONFIG == null) {
            Message.ERROR.send(sender, "Este não é o servidor da guerra.");
            return;
        }

        boolean toggle = !War.OPEN;

        CoreProvider.Redis.ECHO.provide().publish(
                new WarTogglePacket(toggle)
        );

        Message.SUCCESS.send(sender, "Status do /guerra: &f" + toggle);
    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }

}
