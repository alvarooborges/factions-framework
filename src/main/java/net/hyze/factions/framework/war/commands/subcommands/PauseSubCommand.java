package net.hyze.factions.framework.war.commands.subcommands;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.combat.CombatManager;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.war.War;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class PauseSubCommand extends CustomCommand implements GroupCommandRestrictable {

    public PauseSubCommand() {
        super("pause");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if (War.CONFIG == null) {
            Message.ERROR.send(sender, "Este não é o servidor da guerra.");
            return;
        }

        if (War.CLOCK.getCurrentEnumWarPhase() == null) {
            Message.ERROR.send(sender, "O evento não está ocorrendo.");
            return;
        }

        War.PAUSE = true;

        Bukkit.getOnlinePlayers().forEach(player -> {
            User user = CoreProvider.Cache.Local.USERS.provide().get(player.getName());
            CombatManager.untag(user);
        });

        Message.ERROR.send(sender, "Guerra pausada!");

    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }

}
