package net.hyze.factions.framework.echo.listeners;

import dev.utils.echo.IEchoListener;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.messages.MessageUtils;
import net.hyze.core.shared.misc.utils.NumberUtils;
import net.hyze.core.shared.user.User;
import net.hyze.core.shared.world.location.SerializedLocation;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.echo.packets.*;
import net.hyze.factions.framework.echo.packets.relation.FactionRelationCreatedPacket;
import net.hyze.factions.framework.echo.packets.relation.FactionRelationDeletedPacket;
import net.hyze.factions.framework.echo.packets.relation.FactionRelationInvitePacket;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.relation.faction.FactionRelation;
import net.hyze.factions.framework.faction.relation.user.FactionRole;
import net.hyze.factions.framework.lands.Claim;
import net.hyze.factions.framework.misc.scoreboard.ScoreboardManager;
import net.hyze.factions.framework.misc.utils.FactionUserUtils;
import net.hyze.factions.framework.misc.utils.FactionUtils;
import net.hyze.factions.framework.misc.utils.LandUtils;
import net.hyze.factions.framework.user.FactionUser;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.greenrobot.eventbus.Subscribe;

import java.util.Set;

public class FactionEchoListener implements IEchoListener {

    @Subscribe
    public void on(FactionUnderAttackPacket packet) {
        Faction faction = FactionsProvider.Cache.Local.FACTIONS.provide().getIfPresent(packet.getFactionId());

        if (faction == null) {
            return;
        }

        faction.setUnderAttackAt(packet.getUnderAttackAt());

        Set<FactionUser> users = FactionUtils.getUsers(faction, true, true);

        ScoreboardManager.update(users, ScoreboardManager.Slot.FACTION);
    }

    @Subscribe
    public void on(FactionCreatedPacket packet) {
        FactionsProvider.Cache.Local.FACTIONS.provide().put(packet.getFaction());
    }

    @Subscribe
    public void on3(FactionDisbandPacket packet) {
        Set<Claim> oldClaims = FactionsProvider.Cache.Local.LANDS.provide().get(packet.getFaction());

        FactionsProvider.Cache.Local.FACTIONS.provide().remove(packet.getFaction());
        FactionsProvider.Cache.Local.USERS_RELATIONS.provide().removeByFaction(packet.getFaction());
        FactionsProvider.Cache.Local.LANDS.provide().remove(packet.getFaction());

        Set<FactionUser> usersInsideLand = LandUtils.getUsersInsideLands(oldClaims.toArray(new Claim[0]));

        ScoreboardManager.update(usersInsideLand, ScoreboardManager.Slot.TITLE);
    }

    @Subscribe
    public void on(ClaimUpdatePacket packet) {
        Claim claim = packet.getClaim();

        Faction faction = claim.getFaction();

        if (packet.isLocal()) {

            SerializedLocation home = faction.getHome();
            if (home != null) {

                int blockX = NumberUtils.floorInt(home.getX());
                int blockZ = NumberUtils.floorInt(home.getZ());

                int chunkX = blockX >> 4;
                int chunkZ = blockZ >> 4;

                if (chunkX == claim.getChunkX() && chunkZ == claim.getChunkZ()) {
                    User user = CoreProvider.Cache.Local.USERS.provide().get(packet.getUserId());

                    FactionUtils.setFactionHome(FactionUserUtils.getUser(user), faction, null);
                }
            }

        }

        if (packet.getReason() == ClaimUpdatePacket.Reason.CLAIM) {
            FactionsProvider.Cache.Local.LANDS.provide().put(claim);
        } else if (packet.getReason() == ClaimUpdatePacket.Reason.UNCLAIM) {
            FactionsProvider.Cache.Local.LANDS.provide().remove(claim);
        }

        Set<FactionUser> usersInsideLand = LandUtils.getUsersInsideLands(claim);

        ScoreboardManager.update(usersInsideLand, ScoreboardManager.Slot.FACTION);

        if (faction != null) {
            ScoreboardManager.update(
                    FactionUtils.getUsers(claim.getFaction(), true, true),
                    ScoreboardManager.Slot.FACTION
            );
        }
    }

