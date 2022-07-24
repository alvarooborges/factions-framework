package net.hyze.factions.framework.commands;

import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.factions.framework.enchantments.RecyclerInventory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RecycleCommand extends CustomCommand {

    public RecycleCommand() {
        super("reciclar", CommandRestriction.IN_GAME);
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {
        Player player = (Player) sender;
        player.openInventory(new RecyclerInventory());
    }

}
