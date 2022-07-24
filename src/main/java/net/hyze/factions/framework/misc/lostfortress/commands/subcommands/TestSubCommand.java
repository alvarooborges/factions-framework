package net.hyze.factions.framework.misc.lostfortress.commands.subcommands;

import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.factions.framework.misc.lostfortress.LostFortressConstants;
import org.bukkit.command.CommandSender;

public class TestSubCommand extends CustomCommand implements GroupCommandRestrictable {

    public TestSubCommand() {
        super("test", CommandRestriction.IN_GAME);
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {
        System.out.println(CoreConstants.GSON.toJson(LostFortressConstants.CURRENT));
    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }

}
