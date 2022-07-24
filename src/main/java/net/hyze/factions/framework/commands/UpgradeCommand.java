package net.hyze.factions.framework.commands;

import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.misc.vipupgrade.VIPUpgradeInventory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UpgradeCommand extends CustomCommand {

    public UpgradeCommand() {
        super("upgrade", CommandRestriction.IN_GAME);
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {

        Player player = (Player) sender;

        Group currentVIP = user.getHighestGroup();
        Group upgradeVIP = getNextVIP(currentVIP);

        if (upgradeVIP == null) {
            Message.ERROR.send(player, "Você não tem upgrades disponíveis.");
            return;
        }

        player.openInventory(new VIPUpgradeInventory(user, currentVIP, upgradeVIP));

    }

    public static Integer getPrice(Group group) {
        switch (group) {
            case DIVINE:
            case HEAVENLY:
                return 2100;

            default:
                return null;
        }
    }

    public static Group getNextVIP(Group group) {
        switch (group) {
            case ARCANE:
                return Group.DIVINE;

            case DIVINE:
                return Group.HEAVENLY;

            default:
                return null;
        }
    }

}
