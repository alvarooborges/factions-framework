package net.hyze.factions.framework.commands.factioncommand.subcommands;

import net.hyze.factions.framework.commands.factioncommand.FactionSubCommand;
import net.hyze.factions.framework.faction.relation.user.FactionRole;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.menu.inventories.DefaultIndexInventory;
import net.hyze.factions.framework.menu.inventories.LeaderIndexInventory;
import net.hyze.factions.framework.menu.inventories.MemberIndexInventory;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.entity.Player;

public class MenuSubCommand extends FactionSubCommand {

    public MenuSubCommand() {
        super("menu");
    }

    @Override
    public void onCommand(Player player, FactionUser user, FactionUserRelation relation, String[] args) {

        if (relation == null) {
            player.openInventory(new DefaultIndexInventory(user));
            return;
        }

        if (relation.getRole() == FactionRole.LEADER) {
            player.openInventory(new LeaderIndexInventory(user));
            return;
        }
        
        player.openInventory(new MemberIndexInventory(user));
    }
}
