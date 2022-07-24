package net.hyze.factions.framework.echo.listeners;

import dev.utils.echo.IEchoListener;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.echo.packets.user.connect.ConnectReason;
import net.hyze.core.shared.messages.MessageUtils;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.TeleportManager;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.echo.packets.*;
import net.hyze.factions.framework.faction.relation.user.FactionRole;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.lands.Claim;
import net.hyze.factions.framework.misc.chat.ChatManager;
import net.hyze.factions.framework.misc.scoreboard.ScoreboardManager;
import net.hyze.factions.framework.misc.utils.FactionUserUtils;
import net.hyze.factions.framework.misc.utils.FactionUtils;
import net.hyze.factions.framework.misc.utils.LandUtils;
import net.hyze.factions.framework.user.FactionUser;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.greenrobot.eventbus.Subscribe;

import java.util.Collections;
import java.util.Set;

public class UserEchoListener implements IEchoListener {

    @Subscribe
    public void on(UserJoinedFactionPacket packet) {

        if (packet.getReason() == UserJoinedFactionPacket.Reason.INVITATION) {

            FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(packet.getUserId());

            ComponentBuilder builder = new ComponentBuilder("");
            builder.append(user.getHandle().getHighestGroup().getDisplayTag(user.getNick()))
                    .append(" entrou na sua facção!", ComponentBuilder.FormatRetention.NONE)
                    .color(ChatColor.GREEN)
                    .append("\n")
                    .append("O poder dele passará a ser contabilizado após 20 minutos.");

            FactionUtils.broadcast(packet.getFaction(), builder.create(), true);
        }

        FactionsProvider.Cache.Local.USERS_RELATIONS.provide().refreshByUserId(packet.getUserId());

        Set<FactionUser> users = FactionUtils.getUsers(packet.getFaction(), true, true);

        ScoreboardManager.update(users, ScoreboardManager.Slot.FACTION, ScoreboardManager.Slot.TITLE);
    }

    @Subscribe
    public void on(UserLeftFactionPacket packet) {
        User handle = CoreProvider.Cache.Local.USERS.provide().get(packet.getUserId());

        FactionUser user = FactionsProvider.Cache.Local.USERS.provide().getIfPresent(packet.getUserId());

        boolean userIsOnline = user != null && user.isOnline();

        FactionsProvider.Cache.Local.USERS_RELATIONS.provide().refreshByUserId(handle.getId());

        if (userIsOnline) {
            Claim claim = LandUtils.getClaim(user.getPlayer().getLocation());

            if (claim != null) {
                TeleportManager.teleport(handle, AppType.FACTIONS_SPAWN, ConnectReason.PLUGIN);
            } else {
                ScoreboardManager.update(user, ScoreboardManager.Slot.FACTION, ScoreboardManager.Slot.TITLE);
            }
        }

        Set<FactionUser> users = FactionUtils.getUsers(packet.getFaction(), true, true);

        ScoreboardManager.update(users, ScoreboardManager.Slot.FACTION, ScoreboardManager.Slot.TITLE);

        ComponentBuilder builder = new ComponentBuilder("")
                .append(handle.getHighestGroup().getDisplayTag(handle.getNick()))
                .color(ChatColor.RED);

        if (packet.getReason() == UserLeftFactionPacket.Reason.LEAVE) {
            builder.reset()
                    .append(" saiu da sua facção.", ComponentBuilder.FormatRetention.NONE)
                    .color(ChatColor.RED);

            FactionUtils.broadcast(packet.getFaction(), builder.create(), true);

        } else if (packet.getReason() == UserLeftFactionPacket.Reason.KICK) {
            builder.reset()
                    .append(" foi expulso da sua facção.", ComponentBuilder.FormatRetention.NONE)
                    .color(ChatColor.RED);

            FactionUtils.broadcast(packet.getFaction(), builder.create(), true);

            if (userIsOnline) {
                Message.ERROR.send(user.getPlayer(), "Você foi expulso da facção.");
            }
        }
    }

