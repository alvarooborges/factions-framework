package net.hyze.factions.framework.misc.chat;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Sets;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.messages.MessageUtils;
import net.hyze.core.shared.misc.cooldowns.UserCooldowns;
import net.hyze.core.shared.misc.utils.StringSimilarity;
import net.hyze.core.shared.user.User;
import net.hyze.core.shared.user.preferences.PreferenceStatus;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.economy.commands.impl.TopCurrencySubCommand;
import net.hyze.factions.framework.FactionsConstants;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.relation.faction.FactionRelation;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.lands.Claim;
import net.hyze.factions.framework.misc.utils.FactionUserUtils;
import net.hyze.factions.framework.misc.utils.LandUtils;
import net.hyze.factions.framework.ranking.RankIcon;
import net.hyze.factions.framework.ranking.RankingFactory;
import net.hyze.factions.framework.user.FactionUser;
import net.hyze.hyzeskills.datatypes.database.PlayerStat;
import net.hyze.hyzeskills.tasks.McMMORanksTask;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ChatManager {

    private static final ConcurrentMap<Integer, String> LAST_MESSAGE = Maps.newConcurrentMap();
    private static final String COOLDOWN_KEY_GLOGAL = "COOLDOWN_KEY_GLOGAL";
    private static final String COOLDOWN_KEY_LOCAL = "COOLDOWN_KEY_LOCAL";

    public static BaseComponent[] buildAllianceChatMessage(FactionUser user, String messageRaw) {

        FactionUserRelation relation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getIfPresentByUser(user.getHandle());

        if (relation == null) {
            return null;
        }

        ComponentBuilder messageBuilder = new ComponentBuilder(String.format(
                "[%s%s] ",
                relation.getRole().getSymbol(),
                relation.getFaction().getTag().toUpperCase()
        ))
                .color(ChatColor.DARK_AQUA)
                .append(user.getNick())
                .color(ChatColor.WHITE)
                .append(": ")
                .append(MessageUtils.stripColor(MessageUtils.translateColorCodes(messageRaw)));

        return messageBuilder.create();
    }

    public static void sendAllianceChatMessage(User user, BaseComponent[] components) {
        FactionUserRelation relation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getIfPresentByUserId(user.getId());

        Set<FactionUserRelation> users = Sets.newHashSet();

        users.addAll(FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getIfPresentByFaction(relation.getFaction()));

        Set<Faction> allies = FactionsProvider.Cache.Local.FACTIONS_RELATIONS.provide().get(relation.getFaction(), FactionRelation.Type.ALLY);

        for (Faction ally : allies) {
            users.addAll(FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getIfPresentByFaction(ally));
        }

        users.stream()
                .map(FactionUserRelation::getUser)
                .map(target -> Bukkit.getPlayerExact(target.getNick()))
                .filter(Objects::nonNull)
                .filter(Player::isOnline)
                .forEach(player -> player.sendMessage(components));
    }

    public static BaseComponent[] buildFactionChatMessage(FactionUser user, String messageRaw) {
        FactionUserRelation relation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getByUser(user.getHandle());

        if (relation == null) {
            return null;
        }

        ComponentBuilder messageBuilder = new ComponentBuilder(relation.getRole().getSymbol())
                .color(ChatColor.GREEN)
                .append(user.getNick())
                .color(ChatColor.WHITE)
                .append(": ")
                .append(MessageUtils.stripColor(MessageUtils.translateColorCodes(messageRaw)))
                .color(ChatColor.AQUA);

        return messageBuilder.create();
    }

    public static void sendFactionChatMessage(User sender, BaseComponent[] components) {
        FactionUserRelation relation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getByUser(sender);

        if (relation == null) {
            return;
        }

        Set<FactionUserRelation> users = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getIfPresentByFaction(relation.getFaction());

        for (FactionUserRelation user : users) {
            FactionUser target = user.getUser();
            Player player = Bukkit.getPlayerExact(target.getNick());

            if (player != null) {
                if (player.isOnline()) {
                    player.sendMessage(components);
                }
            }
        }
    }

    public static void sendGlobalChatMessage(User sender, BaseComponent[] components) {
        Bukkit.getOnlinePlayers()
                .stream()
                .filter(target -> {
                    User userTarget = CoreProvider.Cache.Local.USERS.provide().get(target.getName());

                    if (userTarget == null) {
                        return false;
                    }

                    PreferenceStatus targetStatus = CoreProvider.Cache.Local.USERS_PREFERENCES.provide()
                            .get(userTarget)
                            .getPreference(FactionsConstants.UserPreference.CHAT_GLOBAL, PreferenceStatus.ON);

                    return targetStatus.is(PreferenceStatus.ON);
                })
                .forEach(target -> {
                    if (sender.hasGroup(Group.MANAGER)) {
                        target.spigot().sendMessage(
                                ObjectArrays.concat(
                                        ObjectArrays.concat(
                                                TextComponent.fromLegacyText("\n"),
                                                components,
                                                BaseComponent.class
                                        ),
                                        TextComponent.fromLegacyText("\n"),
                                        BaseComponent.class
                                )
                        );
                    } else {
                        target.spigot().sendMessage(components);
                    }
                });
    }

    public static BaseComponent[] buildGlobalChatMessage(FactionUser user, String messageRaw) {

        Group group = user.getHandle().getHighestGroup();

        if (!user.getHandle().hasGroup(Group.GAME_MASTER) && !UserCooldowns.hasEnded(user.getHandle(), COOLDOWN_KEY_GLOGAL)) {
            Message.ERROR.send(user.getPlayer(), "Aguarde " + UserCooldowns.getSecondsLeft(user.getHandle(), COOLDOWN_KEY_GLOGAL) + " segundos para utilizar o chat novamente.");
            return null;
        }

        UserCooldowns.start(user.getHandle(), COOLDOWN_KEY_GLOGAL, 5, TimeUnit.SECONDS);

        ComponentBuilder messageBuilder = new ComponentBuilder("[g] ")
                .color(ChatColor.GRAY);

        FactionUserRelation relation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getIfPresentByUser(user.getHandle());

        if (relation != null) {

            messageBuilder
                    .append(String.format(
                            "[%s%s]",
                            relation.getRole().getSymbol(),
                            relation.getFaction().getTag()
                    ))
                    .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/f info " + relation.getFaction().getTag()))
                    .append(" ", ComponentBuilder.FormatRetention.NONE);

            List<RankIcon<?>> list = RankingFactory.FACTIONS_DAILY_TYCOON_RANKING.getRanking().getItems();

            if (list != null && !list.isEmpty()) {
                RankIcon<?> icon = list.get(0);

                if (relation.getFaction().equals(icon.getElement())) {


                }
            }

        }

        Map<Integer, Double> coinsTop = TopCurrencySubCommand.getCoinsTop();

        if (!coinsTop.isEmpty()) {

            Map.Entry<Integer, Double> entry = coinsTop.entrySet().iterator().next();

            if (entry.getKey().equals(user.getId())) {
                ComponentBuilder hover = new ComponentBuilder(user.getNick() + " é o mais rico do servidor.");

                messageBuilder
                        .append("[$]")
                        .color(ChatColor.DARK_GREEN)
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover.create()))
                        .append(" ", ComponentBuilder.FormatRetention.NONE);
            }
        }

        PlayerStat rank = McMMORanksTask.getTopSkill(null, 0);

        if (rank != null && user.getNick().equalsIgnoreCase(rank.name)) {
            ComponentBuilder hover = new ComponentBuilder(user.getNick() + " tem a maior soma de habilidades.");

            messageBuilder
                    .append("[⚒]")
                    .color(ChatColor.BLUE)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover.create()))
                    .append(" ", ComponentBuilder.FormatRetention.NONE);
        }


        if (group != null && !group.getTag().isEmpty()) {
            messageBuilder
                    .append(String.format("[%s] ", group.getTag()))
                    .color(group.getColor());
        }

        ComponentBuilder hoverBuilder = new ComponentBuilder(user.getHandle().getHighestGroup().getDisplayTag(user.getHandle().getNick()))
                .append("\n")
                .append(MessageUtils.translateColorCodes(
                        Joiner.on("\n").join(FactionUserUtils.getDescription(user))
                ));

        messageBuilder
                .append(user.getNick())
                .color(ChatColor.WHITE)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverBuilder.create()))
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, user.getNick()))
                .append(": ", ComponentBuilder.FormatRetention.NONE)
                .color(ChatColor.WHITE);

        String playerMessage;

        if (user.getHandle().hasGroup(Group.GAME_MASTER)) {
            playerMessage = MessageUtils.translateColorCodes(messageRaw);
        } else {
            playerMessage = MessageUtils.stripColor(
                    MessageUtils.translateColorCodes(messageRaw),
                    ChatColor.BLACK,
                    ChatColor.DARK_BLUE,
                    ChatColor.DARK_GRAY,
                    ChatColor.LIGHT_PURPLE,
                    ChatColor.YELLOW,
                    ChatColor.BOLD,
                    ChatColor.UNDERLINE,
                    ChatColor.ITALIC,
                    ChatColor.RESET,
                    ChatColor.MAGIC,
                    ChatColor.STRIKETHROUGH
            );
        }

        String stripedMessage = MessageUtils.stripColor(playerMessage);

        if (!user.getHandle().hasGroup(FactionsProvider.getSettings().getCanUseChatColor())) {
            playerMessage = stripedMessage;
        }

        if (stripedMessage.length() > 0) {

            if (!user.getHandle().hasGroup(Group.GAME_MASTER) && isSimilar(user.getHandle().getId(), stripedMessage)) {
                Message.ERROR.send(user.getPlayer(), "Você não pode enviar uma mensagem tão parecida com a anterior.");
                return null;
            }

            messageBuilder.append(TextComponent.fromLegacyText(ChatColor.GRAY + playerMessage), ComponentBuilder.FormatRetention.NONE);

            return messageBuilder.create();
        }

        return null;
    }

    public static void sendLocalChatMessage(FactionUser user, String messageRaw) {

        Player player = user.getPlayer();

        PreferenceStatus status = CoreProvider.Cache.Local.USERS_PREFERENCES.provide()
                .get(user.getHandle())
                .getPreference(FactionsConstants.UserPreference.CHAT_LOCAL);

        if (status.is(PreferenceStatus.OFF)) {
            Message.ERROR.send(player, "Ops, você está com o chat local desabilitado. Utilize &f/toggle &cpara habilita-lo novamente.");
            return;
        }

        Claim claim = LandUtils.getClaim(player.getLocation());

        if (claim == null && !user.getHandle().hasGroup(Group.MODERATOR) && !UserCooldowns.hasEnded(user.getHandle(), COOLDOWN_KEY_LOCAL)) {
            Message.ERROR.send(player, "Aguarde " + UserCooldowns.getSecondsLeft(user.getHandle(), COOLDOWN_KEY_LOCAL) + " segundos para utilizar o chat novamente.");
            return;
        }

        UserCooldowns.start(user.getHandle(), COOLDOWN_KEY_LOCAL, 2, TimeUnit.SECONDS);

        Group group = user.getHandle().getHighestGroup();

        StringBuilder messageBuilder = new StringBuilder();

        messageBuilder.append(ChatColor.YELLOW).append("[l] ");

        FactionUserRelation relation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getIfPresentByUser(user.getHandle());

        if (relation != null) {

            messageBuilder
                    .append(String.format(
                            "[%s%s] ",
                            relation.getRole().getSymbol(),
                            relation.getFaction().getTag()
                    ));

        }

        if (group != null) {
            group.getTag();
            if (!group.getTag().isEmpty()) {
                messageBuilder.append(group.getColor()).append(String.format("[%s] ", group.getTag()));
            }
        }

        messageBuilder.append(ChatColor.WHITE).append(player.getName()).append(": ");

        String message = MessageUtils.stripColor(MessageUtils.translateColorCodes(messageRaw));

        if (message.length() > 0) {

            if (!user.getHandle().hasGroup(Group.GAME_MASTER) && isSimilar(user.getHandle().getId(), message)) {
                Message.ERROR.send(Bukkit.getPlayerExact(user.getNick()), "Você não pode enviar uma mensagem tão parecida com a anterior.");
                return;
            }

            messageBuilder.append(ChatColor.YELLOW).append(message);

            player.sendMessage(messageBuilder.toString());

            Set<Player> getNearbyPlayers = player.getNearbyEntities(25, 25, 25)
                    .stream()
                    .filter(entity -> entity instanceof Player)
                    .map(entity -> (Player) entity)
                    .collect(Collectors.toSet());

            if (getNearbyPlayers.size() > 0) {
                getNearbyPlayers.stream()
                        .filter(target -> {
                            User userTarget = CoreProvider.Cache.Local.USERS.provide().get(target.getName());

                            if (userTarget == null) {
                                return false;
                            }

                            PreferenceStatus targetStatus = CoreProvider.Cache.Local.USERS_PREFERENCES.provide()
                                    .get(userTarget)
                                    .getPreference(FactionsConstants.UserPreference.CHAT_LOCAL, PreferenceStatus.ON);

                            return targetStatus.is(PreferenceStatus.ON);
                        })
                        .forEach(target -> {
                            target.sendMessage(messageBuilder.toString());
                        });
            } else {
                player.sendMessage(ChatColor.YELLOW + "Não há jogadores perto de você. :(");
            }
        }
    }

    private static boolean isSimilar(int id, String message) {
        message = MessageUtils.stripColor(message);

        String last = LAST_MESSAGE.getOrDefault(id, null);
        boolean result = (last != null
                && (last.equalsIgnoreCase(message) || StringSimilarity.getSimilarity(last, message) >= 0.9D));

        LAST_MESSAGE.put(id, message);
        return result;
    }
}
