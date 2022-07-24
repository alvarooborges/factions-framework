package net.hyze.factions.framework.war.commands.subcommands;

import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.war.War;
import org.bukkit.command.CommandSender;

public class AllyFireSubCommand extends CustomCommand implements GroupCommandRestrictable {

    public AllyFireSubCommand() {
        super("allyfire");
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

        boolean current = FactionsProvider.getSettings().isAllyFire();

        FactionsProvider.getSettings().setAllyFire(!current);

        Message.SUCCESS.send(sender, "PvP entre alianças: &f" + FactionsProvider.getSettings().isAllyFire());
    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }

}
