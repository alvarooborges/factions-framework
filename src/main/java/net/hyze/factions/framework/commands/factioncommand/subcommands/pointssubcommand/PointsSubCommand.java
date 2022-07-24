package net.hyze.factions.framework.commands.factioncommand.subcommands.pointssubcommand;

import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.factions.framework.commands.factioncommand.subcommands.pointssubcommand.data.AddPointsSubCommand;
import net.hyze.factions.framework.commands.factioncommand.subcommands.pointssubcommand.data.InfoPointsSubCommand;
import net.hyze.factions.framework.commands.factioncommand.subcommands.pointssubcommand.data.RemovePointsSubCommand;
import net.hyze.factions.framework.commands.factioncommand.subcommands.pointssubcommand.data.SetPointsSubCommand;

public class PointsSubCommand extends CustomCommand implements GroupCommandRestrictable {

    public PointsSubCommand() {
        super("pontos", CommandRestriction.IN_GAME);
        
        registerSubCommand(new SetPointsSubCommand());
        registerSubCommand(new AddPointsSubCommand());
        registerSubCommand(new RemovePointsSubCommand());
        registerSubCommand(new InfoPointsSubCommand());
    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }

}
