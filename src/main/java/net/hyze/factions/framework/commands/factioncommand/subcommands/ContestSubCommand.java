package net.hyze.factions.framework.commands.factioncommand.subcommands;

import lombok.Getter;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.commands.factioncommand.FactionSubCommand;
import net.hyze.factions.framework.echo.packets.ClaimUpdatePacket;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.permission.FactionPermission;
import net.hyze.factions.framework.faction.relation.user.FactionRole;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.lands.Claim;
import net.hyze.factions.framework.misc.utils.FactionUtils;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.Set;

public class ContestSubCommand extends FactionSubCommand {

    @Getter
    private final CommandRelationType commandRelationType = CommandRelationType.FACTION_TO_ADM;

    public ContestSubCommand() {
        super("contestar", FactionRole.RECRUIT);
    }

    @Override
    public void onCommand(Player player, FactionUser user, FactionUserRelation relation, String[] args) {
        if (!FactionPermission.COMMAND_CLAIM.allows(relation.getFaction(), user)) {
            Message.ERROR.send(player, "Você não tem permissão para contestar terras para sua facção.");
            return;
        }

        Faction faction = relation.getFaction();

        Location playerLocation = player.getLocation();

        final int playerChunkX = playerLocation.getBlockX() >> 4;
        final int playerChunkZ = playerLocation.getBlockZ() >> 4;

        Claim claim = FactionsProvider.Cache.Local.LANDS.provide().get(playerChunkX, playerChunkZ, Claim.class);

        if (claim == null || claim.getFaction().equals(faction)) {
            Message.ERROR.send(player, "Você só pode contestar terras de outras facções.");
            return;
        }

        if (claim.isTemporary()) {
            Message.ERROR.send(player, "Você não pode contestar terras temporárias.");
            return;
        }

        Faction victim = claim.getFaction();

        if (FactionUtils.isAlly(faction, victim)) {
            Message.ERROR.send(player, "Que feio! Você está tentando contestar terras de aliados!?");
            return;
        }

        Set<Claim> victimClaims = FactionsProvider.Cache.Local.LANDS.provide().get(victim);
        long contestedCount = victimClaims.stream().filter(Claim::isContested).count();

        if (FactionUtils.getPower(victim) >= victimClaims.size() - contestedCount) {
            Message.ERROR.send(player, String.format(
                    "A facção %s possui poder suficiente para ficar com esta terra.",
                    victim.getTag()
            ));
            return;
        }

        Set<Claim> claims = FactionsProvider.Cache.Local.LANDS.provide().get(faction);

        if (FactionUtils.getPower(faction) < claims.size()) {
            Message.ERROR.send(player, "Sua facção não tem poder para contestar esta terra.");
            return;
        }

        if (claim.isContested()) {
            Message.ERROR.send(player, "Esta terra já está contestada.");
            return;
        }

        claim.setContestantId(faction.getId());
        claim.setContestedAt(new Date());

        FactionsProvider.Repositories.CLAIMS.provide().update(claim);

        CoreProvider.Redis.ECHO.provide().publish(new ClaimUpdatePacket(
                user.getId(), claim, ClaimUpdatePacket.Reason.CONTEST
        ));

        Message.SUCCESS.send(player, "Você contestou esta terra!");
    }
}
