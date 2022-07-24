package net.hyze.factions.framework.misc.furnaces.commands;

import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.factions.framework.misc.furnaces.inventories.FurnacesListInventory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FurnaceCommand extends CustomCommand {

    public FurnaceCommand() {
        super("furnace", CommandRestriction.IN_GAME, "fornalhas", "fornalha");
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {
        Player player = (Player) sender;

        player.openInventory(new FurnacesListInventory(user));
    }
}
