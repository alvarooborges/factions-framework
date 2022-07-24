package net.hyze.factions.framework.commands.factioncommand.subcommands.pointssubcommand.data;

import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.commands.argument.Argument;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.echo.packets.FactionSeasonPointsUpdatePacket;
import net.hyze.factions.framework.faction.Faction;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class SetPointsSubCommand extends CustomCommand implements GroupCommandRestrictable {

    public SetPointsSubCommand() {
        super("set", CommandRestriction.IN_GAME);

        registerArgument(new Argument("TAG", "Tag da facção.", true));
        registerArgument(new Argument("valor", "Pontos que deseja setar.", true));
    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {

        String tag = args[0].toUpperCase();

        Faction faction = FactionsProvider.Cache.Local.FACTIONS.provide().get(tag);

        if (faction == null) {
            Message.ERROR.send(sender, String.format("Ops, a facção %s não existe.", tag));
            return;
        }

        Integer points = Ints.tryParse(args[1]);

        if (points == null) {
            return;
        }

        faction.setPoints(points);
        FactionsProvider.Repositories.FACTIONS.provide().update(faction);

        Message.SUCCESS.send(sender, String.format("Agora a facção %s possui %s pontos.", tag, faction.getPoints()));

        Map<Integer, Integer> map = Maps.newHashMap();
        map.put(faction.getId(), faction.getPoints());

        CoreProvider.Redis.ECHO.provide().publish(new FactionSeasonPointsUpdatePacket(map));

    }
}
