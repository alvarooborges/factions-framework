package net.hyze.factions.framework.commands.factioncommand.subcommands.permissionsubcommand;

import lombok.Getter;
import net.hyze.factions.framework.commands.factioncommand.FactionSubCommand;
import net.hyze.factions.framework.commands.factioncommand.subcommands.permissionsubcommand.inventories.IndexInventory;
import net.hyze.factions.framework.faction.relation.user.FactionRole;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.entity.Player;

public class PermissionSubCommand extends FactionSubCommand {

    @Getter
    private final CommandRelationType commandRelationType = CommandRelationType.FACTION_TO_ADM;

    public PermissionSubCommand() {
        super("perm", FactionRole.LEADER, "permicoes", "permições");
    }

    @Override
    public void onCommand(Player player, FactionUser user, FactionUserRelation relation, String[] args) {
        player.openInventory(new IndexInventory(user, relation.getFaction()));
    }
}
