package net.hyze.factions.framework.commands;

import lombok.Getter;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.echo.packets.user.connect.ConnectReason;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.utils.teleporter.Teleporter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;

public class VIPCommand extends CustomCommand implements GroupCommandRestrictable {

    @Getter
    private final Group group = Group.ARCANE;

    public VIPCommand() {
        super("vip", CommandRestriction.IN_GAME);
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {
        Teleporter.builder()
                .toAppType(AppType.FACTIONS_VIP)
                .reason(ConnectReason.WARP)
                .welcomeMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Bem-vindo ao mundo vip."))
                .build()
                .teleport(user);
    }
}
