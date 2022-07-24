package net.hyze.factions.framework.commands.factioncommand.subcommands.adminsubcommand;

import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.commands.factioncommand.FactionSubCommand;
import net.hyze.factions.framework.commands.factioncommand.subcommands.adminsubcommand.data.AdminAbandonSubCommand;
import net.hyze.factions.framework.commands.factioncommand.subcommands.adminsubcommand.data.AdminDisbandSubCommand;
import net.hyze.factions.framework.commands.factioncommand.subcommands.adminsubcommand.data.AdminTpaLogSubCommand;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.entity.Player;

public class AdminSubCommand extends FactionSubCommand implements GroupCommandRestrictable {

    public AdminSubCommand() {
        super("admin");

        registerSubCommand(new AdminDisbandSubCommand());
        registerSubCommand(new AdminAbandonSubCommand());
        registerSubCommand(new AdminTpaLogSubCommand());
    }

    @Override
    public Group getGroup() {
        return Group.MANAGER;
    }

    @Override
    public void onCommand(Player player, FactionUser user, String[] args) {

        if (user.getOptions().isAdminModeEnabled()) {
            Message.ERROR.send(player, "Você DESATIVOU o modo admin.");
        } else {
            Message.SUCCESS.send(player, "Você ATIVOU o modo admin.");
        }

        user.getOptions().setAdminModeEnabled(!user.getOptions().isAdminModeEnabled());
        user.getOptions().sync();
    }
}
