package net.hyze.factions.framework.commands.factioncommand.subcommands.claim;

import lombok.Getter;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.misc.utils.TimeCode;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.economy.Currency;
import net.hyze.economy.EconomyAPI;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.commands.factioncommand.FactionSubCommand;
import net.hyze.factions.framework.echo.packets.ClaimUpdatePacket;
import net.hyze.factions.framework.events.UserProtectEvent;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.permission.FactionPermission;
import net.hyze.factions.framework.faction.relation.user.FactionRole;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.lands.Claim;
import net.hyze.factions.framework.lands.LandState;
import net.hyze.factions.framework.lands.Zone;
import net.hyze.factions.framework.misc.utils.LandUtils;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ProtectSubCommand extends FactionSubCommand {

    @Getter
    private final CommandRelationType commandRelationType = CommandRelationType.FACTION_TO_ADM;

    public ProtectSubCommand() {
        super("proteger", FactionRole.RECRUIT);
    }

    @Override
    public void onCommand(Player player, FactionUser user, FactionUserRelation relation, String[] args) {

        Faction faction = relation.getFaction();
        if (!FactionPermission.COMMAND_CLAIM.allows(faction, user)) {
            Message.ERROR.send(player, "Você não tem permissão para dominar temporariamente.");
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, -9);

        if (faction.getCreatedAt().after(calendar.getTime())) {
            Message.ERROR.send(player, "Sua facção deve ter pelo menos 9 horas de existência.");
            return;
        }

        Location playerLocation = player.getLocation();

        UserProtectEvent protectEvent = new UserProtectEvent(user, playerLocation);

        Bukkit.getPluginManager().callEvent(protectEvent);

        if (protectEvent.isCancelled()) {
            return;
        }

        Zone zone = LandUtils.getZone(playerLocation);

        if (zone != null && zone.getType() != Zone.Type.NEUTRAL) {
            Message.ERROR.send(player, String.format("Você só pode dominar uma %s&c.", LandState.FREE_LAND.getName()));
            return;
        }

        Claim claim = LandUtils.getClaim(playerLocation);

        if (claim != null) {
            Message.ERROR.send(player, "Esta terra já está dominada.");
            return;
        }

        Set<Claim> temporaryClaims = LandUtils.getTemporaryClaims(relation.getFaction());

        int limit = FactionsProvider.getSettings().getTemporaryClaimLimit();
        if (temporaryClaims.size() >= limit) {
            Message.ERROR.send(player, String.format("Sua facção atingiu o limite de %s terras temporárias.", limit));
            return;
        }

        double balance = EconomyAPI.get(user.getHandle(), Currency.COINS);

        double price = FactionsProvider.getSettings().getTemporaryClaimPrice();

        if (balance < price) {
            Message.ERROR.send(player, String.format(
                    "&cVocê precisa de %s para dominar temporariamente uma terra .",
                    Currency.COINS.format(price)
            ));
            return;
        }

        final int playerChunkX = playerLocation.getBlockX() >> 4;
        final int playerChunkZ = playerLocation.getBlockZ() >> 4;

        Claim newClaim = new Claim(
                relation.getFaction().getId(),
                new Date(),
                true,
                CoreProvider.getApp().getId(),
                playerChunkX,
                playerChunkZ
        );

        boolean result = FactionsProvider.Repositories.CLAIMS.provide().insert(newClaim);

        if (!result) {
            Message.ERROR.send(player, "Algo de errado aconteceu, tente novamente.");
            return;
        }

        EconomyAPI.remove(user.getHandle(), Currency.COINS, price);

        CoreProvider.Redis.ECHO.provide().publish(new ClaimUpdatePacket(user.getId(), newClaim, ClaimUpdatePacket.Reason.CLAIM));


        int minutes = FactionsProvider.getSettings().getTemporaryClaimMinutes();
        String minutesStr = TimeCode.getFormattedTimeLeft(TimeUnit.MINUTES.toMillis(minutes));

        String msg = "\n&aVocê dominou esta terra temporariamente.\n" +
                "&aEla será desprotegida automaticamente em &e" + minutesStr + "&a.\n";

        Message.SUCCESS.send(player, msg);
    }
}
