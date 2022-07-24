package net.hyze.factions.framework.commands;

import lombok.Getter;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkinCommand extends CustomCommand implements GroupCommandRestrictable {

    @Getter
    private final Group group = Group.ARCANE;

    public SkinCommand() {
        super("skin", CommandRestriction.IN_GAME);
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {

        ComponentBuilder builder = new ComponentBuilder("\n")
                .append(" O comando ")
                .color(ChatColor.YELLOW)
                .append("/skin")
                .color(ChatColor.GREEN)
                .append(" está disponível para você, mas só pode ser usado no ")
                .color(ChatColor.YELLOW)
                .append("/lobby")
                .color(ChatColor.GREEN)
                .append(".")
                .color(ChatColor.YELLOW)
                .append("\n");

        ((Player) sender).sendMessage(builder.create());
    }
}
