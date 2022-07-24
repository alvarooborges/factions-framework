package net.hyze.factions.framework.divinealtar.command;

import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.factions.framework.divinealtar.manager.AltarManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AltarCommand extends CustomCommand implements GroupCommandRestrictable {

    public AltarCommand() {
        super("altar", CommandRestriction.IN_GAME);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        
        Player player = (Player) sender;
        player.getInventory().addItem(AltarManager.buildAltarItem());
        
    }
    
    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }

}
