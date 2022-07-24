package net.hyze.factions.framework.commands;

import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.factions.framework.enchantments.EnchantmentInventory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EnchantCommand extends CustomCommand {

    public EnchantCommand() {
        super("encantar", CommandRestriction.IN_GAME);
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {
        Player player = (Player) sender;
        player.openInventory(new EnchantmentInventory(player, user));
    }

}
