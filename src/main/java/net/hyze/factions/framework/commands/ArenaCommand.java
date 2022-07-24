package net.hyze.factions.framework.commands;

import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.echo.packets.user.connect.ConnectReason;
import net.hyze.core.shared.user.User;
import net.hyze.core.shared.world.location.SerializedLocation;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.utils.TeleportManager;
import net.hyze.factions.framework.FactionsProvider;
import org.bukkit.command.CommandSender;

public class ArenaCommand extends CustomCommand {

    public ArenaCommand() {
        super("arena", CommandRestriction.IN_GAME);
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {

        SerializedLocation serializedLocation = FactionsProvider.getSettings().getArenaLocation();

        if (serializedLocation == null) {
            return;
        }

        TeleportManager.teleport(user, serializedLocation.clone(), ConnectReason.WARP);
    }

}
