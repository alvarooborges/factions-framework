package net.hyze.factions.framework.commands.factioncommand.subcommands;

import lombok.Getter;
import net.hyze.core.shared.commands.argument.impl.NickArgument;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.commands.factioncommand.FactionSubCommand;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.misc.utils.FactionUtils;
import net.hyze.factions.framework.user.FactionUser;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ProfileSubCommand extends FactionSubCommand {

    @Getter
    private final CommandRelationType commandRelationType = CommandRelationType.PLAYER_TO_FACTION;

    public ProfileSubCommand() {
        super("perfil");

        registerArgument(new NickArgument("nick", "Nick de quem você deseja ver o perfil", false));
    }

    @Override
    public void onCommand(Player player, FactionUser user, FactionUserRelation relation, String[] args) {
        FactionUser target = user;
        FactionUserRelation targetRelation = relation;

        if (args.length > 0) {
            String nick = args[0];
            target = FactionsProvider.Cache.Local.USERS.provide().get(nick);

            if (target == null) {
                Message.ERROR.send(player, "O jogador não existe.");
                return;
            }

            targetRelation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getByUser(target);
        }

        ComponentBuilder builder = new ComponentBuilder("\n Perfil de ")
                .color(ChatColor.YELLOW)
                .append(target.getDisplayName(ChatColor.GRAY, target.getHandle().getHighestGroup().getColor()))
                .append("\n");

        if (targetRelation != null) {
            append(builder, "Facção", ChatColor.stripColor(targetRelation.getFaction().getDisplayName()));
            append(builder, "Cargo", targetRelation.getRole().getDisplayName());
        } else {
            append(builder, "Facção", "Sem facção");
        }

        append(builder, "Poder", String.format("%s/%s", target.getStats().getPower(), target.getStats().getTotalMaxPower()));
        append(builder, "KDR", FactionUtils.formatKDR(target.getStats().getKDR()));
        append(builder, "Abates", target.getStats().getTotalKills());
        append(builder, "Mortes", target.getStats().getTotalDeaths());

        player.spigot().sendMessage(builder.create());
    }

    private ComponentBuilder append(ComponentBuilder builder, String label, Object value) {
        return builder.append(String.format(" * %s: ", label))
                .color(ChatColor.YELLOW)
                .append(String.valueOf(value))
                .color(ChatColor.WHITE)
                .append("\n");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        return super.tabComplete0(sender, alias, args);
    }
}
