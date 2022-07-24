package net.hyze.factions.framework.commands.factioncommand.subcommands;

import lombok.Getter;
import net.hyze.core.shared.commands.argument.Argument;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.commands.factioncommand.FactionSubCommand;
import net.hyze.factions.framework.misc.utils.LandChatHelper;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.entity.Player;

public class MapSubCommand extends FactionSubCommand {

    @Getter
    private final CommandRelationType commandRelationType = CommandRelationType.MISC;

    public MapSubCommand() {
        super("mapa", "map");

        registerArgument(new Argument("on|off", "Ligue ou desligue a visualização do mapa", false));
    }

    @Override
    public void onCommand(Player player, FactionUser user, String[] args) {

        if (args.length > 0) {
            String action = args[0].toLowerCase();

            boolean automap = user.getOptions().isAutoMapEnabled();

            switch (action) {
                case "on":
                    user.getOptions().setAutoMapEnabled(true);
                    Message.SUCCESS.send(player, "&aVisualização do mapa ativada.");
                    break;
                case "off":
                    user.getOptions().setAutoMapEnabled(false);
                    Message.INFO.send(player, "&cVisualização do mapa desativada.");
                    break;
                default:
                    Message.ERROR.send(player, "Use /f mapa [on|off]");
                    return;
            }

            if (automap != user.getOptions().isAutoMapEnabled()) {
                user.getOptions().sync();
            }

            return;
        }

        player.sendMessage(LandChatHelper.drawChatMap(user));
    }
}
