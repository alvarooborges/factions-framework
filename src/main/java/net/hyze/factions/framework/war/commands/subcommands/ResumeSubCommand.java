package net.hyze.factions.framework.war.commands.subcommands;

import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.war.War;
import org.bukkit.command.CommandSender;

public class ResumeSubCommand extends CustomCommand implements GroupCommandRestrictable {

    public ResumeSubCommand() {
        super("resume");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if (War.CONFIG == null) {
            Message.ERROR.send(sender, "Este não é o servidor da guerra.");
            return;
        }

        if (War.CLOCK.getCurrentEnumWarPhase() == null) {
            Message.ERROR.send(sender, "O evento não está ocorrendo.");
            return;
        }

        War.PAUSE = false;

        Message.SUCCESS.send(sender, "Guerra religada!");

    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }

}
