package net.hyze.factions.framework.war.commands.subcommands;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.commands.argument.impl.NickArgument;
import net.hyze.core.shared.echo.packets.user.connect.ConnectReason;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.combat.CombatManager;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.TeleportManager;
import org.bukkit.command.CommandSender;

public class SendSubCommand extends CustomCommand implements GroupCommandRestrictable {

    public SendSubCommand() {
        super("send");

        registerArgument(new NickArgument("nick", "Nick do jogador que você deseja enviar para o spawn."));
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        User user = CoreProvider.Cache.Local.USERS.provide().get(args[0]);

        CombatManager.untag(user);

        TeleportManager.teleport(user, AppType.FACTIONS_SPAWN, ConnectReason.PLUGIN, "");

        Message.SUCCESS.send(sender, "Tentando enviar o jogador " + user.getNick() + " para o spawn.");
    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }

}
