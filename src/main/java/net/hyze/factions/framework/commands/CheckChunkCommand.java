package net.hyze.factions.framework.commands;

import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.FactionsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

public class CheckChunkCommand extends CustomCommand implements GroupCommandRestrictable, Listener {

    private static Boolean ENABLED = false;

    public CheckChunkCommand() {
        super("checkchunk", CommandRestriction.IN_GAME);

        Bukkit.getPluginManager().registerEvents(this, FactionsPlugin.getInstance());
    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {

        Player player = (Player) sender;

        ENABLED = !ENABLED;

        if (ENABLED) {
            Message.SUCCESS.send(player, "Habilitado.");
        } else {
            Message.ERROR.send(player, "Desabilitado.");
        }

    }

    @EventHandler
    public void on(ChunkLoadEvent event) {

        if (!ENABLED) {
            return;
        }

        if (event.isNewChunk()) {
            Bukkit.getOnlinePlayers().stream().filter(player -> player.isOp()).forEach(player -> {
                Message.ERROR.send(player, "Nova chunk encontrada!");
            });
        }

    }
}
