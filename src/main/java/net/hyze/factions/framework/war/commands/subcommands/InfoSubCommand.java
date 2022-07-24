package net.hyze.factions.framework.war.commands.subcommands;

import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.war.War;
import net.hyze.factions.framework.war.WarUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class InfoSubCommand extends CustomCommand implements GroupCommandRestrictable {

    public InfoSubCommand() {
        super("info");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        String phase = "nenhuma";

        if (War.CLOCK.getCurrentEnumWarPhase() != null) {
            phase = War.CLOCK.getCurrentEnumWarPhase().getWarPhase().getDisplayName();
        }

        Message.SUCCESS.send(
                sender,
                String.format(
                        "\n&aFase: &f%s.\n&aJogadores Online: &f%s\n&aFacções: &f%s\n ",
                        phase,
                        Bukkit.getOnlinePlayers().size(),
                        WarUtils.getFactions().size()
                )
        );

    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }

}
