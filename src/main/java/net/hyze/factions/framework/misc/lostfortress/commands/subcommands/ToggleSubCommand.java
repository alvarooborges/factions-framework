package net.hyze.factions.framework.misc.lostfortress.commands.subcommands;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.echo.packets.LostFortressTogglePacket;
import net.hyze.factions.framework.misc.lostfortress.LostFortressConstants;
import org.bukkit.command.CommandSender;

public class ToggleSubCommand extends CustomCommand implements GroupCommandRestrictable {

    public ToggleSubCommand() {
        super("toggle", CommandRestriction.IN_GAME);
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {
        if (!AppType.FACTIONS_LOSTFORTRESS.isCurrent()) {
            Message.ERROR.send(sender, "Digite este comando apenas no servidor da Base Perdida!");
            return;
        }

        LostFortressConstants.STATUS = !LostFortressConstants.STATUS;

        CoreProvider.Redis.ECHO.provide().publish(new LostFortressTogglePacket(LostFortressConstants.STATUS));

        Message.SUCCESS.send(sender, "Feito: &f" + LostFortressConstants.STATUS);
    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }

}
