package net.hyze.factions.framework.commands;

import com.google.common.primitives.Ints;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.commands.argument.Argument;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import org.bukkit.command.CommandSender;

public class SleepCommand extends CustomCommand implements GroupCommandRestrictable {

    public SleepCommand() {
        super("sleep", CommandRestriction.CONSOLE_AND_IN_GAME);

        registerArgument(new Argument("tempo", "Tempo em millisegundos."));
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {

        Integer millis = Ints.tryParse(args[0]);

        if (millis == null) {
            return;
        }

        if (!AppType.FACTIONS_TESTS.isCurrent()) {
            Message.ERROR.send(sender, "Não.");
            return;
        }

        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
        }

    }

    @Override
    public Group getGroup() {
        return Group.MANAGER;
    }
}
