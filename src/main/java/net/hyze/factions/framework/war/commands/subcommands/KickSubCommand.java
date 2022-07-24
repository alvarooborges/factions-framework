package net.hyze.factions.framework.war.commands.subcommands;

import net.hyze.core.shared.commands.argument.Argument;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KickSubCommand extends CustomCommand {

    public KickSubCommand() {
        super("kick");
        
        registerArgument(new Argument("nick", "nick do jogador"));
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        Player player = Bukkit.getPlayerExact(args[0]);
        
        if(player == null){
            Message.ERROR.send(sender, "Ops, jogador não encontrado.");
            return;
        }

        player.kickPlayer("Tchau!");
        
        Message.SUCCESS.send(sender, player.getName() + " foi chutado.");

    }

}