    @Subscribe
    public void on(UserInvitedFactionPacket packet) {
        User user = CoreProvider.Cache.Local.USERS.provide().getIfPresent(packet.getTargetId());

        if (user == null) {
            return;
        }

        Player player = Bukkit.getPlayerExact(user.getNick());

        if (player == null || !player.isOnline()) {
            return;
        }

        ComponentBuilder builder = new ComponentBuilder("Você foi convidado para a facção " + packet.getFaction().getTag().toUpperCase())
                .color(ChatColor.YELLOW)
                .append("\n")
                .append("Clique para ")
                .color(ChatColor.YELLOW)
                .append("ACEITAR")
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f aceitar " + packet.getFaction().getTag()))
                .event(new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        new BaseComponent[]{
                            new TextComponent(MessageUtils.translateColorCodes(String.format(
                                    "&aClique e aceite o convite para\n&aentrar na facção %s.", packet.getFaction().getTag().toUpperCase()
                            )))
                        }
                ))
                .color(ChatColor.GREEN)
                .bold(true)
                .append(".")
                .color(ChatColor.YELLOW);

        player.spigot().sendMessage(builder.create());
    }

    @Subscribe
    public void on(UserRankUpdatedPacket packet) {
        FactionUserRelation relation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().refreshByUserId(packet.getUserId());

        if (packet.getNewRank() != FactionRole.LEADER && packet.getOldRank() != FactionRole.LEADER) {

            FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(packet.getUserId());

            ComponentBuilder builder = new ComponentBuilder("")
                    .append(FactionUserUtils.getChatComponents(user));

            if (packet.getNewRank().isHigher(packet.getOldRank())) {
                builder.append(" foi promovido a ", ComponentBuilder.FormatRetention.NONE)
                        .color(ChatColor.GREEN)
                        .append(packet.getNewRank().getDisplayName())
                        .append("!");
            } else {
                builder.append(" foi rebaixado a ", ComponentBuilder.FormatRetention.NONE)
                        .color(ChatColor.RED)
                        .append(packet.getNewRank().getDisplayName())
                        .append("!");
            }

            FactionUtils.broadcast(relation.getFaction(), builder.create(), true);
        }
    }

    @Subscribe
    public void on(UserSpokePacket packet) {
        User user = CoreProvider.Cache.Local.USERS.provide().get(packet.getUserId());

        switch (packet.getChat()) {
            case GLOBAL:

                if (FactionsProvider.getSettings().getDisabledChats() != null
                        && FactionsProvider.getSettings().getDisabledChats().contains(UserSpokePacket.Chat.GLOBAL)) {
                    break;
                }
                ChatManager.sendGlobalChatMessage(user, packet.getComponents());
                break;

            case FACTION:

                if (FactionsProvider.getSettings().getDisabledChats() != null
                        && FactionsProvider.getSettings().getDisabledChats().contains(UserSpokePacket.Chat.FACTION)) {
                    break;
                }
                ChatManager.sendFactionChatMessage(user, packet.getComponents());
                break;

            case ALLIANCE:

                if (FactionsProvider.getSettings().getDisabledChats() != null
                        && FactionsProvider.getSettings().getDisabledChats().contains(UserSpokePacket.Chat.ALLIANCE)) {
                    break;
                }
                ChatManager.sendAllianceChatMessage(user, packet.getComponents());
                break;
        }
    }

    @Subscribe
    public void on(UserPowerUpdatedPacket packet) {
        FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(packet.getUserId());

        user.getStats().setPower(packet.getNewPower());

        if (user.isOnline()) {
            ScoreboardManager.update(Collections.singleton(user), ScoreboardManager.Slot.POWER);
        }

        FactionUserRelation relation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getIfPresentByUser(user);

        if (relation != null) {
            Set<FactionUser> users = FactionUtils.getUsers(relation.getFaction(), true, true);
            ScoreboardManager.update(users, ScoreboardManager.Slot.FACTION);
        }
    }

    @Subscribe
    public void on(UserAdditionalMaxPowerUpdatedPacket packet) {
        FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(packet.getUserId());

        user.getStats().setAdditionalMaxPower(packet.getNewAdditionalMaxPower());

        ScoreboardManager.update(Collections.singleton(user), ScoreboardManager.Slot.POWER);

        FactionUserRelation relation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getIfPresentByUser(user);

        if (relation != null) {
            Set<FactionUser> users = FactionUtils.getUsers(relation.getFaction(), true, true);
            ScoreboardManager.update(users, ScoreboardManager.Slot.FACTION);
        }
    }
}
