package net.hyze.factions.framework.misc.lostfortress.commands.subcommands;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.echo.packets.BroadcastMessagePacket;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;

import java.util.Collections;

public class BroadcastSubCommand extends CustomCommand implements GroupCommandRestrictable {

    public BroadcastSubCommand() {
        super("broadcast", CommandRestriction.IN_GAME);
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {
        String message = String.join(" ", args);

        CoreProvider.Redis.ECHO.provide().publish(
                BroadcastMessagePacket.builder()
                .groups(Collections.singleton(Group.ADMINISTRATOR))
                .components(TextComponent.fromLegacyText("\n" + ChatColor.DARK_PURPLE + "[BASE PERDIDA] " + ChatColor.LIGHT_PURPLE + message + "\n "))
                .build()
        );
    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }

}
