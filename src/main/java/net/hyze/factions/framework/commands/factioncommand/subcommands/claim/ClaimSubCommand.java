package net.hyze.factions.framework.commands.factioncommand.subcommands.claim;

import com.google.common.collect.Sets;
import lombok.Getter;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.commands.factioncommand.FactionSubCommand;
import net.hyze.factions.framework.echo.packets.ClaimUpdatePacket;
import net.hyze.factions.framework.events.UserClaimEvent;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.permission.FactionPermission;
import net.hyze.factions.framework.faction.relation.user.FactionRole;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.lands.Claim;
import net.hyze.factions.framework.lands.LandState;
import net.hyze.factions.framework.lands.Zone;
import net.hyze.factions.framework.misc.utils.FactionUtils;
import net.hyze.factions.framework.misc.utils.LandUtils;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;

public class ClaimSubCommand extends FactionSubCommand {

    @Getter
    private final CommandRelationType commandRelationType = CommandRelationType.FACTION_TO_ADM;

    public ClaimSubCommand() {
        super("dominar", FactionRole.RECRUIT);
    }

    @Override
    public void onCommand(Player player, FactionUser user, FactionUserRelation relation, String[] args) {

        if (!FactionPermission.COMMAND_CLAIM.allows(relation.getFaction(), user)) {
            Message.ERROR.send(player, "Você não tem permissão para dominar terras para sua facção.");
            return;
        }

        Location playerLocation = player.getLocation();

        final int playerChunkX = playerLocation.getBlockX() >> 4;
        final int playerChunkZ = playerLocation.getBlockZ() >> 4;

        Runnable claimer = () -> {

            UserClaimEvent claimEvent = new UserClaimEvent(user, playerLocation);

            Bukkit.getPluginManager().callEvent(claimEvent);

            if (claimEvent.isCancelled()) {
                return;
            }

            Claim claim = new Claim(
                    relation.getFaction().getId(),
                    new Date(),
                    false,
                    CoreProvider.getApp().getId(),
                    playerChunkX,
                    playerChunkZ
            );

            boolean success = FactionsProvider.Repositories.CLAIMS.provide().insert(claim);

            if (!success) {
                Message.ERROR.send(player, "Algo de errado aconteceu, tente novamente.");
                return;
            }

            CoreProvider.Redis.ECHO.provide().publish(new ClaimUpdatePacket(user.getId(), claim, ClaimUpdatePacket.Reason.CLAIM));

            String msg = "\n&aVocê dominou esta terra permanentemente.\n" +
                    "&aUse &e/f abandonar &apara abandoná-la.\n" +
                    "&aUse &e/f proteger &apara dominar temporariamente.\n";

            Message.SUCCESS.send(player, msg);

        };

        BiFunction<Faction, Integer, Boolean> hasPower = (faction, power) -> {
            return FactionUtils.getPower(faction) >= power;
        };

        Set<Claim> permanentClaims = LandUtils.getPermanentClaims(relation.getFaction());

        Zone zone = LandUtils.getZone(playerLocation);

        if (zone != null) {
            Message.ERROR.send(player, String.format("Você só pode dominar uma %s&c.", LandState.FREE_LAND.getName()));
            return;
        }

        Claim claim = LandUtils.getClaim(playerLocation);

        if (claim != null) {
            Message.ERROR.send(player, "Esta terra já está dominada.");
            return;
        }

        if (!hasPower.apply(relation.getFaction(), permanentClaims.size() + 1)) {
            Message.ERROR.send(player, "Sua facção não tem poder para dominar esta terra.");
            return;
        }

        Set<Claim> around = Sets.newHashSet();

        int claimPadding = 4;

        for (int i = -claimPadding; i <= claimPadding; i++) {
            for (int j = -claimPadding; j <= claimPadding; j++) {
                around.add(FactionsProvider.Cache.Local.LANDS.provide().get(playerChunkX + i, playerChunkZ + j, Claim.class));
            }
        }

        boolean otherOwnsClaimAround = around.stream()
                .filter(Objects::nonNull)
                .filter(c -> !c.isTemporary())
                .anyMatch(c -> !c.getFactionId().equals(relation.getFaction().getId()));

        if (otherOwnsClaimAround) {
            Message.ERROR.send(player, "Você não pode dominar terras perto de terras de outra facção.");
            return;
        }

        if (permanentClaims.isEmpty()) {
            claimer.run();
            return;
        }

        Runnable separateClaimErrorSender = () -> {
            Message.ERROR.send(player, "Você não pode dominar terras separadas de terras já dominadas pela sua facção.");
        };

        boolean haveAnotherWorldClaim = permanentClaims.stream().anyMatch(c -> !c.getAppId().equals(CoreProvider.getApp().getId()));

        if (haveAnotherWorldClaim) {
            separateClaimErrorSender.run();
            return;
        }

        Set<Claim> beside = Sets.newHashSet(
                FactionsProvider.Cache.Local.LANDS.provide().get(playerChunkX + 1, playerChunkZ, Claim.class),
                FactionsProvider.Cache.Local.LANDS.provide().get(playerChunkX - 1, playerChunkZ, Claim.class),
                FactionsProvider.Cache.Local.LANDS.provide().get(playerChunkX, playerChunkZ + 1, Claim.class),
                FactionsProvider.Cache.Local.LANDS.provide().get(playerChunkX, playerChunkZ - 1, Claim.class)
        );

        boolean ownsClaimBeside = beside.stream()
                .filter(Objects::nonNull)
                .anyMatch(c -> c.getFactionId().equals(relation.getFaction().getId()));

        if (!ownsClaimBeside) {
            separateClaimErrorSender.run();
            return;
        }

        claimer.run();
    }
}
