package net.hyze.factions.framework.commands.factioncommand.subcommands;

import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.commands.factioncommand.FactionSubCommand;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.entity.Player;

public class SeeChunkCommand extends FactionSubCommand {

    public SeeChunkCommand() {
        super("verterras");
    }

    @Override
    public void onCommand(Player player, FactionUser user, String[] args) {
        if (user.getOptions().isSeeChunksEnabled()) {
            Message.ERROR.send(player, "Você desativou o '/f verterras'!");
        } else {
            Message.SUCCESS.send(player, "Você ativou o '/f verterras'!");
        }

        user.getOptions().setSeeChunksEnabled(!user.getOptions().isSeeChunksEnabled());
        user.getOptions().sync();
    }
}
