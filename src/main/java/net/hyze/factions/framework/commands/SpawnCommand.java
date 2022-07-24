package net.hyze.factions.framework.commands;

import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.echo.packets.user.connect.ConnectReason;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.utils.teleporter.Teleporter;
import net.hyze.factions.framework.events.PlayerSpawnCommandEvent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class SpawnCommand extends CustomCommand {

    public SpawnCommand() {
        super("spawn", CommandRestriction.IN_GAME);
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {
        PlayerSpawnCommandEvent event = new PlayerSpawnCommandEvent(user);

        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        Teleporter.builder()
                .toAppType(AppType.FACTIONS_SPAWN)
                .reason(ConnectReason.WARP)
                .welcomeMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Bem-vindo ao spawn."))
                .build()
                .teleport(user);
    }
}
