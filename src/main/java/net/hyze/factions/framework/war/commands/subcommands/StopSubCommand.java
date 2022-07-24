package net.hyze.factions.framework.war.commands.subcommands;

import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.settings.SettingsManager;
import net.hyze.factions.framework.war.War;
import org.bukkit.command.CommandSender;

public class StopSubCommand extends CustomCommand implements GroupCommandRestrictable {

    public StopSubCommand() {
        super("stop");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if (War.CLOCK.getCurrentEnumWarPhase() == null) {
            Message.ERROR.send(sender, "O evento já não está ocorrendo.");
            return;
        }

        SettingsManager.setExplosionsStatus(true);

        Message.SUCCESS.send(sender, "Parando fases.");
        War.CLOCK.stop();

        War.OPEN = false;

    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }

}
