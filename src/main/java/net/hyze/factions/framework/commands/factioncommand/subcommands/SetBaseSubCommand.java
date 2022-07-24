package net.hyze.factions.framework.commands.factioncommand.subcommands;

import lombok.Getter;
import net.hyze.core.shared.world.location.SerializedLocation;
import net.hyze.core.spigot.inventory.ConfirmInventory;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.core.spigot.world.location.unserializer.BukkitLocationParser;
import net.hyze.factions.framework.commands.factioncommand.FactionSubCommand;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.permission.FactionPermission;
import net.hyze.factions.framework.faction.relation.user.FactionRole;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.lands.Claim;
import net.hyze.factions.framework.misc.utils.FactionUtils;
import net.hyze.factions.framework.misc.utils.LandUtils;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class SetBaseSubCommand extends FactionSubCommand {

    @Getter
    private final CommandRelationType commandRelationType = CommandRelationType.FACTION_TO_ADM;

    public SetBaseSubCommand() {
        super("setbase", FactionRole.RECRUIT);
    }

    @Override
    public void onCommand(Player player, FactionUser user, FactionUserRelation relation, String[] args) {

        Faction faction = relation.getFaction();

        if (!check(player, user, faction)) {
            return;
        }

        ConfirmInventory inventory = ConfirmInventory.of(event -> {
            Faction faction0 = relation.getFaction();

            if (!check(player, user, faction0)) {
                return;
            }

            SerializedLocation baseLocation = BukkitLocationParser.serialize(player.getLocation());

           if (!FactionUtils.setFactionHome(user, faction0, baseLocation)) {
               Message.ERROR.send(player, "Não foi possível definir a base da facção.");
           }

        }, event -> {
            player.closeInventory();
            Message.ERROR.send(player, "Operação cancelada.");
        }, ItemBuilder.of(Material.BEDROCK).name("&aDefinir base para facção").make());

        player.openInventory(inventory.make("Deseja definir a base?"));
    }

    private boolean check(Player player, FactionUser user, Faction faction) {

        if (!FactionPermission.COMMAND_SET_BASE.allows(faction, user)) {
            player.closeInventory();
            Message.ERROR.send(player, "Você não tem permissão para definir pontos de spawn para sua facção.");
            return false;
        }

        Claim claim = LandUtils.getClaim(player.getLocation());

        if (claim == null || claim.isTemporary() || claim.isContested() || !claim.getFactionId().equals(faction.getId())) {
            Message.ERROR.send(player, "Você precisa estar em uma terra da sua facção.");
            return false;
        }

        return true;
    }
}
