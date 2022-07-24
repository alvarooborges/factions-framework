package net.hyze.factions.framework.commands;

import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import org.bukkit.command.CommandSender;

public class CallendarCommand extends CustomCommand implements GroupCommandRestrictable {

    public CallendarCommand() {
        super("calendario", CommandRestriction.IN_GAME);
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {

        Message.SUCCESS.send(sender, "Abrindo calendário...");

    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }

}
