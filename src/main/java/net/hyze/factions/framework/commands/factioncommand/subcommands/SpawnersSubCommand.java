package net.hyze.factions.framework.commands.factioncommand.subcommands;

import com.google.common.base.Objects;
import lombok.Getter;
import net.hyze.core.shared.group.Group;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.commands.factioncommand.FactionSubCommand;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.misc.utils.FactionDefaultMessage;
import net.hyze.factions.framework.spawners.inventories.IndexSpawnerInventory;
import net.hyze.factions.framework.spawners.inventories.ListPlacedSpawnersInventory;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.entity.Player;

import java.util.Optional;

public class SpawnersSubCommand extends FactionSubCommand {

    @Getter
    private final CommandRelationType commandRelationType = CommandRelationType.FACTION_TO_ADM;

    public SpawnersSubCommand() {
        super("geradores");
    }

    @Override
    public void onCommand(Player player, FactionUser user, FactionUserRelation relation, String[] args) {

        Faction userFaction = Optional.ofNullable(relation).map(FactionUserRelation::getFaction).orElse(null);
        Faction faction = userFaction;

        if (args.length > 0) {
            String arg1 = args[0];

            if (arg1.equalsIgnoreCase("listar")) {

                if (args.length > 1) {
                    String arg2 = args[1];
                    faction = FactionsProvider.Cache.Local.FACTIONS.provide().get(arg2);

                    if (faction == null) {
                        Message.ERROR.sendDefault(player, FactionDefaultMessage.FACTION_NOT_FOUND, arg1);
                        return;
                    }
                }

                if (faction == null) {
                    Message.ERROR.send(player, "Use /f geradores listar [tag]");
                    return;
                }

                player.openInventory(new ListPlacedSpawnersInventory(user, faction));
                return;
            }

            faction = FactionsProvider.Cache.Local.FACTIONS.provide().get(arg1);

            if (faction == null) {
                Message.ERROR.sendDefault(player, FactionDefaultMessage.FACTION_NOT_FOUND, arg1);
                return;
            }

            if (!Objects.equal(faction, userFaction) && !user.getHandle().hasGroup(Group.GAME_MASTER)) {
                Message.ERROR.sendDefault(player, FactionDefaultMessage.NO_PERMISSION, Group.GAME_MASTER.getDisplayNameRaw());
                return;
            }
        }

        if (faction == null) {
            Message.ERROR.send(player, "Use /f geradores [tag]");
            return;
        }

        if(FactionsProvider.getSettings().isSpawnersManagerActive()){
            IndexSpawnerInventory inventory = new IndexSpawnerInventory(user, faction);
            player.openInventory(inventory);
        } else {
            player.openInventory(new ListPlacedSpawnersInventory(user, faction));
        }


    }
}
