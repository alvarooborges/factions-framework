package net.hyze.factions.framework.commands.factioncommand.subcommands;

import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.furypoints.FuryType;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FurySwordSubCommand extends CustomCommand implements GroupCommandRestrictable {

    public FurySwordSubCommand() {
        super("furysword", CommandRestriction.IN_GAME);
    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {
        Player player = (Player) sender;

        ItemBuilder sword = ItemBuilder.of(Material.DIAMOND_SWORD);
        
        sword.nbt(FuryType.FURY_MOBS_KEY.name(), 0);
        sword.nbt(FuryType.FURY_PLAYERS_KEY.name(), 3749970);
        
        player.getInventory().addItem(sword.make());

    }
}
