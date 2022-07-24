package net.hyze.factions.framework.commands;

import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import org.bukkit.command.CommandSender;

public class DiscordCommand extends CustomCommand {

    public static boolean eneblad = false;

    public DiscordCommand() {
        super("discord", CommandRestriction.IN_GAME);
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {

    }

}
