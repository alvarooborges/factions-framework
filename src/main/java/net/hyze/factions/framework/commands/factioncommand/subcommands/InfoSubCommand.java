package net.hyze.factions.framework.commands.factioncommand.subcommands;

import lombok.Getter;
import net.hyze.core.shared.commands.argument.Argument;
import net.hyze.core.shared.messages.MessageUtils;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.FactionsConstants;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.commands.factioncommand.FactionSubCommand;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.relation.faction.FactionRelation;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.misc.utils.FactionUserUtils;
import net.hyze.factions.framework.misc.utils.FactionUtils;
import net.hyze.factions.framework.user.FactionUser;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class InfoSubCommand extends FactionSubCommand {

    @Getter
    private final CommandRelationType commandRelationType = CommandRelationType.PLAYER_TO_FACTION;

    public InfoSubCommand() {
        super("info");

        registerArgument(new Argument("tag", "A tag da facção", false));

        
    }

    @Override
    public void onCommand(Player player, FactionUser user, FactionUserRelation relation, String[] args) {

        Faction faction;

        if (args.length > 0) {
            String tag = args[0];
            if (!tag.matches(FactionsConstants.TAG_PATTERN)) {
                Message.ERROR.send(player, "A tag da facção não pode conter caracteres especiais.");
                return;
            }

            faction = FactionsProvider.Cache.Local.FACTIONS.provide().getIfPresent(tag);

            if (faction == null) {
                Message.ERROR.send(player, String.format("A facção '%s' não existe.", tag));
                return;
            }
        } else {
            if (relation == null) {
                Message.ERROR.send(player, "Use /f info <tag>");
                return;
            }

            faction = relation.getFaction();
        }

//        player.openInventory(new FactionInformationInventory(faction, user));
        Set<BaseComponent[]> onlineUsers = FactionUtils.getUsers(faction, true)
                .stream()
                .map(FactionUserUtils::getChatComponents)
                .collect(Collectors.toSet());

        Set<FactionUser> users = FactionUtils.getUsers(faction);

        ComponentBuilder builder = new ComponentBuilder(String.format("%-3s - %s", faction.getTag().toUpperCase(), faction.getName()))
                .color(ChatColor.YELLOW)
                .append("\n")
                .append("Terras: ").color(ChatColor.GRAY)
                .append(FactionUtils.countClaims(faction).toString()).color(ChatColor.WHITE)
                .append("\n")
                .append("Poder: ").color(ChatColor.GRAY)
                .append(String.format("%s/%s",
                        FactionUtils.getPower(faction),
                        FactionUtils.getMaxPower(faction)
                )).color(ChatColor.WHITE)
                .append("\n")
                .append(String.format(
                        "Membros (%s/%s): ",
                        onlineUsers.size(), users.size()
                )).color(ChatColor.GRAY);

        if (onlineUsers.isEmpty()) {
            builder.append("Ninguém está online.");
        } else {
            Iterator<BaseComponent[]> iterator = onlineUsers.iterator();

            while (iterator.hasNext()) {
                builder.append(iterator.next());

                if (iterator.hasNext()) {
                    builder.append(", ", ComponentBuilder.FormatRetention.NONE);
                } else {
                    builder.append(".", ComponentBuilder.FormatRetention.NONE);
                }

                builder.color(ChatColor.GRAY);
            }
        }

        Set<Faction> allies = FactionsProvider.Cache.Local.FACTIONS_RELATIONS.provide().get(faction, FactionRelation.Type.ALLY);

        builder.append("\n")
                .append(String.format(
                        "Aliados (%s/%s): ",
                        allies.size(), FactionsProvider.getSettings().getAllyLimit()
                )).color(ChatColor.GRAY);

        if (allies.isEmpty()) {
            builder.append("Nenhum aliado.")
                    .color(ChatColor.DARK_GRAY);
        } else {
            List<String> alliesNames = allies.stream()
                    .map(Faction::getDisplayName)
                    .map(name -> ChatColor.WHITE + MessageUtils.stripColor(name))
                    .collect(Collectors.toList());

            builder.append(String.join(ChatColor.GRAY + ", ", alliesNames));
            builder.append(ChatColor.GRAY + ".");
        }

        double kdr = users.stream()
                .mapToDouble(u -> u.getStats().getKDR())
                .sum();

        int kills = users.stream()
                .mapToInt(u -> u.getStats().getTotalKills())
                .sum();

        int deaths = users.stream()
                .mapToInt(u -> u.getStats().getTotalDeaths())
                .sum();

        builder.append("\n")
                .append("KDR: ").color(ChatColor.GRAY)
                .append(FactionUtils.formatKDR(kdr) + "").color(ChatColor.WHITE)
                .append("\n")
                .append("Abates: ").color(ChatColor.GRAY)
                .append(kills + "").color(ChatColor.WHITE)
                .append("\n")
                .append("Mortes: ").color(ChatColor.GRAY)
                .append(deaths + "").color(ChatColor.WHITE)
                .append("\n");

        player.spigot().sendMessage(builder.create());
    }
}
