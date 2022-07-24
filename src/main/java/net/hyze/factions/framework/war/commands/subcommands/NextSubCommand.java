package net.hyze.factions.framework.war.commands.subcommands;

import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.war.War;
import org.bukkit.command.CommandSender;

public class NextSubCommand extends CustomCommand implements GroupCommandRestrictable {

    public NextSubCommand() {
        super("next");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if (War.CLOCK.getCurrentEnumWarPhase() == null) {
            Message.ERROR.send(sender, "O evento não está ocorrendo.");
            return;
        }

        Message.SUCCESS.send(sender, "Avançado fase...");
        War.CLOCK.next();

    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }

}
