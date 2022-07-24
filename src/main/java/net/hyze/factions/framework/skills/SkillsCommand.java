package net.hyze.factions.framework.skills;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.misc.utils.DefaultMessage;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkillsCommand extends CustomCommand {

    public SkillsCommand() {
        super("skills", CommandRestriction.IN_GAME, "habilidades", "stats", "inspect");
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {

        Player player = (Player) sender;

        if (args.length == 0) {
            player.openInventory(UserInventoryManager.getSkillsInventory(user));
        } else {

            if (args[0].equalsIgnoreCase("top")) {
                player.openInventory(UserInventoryManager.getTopSkillsInventory(user));
                return;
            }

            User target = CoreProvider.Cache.Local.USERS.provide().get(args[0]);

            if (target == null) {
                Message.ERROR.sendDefault(sender, DefaultMessage.PLAYER_NOT_FOUND, args[0]);
                return;
            }

            player.openInventory(UserInventoryManager.getSkillsInventory(target));
        }
    }
}
