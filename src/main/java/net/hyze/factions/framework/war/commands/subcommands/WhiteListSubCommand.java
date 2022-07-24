package net.hyze.factions.framework.war.commands.subcommands;

import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.war.War;
import org.bukkit.command.CommandSender;

public class WhiteListSubCommand extends CustomCommand implements GroupCommandRestrictable {

    public WhiteListSubCommand() {
        super("whitelist");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if (War.CONFIG == null) {
            Message.ERROR.send(sender, "Este não é o servidor da guerra.");
            return;
        }

        if (args.length < 1) {
            StringBuilder string = new StringBuilder();

            string.append("&aPRE_START_WHITELIST:&f\n");

            War.COMMAND_PRE_START_WHITELIST.forEach(target -> {
                string.append(target).append("\n");
            });

            string.append("\n \n&aLATE_GAME_WHITELIST&f\n");

            War.COMMAND_LATE_GAME_WHITELIST.forEach(target -> {
                string.append(target).append("\n");
            });

            Message.INFO.send(sender, string.toString());
            return;
        }

        if (args[0].equalsIgnoreCase("prestart")) {

            String command = args[1];

            if (War.COMMAND_PRE_START_WHITELIST.contains(command)) {
                War.COMMAND_PRE_START_WHITELIST.remove(command);
                Message.ERROR.send(sender, String.format("Comando \"%s\" REMOVIDO da lista PRE_START_WHITELIST!", command));
            } else {
                War.COMMAND_PRE_START_WHITELIST.add(command);
                Message.SUCCESS.send(sender, String.format("Comando \"%s\" ADICIONADO na lista PRE_START_WHITELIST!", command));
            }

        }

        if (args[0].equalsIgnoreCase("lategame")) {

            String command = args[1];

            if (War.COMMAND_LATE_GAME_WHITELIST.contains(command)) {
                War.COMMAND_LATE_GAME_WHITELIST.remove(command);
                Message.ERROR.send(sender, String.format("Comando \"%s\" REMOVIDO da lista LATE_GAME_WHITELIST!", command));
            } else {
                War.COMMAND_LATE_GAME_WHITELIST.add(command);
                Message.SUCCESS.send(sender, String.format("Comando \"%s\" ADICIONADO na lista LATE_GAME_WHITELIST!", command));
            }

        }
    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }

}
