package net.hyze.factions.framework.commands;

import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.echo.packets.user.connect.ConnectReason;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.utils.TeleportManager;
import net.hyze.factions.framework.FactionsProvider;
import org.bukkit.command.CommandSender;

public class ShopCommand extends CustomCommand {

    public ShopCommand() {
        super("loja", CommandRestriction.IN_GAME);
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {

        TeleportManager.teleport(
                user,
                AppType.FACTIONS_SPAWN,
                ConnectReason.WARP,
                FactionsProvider.getSettings().getShopPosition()
        );

    }

}
