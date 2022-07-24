package net.hyze.factions.framework.commands.factioncommand.subcommands.rankingsubcommand;

import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.commands.factioncommand.FactionSubCommand;
import net.hyze.factions.framework.commands.factioncommand.subcommands.rankingsubcommand.inventories.RankingInventory;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.entity.Player;

public class RankingSubCommand extends FactionSubCommand implements GroupCommandRestrictable {

    public RankingSubCommand() {
        super("rank", "ranking", "liga");
    }

    @Override
    public void onCommand(Player player, FactionUser user, FactionUserRelation relation, String[] args) {
        player.openInventory(new RankingInventory(user, 0));
    }

    @Override
    public Group getGroup() {
        if (FactionsProvider.getSettings().isAllowRankCommand()) {
            return Group.DEFAULT;
        }

        return Group.MANAGER;
    }
}
