package net.hyze.factions.framework.commands;

import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.factions.framework.misc.tags.TagInventory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TagsCommand extends CustomCommand implements GroupCommandRestrictable {

    public TagsCommand() {
        super("tags", CommandRestriction.IN_GAME, "tag");
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {

        Player player = (Player) sender;
        player.openInventory(new TagInventory(user));

    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }


}
