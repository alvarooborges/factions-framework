package net.hyze.factions.framework.commands;

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

public class AnnounceCommand extends CustomCommand implements GroupCommandRestrictable {

    public AnnounceCommand() {
        super("anuncio", CommandRestriction.IN_GAME);
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {
        
        if(args.length == 0){
            return;
        }

        String message = String.join(" ", args);

        CoreProvider.Redis.ECHO.provide().publish(
                BroadcastMessagePacket.builder()
                .components(TextComponent.fromLegacyText("\n" + ChatColor.DARK_PURPLE + "[!] " + ChatColor.LIGHT_PURPLE + message + "\n "))
                .build()
        );

    }

    @Override
    public Group getGroup() {
        return Group.MANAGER;
    }


}
