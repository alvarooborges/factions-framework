package net.hyze.factions.framework.commands.profilecommand;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.argument.impl.NickArgument;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.commands.profilecommand.inventories.UserProfileInventory;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ProfileCommand extends CustomCommand {

    public ProfileCommand() {
        super("perfil", CommandRestriction.IN_GAME, "perfil");

        registerArgument(new NickArgument("nick", "Nick do jogador.", false));

    }

    @Override
    public void onCommand(CommandSender sender, User handle, String[] args) {
        Player player = (Player) sender;

        FactionUser user = null;

        if (args.length == 1) {
            User target = CoreProvider.Cache.Local.USERS.provide().get(args[0]);
            if (target != null) {
                user = FactionsProvider.Cache.Local.USERS.provide().get(target);
            }
        } else {
            user = FactionsProvider.Cache.Local.USERS.provide().get(handle);
        }

        if (user == null) {
            Message.ERROR.send(player, "Usuário inválido.");
            return;
        }

        player.openInventory(new UserProfileInventory(player, user));

    }
}
