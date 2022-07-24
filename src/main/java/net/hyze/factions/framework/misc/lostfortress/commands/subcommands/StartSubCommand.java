package net.hyze.factions.framework.misc.lostfortress.commands.subcommands;

import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.misc.utils.Pair;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.misc.lostfortress.LostFortress;
import net.hyze.factions.framework.misc.lostfortress.LostFortressConstants;
import org.bukkit.command.CommandSender;

public class StartSubCommand extends CustomCommand implements GroupCommandRestrictable {

    public StartSubCommand() {
        super("start", CommandRestriction.IN_GAME);
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {

        if (!AppType.FACTIONS_LOSTFORTRESS.isCurrent()) {
            Message.ERROR.send(sender, "Digite este comando apenas no servidor da Base Perdida!");
            return;
        }

        if (LostFortressConstants.CURRENT_ID != null) {
            Message.ERROR.send(sender, "Ops, já existe um evento ocorrendo com o id: &f" + LostFortressConstants.CURRENT_ID);
            return;
        }

        FactionsProvider.Repositories.LOST_FORTRESS.provide().insert(CoreConstants.GSON.toJson(new LostFortress()));

        /**
         * Tá feito assim pq eu estou testando o sistema.
         */
        Pair<Integer, String> pair = FactionsProvider.Repositories.LOST_FORTRESS.provide().fetch();

        if (pair == null) {
            Message.ERROR.send(sender, "Algo deu errado.");
            return;
        }

        LostFortressConstants.CURRENT_ID = pair.getLeft();
        LostFortressConstants.CURRENT = CoreConstants.GSON.fromJson(pair.getRight(), LostFortress.class);

        Message.SUCCESS.send(sender, "Feito!");

    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }

}
