package net.hyze.factions.framework.commands;

import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.echo.packets.user.connect.ConnectReason;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.utils.teleporter.Teleporter;
import org.bukkit.command.CommandSender;

public class MineCommand extends CustomCommand {

    public MineCommand() {
        super("mina", CommandRestriction.IN_GAME, "minerar");
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {

        /*
        if (AppType.FACTIONS_MINE.isCurrent()) {
            Message.ERROR.send(sender, "Você já está na mina.");
            return;
        }
         */

        Teleporter.builder()
                .toAppType(AppType.FACTIONS_MINE)
                .reason(ConnectReason.WARP)
                .build()
                .teleport(user);
    }
}
