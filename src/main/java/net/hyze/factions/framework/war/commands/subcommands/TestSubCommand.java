package net.hyze.factions.framework.war.commands.subcommands;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.echo.packets.WarTestTogglePacket;
import net.hyze.factions.framework.war.War;
import org.bukkit.command.CommandSender;

public class TestSubCommand extends CustomCommand implements GroupCommandRestrictable {

    public TestSubCommand() {
        super("test");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        War.TEST = !War.TEST;

        CoreProvider.Redis.ECHO.provide().publish(
                new WarTestTogglePacket(War.TEST)
        );
        
        Message.SUCCESS.send(sender, "Em modo de testes: &f" + War.TEST);

    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }

}
