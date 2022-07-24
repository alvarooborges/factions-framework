package net.hyze.factions.framework.misc.lostfortress.commands.subcommands;

import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.misc.lostfortress.LostFortressConstants;
import org.bukkit.command.CommandSender;

public class FallDamageSubCommand extends CustomCommand implements GroupCommandRestrictable {

    public FallDamageSubCommand() {
        super("falldamage", CommandRestriction.IN_GAME);
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {

        LostFortressConstants.FALL_DAMAGE = !LostFortressConstants.FALL_DAMAGE;
        Message.INFO.send(sender, "Dano de queda: &f" + LostFortressConstants.FALL_DAMAGE);

    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }

}
