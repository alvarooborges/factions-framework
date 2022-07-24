package net.hyze.factions.framework.commands.factioncommand.subcommands;

import lombok.Getter;
import net.hyze.core.shared.misc.cooldowns.UserCooldowns;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.CooldownConstants;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.commands.factioncommand.FactionSubCommand;
import net.hyze.factions.framework.faction.relation.user.FactionRole;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.lands.Claim;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Objects;

public class FlySubCommand extends FactionSubCommand {

    @Getter
    private final CommandRelationType commandRelationType = CommandRelationType.MISC;

    public FlySubCommand() {
        super("voar", FactionRole.RECRUIT);
    }

    @Override
    public void onCommand(Player player, FactionUser user, FactionUserRelation relation, String[] args) {

        if (relation.getFaction().isUnderAttack()) {
            Message.ERROR.send(player, "Você não pode voar enquanto sua facção estiver sob ataque.");
            return;
        }

        Location playerLocation = player.getLocation();

        final int playerChunkX = playerLocation.getBlockX() >> 4;
        final int playerChunkZ = playerLocation.getBlockZ() >> 4;

        Claim claim = FactionsProvider.Cache.Local.LANDS.provide().get(playerChunkX, playerChunkZ, Claim.class);

        if (claim == null || !Objects.equals(claim.getFactionId(), relation.getFaction().getId())) {
            Message.ERROR.send(player, "Você só pode voar em terras de sua facção.");
            return;
        }

        if (player.isFlying() || player.getAllowFlight()) {
            Message.SUCCESS.send(player, "Modo voo desabilitado.");
            user.stopFlyingTask();
            return;
        }

        if (!UserCooldowns.hasEnded(user.getHandle(), CooldownConstants.Fly.FLY_COOLDOWN_KEY)) {
            String cooldown = UserCooldowns.getFormattedTimeLeft(user.getHandle(), CooldownConstants.Fly.FLY_COOLDOWN_KEY);

            Message.ERROR.send(player, String.format("Aguarde %s para voar novamente.", cooldown));
            return;
        }

        Message.SUCCESS.send(player, "Agora você pode voar!");

        user.startFlyingTask();

    }
}
