package net.hyze.factions.framework.commands.factioncommand.subcommands;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.base.Joiner;
import lombok.Getter;
import net.hyze.core.shared.cache.local.utils.CaffeineScheduler;
import net.hyze.core.shared.commands.argument.Argument;
import net.hyze.core.shared.messages.MessageUtils;
import net.hyze.core.shared.misc.utils.Plural;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.commands.factioncommand.FactionSubCommand;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.relation.user.FactionRole;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.misc.utils.FactionDefaultMessage;
import net.hyze.factions.framework.misc.utils.FactionUserUtils;
import net.hyze.factions.framework.misc.utils.FactionUtils;
import net.hyze.factions.framework.user.FactionUser;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MembersSubCommand extends FactionSubCommand {

    @Getter
    private final CommandRelationType commandRelationType = CommandRelationType.PLAYER_TO_FACTION;

    public static final LoadingCache<Faction, Set<FactionUser>> ALL_USERS_CACHE = Caffeine.newBuilder()
            .scheduler(CaffeineScheduler.getInstance())
            .expireAfterWrite(5, TimeUnit.SECONDS)
            .build(target -> {
                return FactionUtils.getUsers(target);
            });

    public static final LoadingCache<Faction, Set<FactionUser>> ONLINE_USERS_CACHE = Caffeine.newBuilder()
            .scheduler(CaffeineScheduler.getInstance())
            .expireAfterWrite(5, TimeUnit.SECONDS)
            .build(target -> {
                return FactionUtils.getUsers(target, true);
            });

    public MembersSubCommand() {
        super("membros");

        registerArgument(new Argument("tag", "Tag da facção", false));
    }

    @Override
    public void onCommand(Player player, FactionUser user, FactionUserRelation relation, String[] args) {

        Faction faction = null;

        if (relation != null) {
            faction = relation.getFaction();
        }

        if (args.length < 1 && relation == null) {
            player.spigot().sendMessage(getUsage(player, "membros").create());
            return;
        }

        if (args.length > 0) {
            String tag = args[0].toUpperCase();

            faction = FactionsProvider.Cache.Local.FACTIONS.provide().getIfPresent(tag);

            if (faction == null) {
                Message.ERROR.sendDefault(player, FactionDefaultMessage.FACTION_NOT_FOUND, tag);
                return;
            }
        }

        Set<FactionUser> allUsers = ALL_USERS_CACHE.get(faction);

        Set<FactionUser> onlineUsers = ONLINE_USERS_CACHE.get(faction);

        ComponentBuilder builder = new ComponentBuilder("\n");

        allUsers.stream()
                .collect(Collectors.groupingBy(target -> target.getRelation().getRole()))
                .entrySet()
                .stream()
                .sorted((o1, o2) -> o2.getKey().compareTo(o1.getKey()))
                .forEach(entry -> {
                    FactionRole role = entry.getKey();
                    Collection<FactionUser> users = entry.getValue();

                    String roleDisplayName = Plural.of(users.size(), role.getDisplayName(), role.getDisplayPluralName());

                    builder.reset()
                            .append(role.getSymbol())
                            .color(ChatColor.YELLOW)
                            .append(roleDisplayName)
                            .append(": ");

                    Set<BaseComponent[]> components = users.stream()
                            .map(target -> {

                                boolean isLogged = onlineUsers.contains(target);

                                String prefix = target.getHandle().getHighestGroup().getDisplayTag(target.getNick());

                                ComponentBuilder hoverBuilder = new ComponentBuilder(prefix)
                                        .append("\n")
                                        .append(MessageUtils.translateColorCodes(
                                                Joiner.on("\n").join(FactionUserUtils.getDescription(target))
                                        ));

                                ComponentBuilder targetBuilder = new ComponentBuilder("\u25CF")
                                        .color(isLogged ? ChatColor.GREEN : ChatColor.RED)
                                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverBuilder.create()))
                                        .append(" ")
                                        .append(target.getNick());

                                return targetBuilder.create();
                            })
                            .collect(Collectors.toSet());

                    Iterator<BaseComponent[]> iterator = components.iterator();

                    while (iterator.hasNext()) {
                        BaseComponent[] bcs = iterator.next();

                        builder.append(bcs, ComponentBuilder.FormatRetention.NONE)
                                .color(ChatColor.GRAY);

                        if (iterator.hasNext()) {
                            builder.append(", ");
                        } else {
                            builder.append(".");
                        }
                    }

                    builder.append("\n");
                });

        player.spigot().sendMessage(builder.create());
    }
}
