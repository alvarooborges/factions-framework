package net.hyze.factions.framework.war.commands.subcommands;

import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.spigot.commands.CustomCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;

public class BossSubCommand extends CustomCommand implements GroupCommandRestrictable {

    public BossSubCommand() {
        super("boss");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        
        player.launchProjectile(WitherSkull.class).setVelocity(player.getLocation().getDirection());

        //new WarCustomWither().spawn(new Location(player.getWorld(), War.CONFIG.getSpawn().getX(), War.CONFIG.getSpawn().getY() + 1, War.CONFIG.getSpawn().getZ()));
    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }

}
