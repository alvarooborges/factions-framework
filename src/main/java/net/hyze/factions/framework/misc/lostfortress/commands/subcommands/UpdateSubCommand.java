package net.hyze.factions.framework.misc.lostfortress.commands.subcommands;

import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.misc.lostfortress.LostFortressConstants;
import org.bukkit.command.CommandSender;

public class UpdateSubCommand extends CustomCommand implements GroupCommandRestrictable {

    public UpdateSubCommand() {
        super("update", CommandRestriction.IN_GAME);
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {

        if (!AppType.FACTIONS_LOSTFORTRESS.isCurrent()) {
            Message.ERROR.send(sender, "Digite este comando apenas no servidor da Base Perdida!");
            return;
        }

        if (LostFortressConstants.CURRENT_ID == null) {
            Message.ERROR.send(sender, "Ops, o evento não está ocorrendo.");
            return;
        }

        FactionsProvider.Repositories.LOST_FORTRESS.provide().update(
                LostFortressConstants.CURRENT_ID,
                CoreConstants.GSON.toJson(LostFortressConstants.CURRENT)
        );

        Message.SUCCESS.send(sender, "Feito!");

    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }

}