    @Subscribe
    public void on(FactionRelationInvitePacket packet) {
        FactionUser leader = FactionUtils.getLeader(packet.getTarget());

        if (leader == null || leader.getPlayer() == null || !leader.getPlayer().isOnline()) {
            return;
        }

        ComponentBuilder builder = new ComponentBuilder("\nSua facção foi convidada para uma aliança com a facção " + packet.getSender().getTag().toUpperCase())
                .color(ChatColor.YELLOW)
                .append("\n")
                .append("Clique para ")
                .color(ChatColor.YELLOW)
                .append("ACEITAR")
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f ally " + packet.getSender().getTag()))
                .event(new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        new BaseComponent[]{
                                new TextComponent(MessageUtils.translateColorCodes(String.format(
                                        "&aClique e aceite o convite \n&ade aliança de %s.", packet.getSender().getTag().toUpperCase()
                                )))
                        }
                ))
                .color(ChatColor.GREEN)
                .bold(true)
                .append(".\n")
                .color(ChatColor.YELLOW);

        leader.getPlayer().spigot().sendMessage(builder.create());
    }

    @Subscribe
    public void on(FactionRelationCreatedPacket packet) {
        FactionsProvider.Cache.Local.FACTIONS_RELATIONS.provide().clear();

        FactionsProvider.Repositories.FACTIONS_RELATIONS.provide().fetch()
                .forEach(relation -> FactionsProvider.Cache.Local.FACTIONS_RELATIONS.provide().put(relation));

        FactionUser leader = FactionUtils.getLeader(packet.getFactionRight());

        if (leader == null || leader.getPlayer() == null || !leader.getPlayer().isOnline()) {
            return;
        }

        if (packet.getType() == FactionRelation.Type.ALLY) {
            Message.SUCCESS.send(leader.getPlayer(), String.format("Sua facção começou uma aliança com a facção %s&a.", packet.getFactionLeft().getDisplayName()));
        } else {
            Message.INFO.send(leader.getPlayer(), String.format("Sua facção começou uma rivalidade com a facção %s&a.", packet.getFactionLeft().getDisplayName()));
        }

        Set<FactionUser> usersInsideClaims = LandUtils.getUsersInsideClaims(
                packet.getFactionLeft(),
                packet.getFactionRight()
        );

        ScoreboardManager.update(usersInsideClaims, ScoreboardManager.Slot.FACTION, ScoreboardManager.Slot.TITLE);
    }

    @Subscribe
    public void on(FactionRelationDeletedPacket packet) {
        FactionsProvider.Cache.Local.FACTIONS_RELATIONS.provide().clear();

        FactionsProvider.Repositories.FACTIONS_RELATIONS.provide().fetch()
                .forEach(relation -> FactionsProvider.Cache.Local.FACTIONS_RELATIONS.provide().put(relation));

        FactionUser leader = FactionUtils.getLeader(packet.getFactionRight());

        if (leader == null || leader.getPlayer() == null || !leader.getPlayer().isOnline()) {
            return;
        }

        if (packet.getType() == FactionRelation.Type.ALLY) {
            Message.INFO.send(leader.getPlayer(), String.format("A aliança com a facção %s &efoi desfeita.", packet.getFactionLeft().getDisplayName()));
        } else {
            Message.INFO.send(leader.getPlayer(), String.format("A rivalidade com a facção %s &efoi desfeita.", packet.getFactionLeft().getDisplayName()));
        }

        Set<FactionUser> usersInsideClaims = LandUtils.getUsersInsideClaims(
                packet.getFactionLeft(),
                packet.getFactionRight()
        );

        ScoreboardManager.update(usersInsideClaims, ScoreboardManager.Slot.FACTION, ScoreboardManager.Slot.TITLE);
    }

    @Subscribe
    public void on(FactionDefineBasePacket packet) {
        if (packet.getFaction() != null) {
            packet.getFaction().setHome(packet.getHome());

            FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(packet.getUserId());

            ComponentBuilder builder = new ComponentBuilder("")
                    .append(FactionUserUtils.getChatComponents(user));

            if (packet.getHome() == null) {
                builder.append(" desfez a base da facção.", ComponentBuilder.FormatRetention.NONE)
                        .color(ChatColor.YELLOW);
            } else {
                builder.append(" definiu a base da facção.", ComponentBuilder.FormatRetention.NONE)
                        .color(ChatColor.YELLOW);
            }

            FactionUtils.broadcast(packet.getFaction(), builder.create(), true, FactionRole.values());
        }
    }

    @Subscribe
    public void on(FactionSeasonPointsUpdatePacket packet) {

        packet.getPoints()
                .forEach((factionId, points) -> {
                    Faction faction = FactionsProvider.Cache.Local.FACTIONS.provide().get(factionId);

                    if (faction != null) {
                        faction.setPoints(points);
                    }
                });

    }
}
