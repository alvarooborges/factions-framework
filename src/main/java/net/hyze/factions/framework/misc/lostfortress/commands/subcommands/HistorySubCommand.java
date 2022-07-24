package net.hyze.factions.framework.misc.lostfortress.commands.subcommands;

import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.factions.framework.misc.lostfortress.LostFortressUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HistorySubCommand extends CustomCommand {

    public HistorySubCommand() {
        super("ver", CommandRestriction.IN_GAME);
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {
        Player player = (Player) sender;

        LostFortressUtil.openInventory(user, player);
    }

}
