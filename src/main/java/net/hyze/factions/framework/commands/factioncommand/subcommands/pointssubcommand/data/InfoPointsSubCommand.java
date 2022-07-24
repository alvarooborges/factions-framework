package net.hyze.factions.framework.commands.factioncommand.subcommands.pointssubcommand.data;

import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.commands.argument.Argument;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;
import org.bukkit.command.CommandSender;

public class InfoPointsSubCommand extends CustomCommand implements GroupCommandRestrictable {

    public InfoPointsSubCommand() {
        super("info", CommandRestriction.IN_GAME);

        registerArgument(new Argument("TAG", "Tag da facção.", true));
    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {
        String tag = args[0].toUpperCase();

        Faction faction = FactionsProvider.Cache.Local.FACTIONS.provide().get(tag);
        Message.SUCCESS.send(sender, String.format("A facção %s possui %s pontos.", tag, faction.getPoints()));
    }
}
