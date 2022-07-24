package net.hyze.factions.framework.commands.factioncommand.subcommands;

import com.google.common.primitives.Ints;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.commands.factioncommand.FactionCommand;
import net.hyze.factions.framework.commands.factioncommand.FactionSubCommand;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.user.FactionUser;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;

import java.util.Collection;

public class HelpSubCommand extends FactionSubCommand {

    public HelpSubCommand() {
        super("ajuda");
    }

    @Override
    public void onCommand(Player player, FactionUser user, FactionUserRelation runtimeRelation, String[] args) {

        if (args.length == 1) {

            Integer index = Ints.tryParse(args[0]);
            if (index == null) {
                Message.ERROR.send(player, "Algo de errado aconteceu.");
                return;
            }

            String header = null;
            Collection<String> customCommands = null;

            switch (index) {
                case 0:
                    header = CommandRelationType.FACTION_TO_PLAYER.getDisplayName();
                    customCommands = FactionCommand.getCommandsRelationsTypesMap().get(CommandRelationType.FACTION_TO_PLAYER);
                    break;

                case 1:
                    header = CommandRelationType.PLAYER_TO_FACTION.getDisplayName();
                    customCommands = FactionCommand.getCommandsRelationsTypesMap().get(CommandRelationType.PLAYER_TO_FACTION);
                    break;

                case 2:
                    header = CommandRelationType.FACTION_TO_ADM.getDisplayName();
                    customCommands = FactionCommand.getCommandsRelationsTypesMap().get(CommandRelationType.FACTION_TO_ADM);
                    break;

                case 3:
                    header = CommandRelationType.MISC.getDisplayName();
                    customCommands = FactionCommand.getCommandsRelationsTypesMap().get(CommandRelationType.MISC);
                    break;

                default:
                    Message.ERROR.send(player, "Acho que você está um pouco confuso, tente /f ajuda novamente por favor :)");
            }

            if (header != null) {
                Message.SUCCESS.send(player, header + ":");
            }

            String prefix = ChatColor.YELLOW + "/f";

            if (customCommands != null) {
                player.sendMessage(" ");
                customCommands.forEach(subCommands -> player.sendMessage(" " + prefix + subCommands));
                player.sendMessage(" ");
            }

            return;
        }

        ComponentBuilder builder = new ComponentBuilder("Escolha o tipo de comando e clique para receber seu guia!")
                .color(ChatColor.YELLOW)
                .append("\n ")
                .append("\n ")
                .append(" ▪")
                .color(ChatColor.DARK_GRAY)
                .append(" Relação facção - jogador", ComponentBuilder.FormatRetention.NONE)
                .color(ChatColor.GREEN)
                .event(buildClickEventByRelation(CommandRelationType.FACTION_TO_PLAYER))
                .append("\n ")
                .append(" ▪")
                .color(ChatColor.DARK_GRAY)
                .append(" Relação jogador - facção", ComponentBuilder.FormatRetention.NONE)
                .color(ChatColor.GREEN)
                .event(buildClickEventByRelation(CommandRelationType.PLAYER_TO_FACTION))
                .append("\n ")
                .append(" ▪")
                .color(ChatColor.DARK_GRAY)
                .append(" Relação facção - administração", ComponentBuilder.FormatRetention.NONE)
                .color(ChatColor.GREEN)
                .event(buildClickEventByRelation(CommandRelationType.FACTION_TO_ADM))
                .append("\n ")
                .append(" ▪")
                .color(ChatColor.DARK_GRAY)
                .append(" Outros", ComponentBuilder.FormatRetention.NONE)
                .color(ChatColor.GREEN)
                .event(buildClickEventByRelation(CommandRelationType.MISC))
                .append("\n ");

        player.sendMessage(builder.create());
    }

    private ClickEvent buildClickEventByRelation(CommandRelationType type) {
        return new ClickEvent(
                ClickEvent.Action.RUN_COMMAND,
                "/f ajuda " + type.ordinal()
        );
    }
}
