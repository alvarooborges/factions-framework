package net.hyze.factions.framework.commands;

import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VanishCommand extends CustomCommand implements GroupCommandRestrictable {

    public VanishCommand() {
        super("v", CommandRestriction.IN_GAME);

    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }

    
    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {
        Bukkit.getOnlinePlayers().forEach(target -> {
            target.hidePlayer((Player) sender);
        });
    }
}
