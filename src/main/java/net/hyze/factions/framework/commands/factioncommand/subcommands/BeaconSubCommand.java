package net.hyze.factions.framework.commands.factioncommand.subcommands;

import net.hyze.beacon.BeaconBuilder;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.factions.framework.beacon.FactionBeaconAttribute;
import net.hyze.factions.framework.beacon.FactionBeaconConstants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BeaconSubCommand extends CustomCommand implements GroupCommandRestrictable {

    public BeaconSubCommand() {
        super("beacon", CommandRestriction.IN_GAME);
    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {
        Player player = (Player) sender;

        BeaconBuilder beacon = new BeaconBuilder(
                FactionBeaconConstants.BEACON_DEFAULT,
                "&bSinalizador"
        )
                .attribute(FactionBeaconAttribute.RESTRICT.getAttribute(), 0)
                .attribute(FactionBeaconAttribute.DURABILITY.getAttribute(), 1)
                .attribute(FactionBeaconAttribute.HASTE.getAttribute(), 0)
                //.attribute(FactionBeaconAttribute.REGENERATION.getAttribute(), 0)
                //.attribute(FactionBeaconAttribute.RESISTANCE.getAttribute(), 0)
                .attribute(FactionBeaconAttribute.SPEED.getAttribute(), 0)
                .attribute(FactionBeaconAttribute.STRENGTH.getAttribute(), 0)
                .attribute(FactionBeaconAttribute.JUMP.getAttribute(), 0);

        BeaconBuilder beaconSupreme = new BeaconBuilder(
                FactionBeaconConstants.BEACON_SUPREME,
                "&bSinalizador Supremo",
                "&7Este Sinalizador não é destruído por",
                "&7explosões e possui efeitos adicionais.",
                "",
                "&7Ao ser danificado por explosões, este",
                "&7Sinalizador é desativado por 72 horas."
        )
                .attribute(FactionBeaconAttribute.RESTRICT.getAttribute(), 0)
                .attribute(FactionBeaconAttribute.DURABILITY.getAttribute(), 1)
                .attribute(FactionBeaconAttribute.HASTE.getAttribute(), 0)
                //.attribute(FactionBeaconAttribute.REGENERATION.getAttribute(), 0)
                //.attribute(FactionBeaconAttribute.RESISTANCE.getAttribute(), 0)
                .attribute(FactionBeaconAttribute.SPEED.getAttribute(), 0)
                .attribute(FactionBeaconAttribute.STRENGTH.getAttribute(), 0)
                .attribute(FactionBeaconAttribute.JUMP.getAttribute(), 0)
                .attribute(FactionBeaconAttribute.EXTRA_LIFE.getAttribute(), 0);

        player.getInventory().addItem(beacon.make());
        player.getInventory().addItem(beaconSupreme.make());
    }
}
