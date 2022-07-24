package net.hyze.factions.framework.commands.factioncommand.subcommands.setspawnsubcommand;

import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.commands.factioncommand.FactionSubCommand;
import net.hyze.factions.framework.commands.factioncommand.subcommands.setspawnsubcommand.inventories.IndexInventory;
import net.hyze.factions.framework.faction.permission.FactionPermission;
import net.hyze.factions.framework.faction.relation.user.FactionRole;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.entity.Player;

public class SetSpawnSubCommand extends FactionSubCommand {

    public SetSpawnSubCommand() {
        super("setspawn", FactionRole.RECRUIT);
    }

    @Override
    public void onCommand(Player player, FactionUser user, FactionUserRelation relation, String[] args) {

        if (!FactionPermission.COMMAND_SPAWNER_SET_SPAWN.allows(relation.getFaction(), user)) {
            Message.ERROR.send(player, "Você não tem permissão para definir pontos de spawn para sua facção.");
            return;
        }

        player.openInventory(new IndexInventory(user));
    }
}
