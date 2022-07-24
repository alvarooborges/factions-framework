package net.hyze.factions.framework.commands.factioncommand.subcommands.adminsubcommand.data;

import lombok.Getter;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.commands.factioncommand.FactionSubCommand;
import net.hyze.factions.framework.echo.packets.ClaimUpdatePacket;
import net.hyze.factions.framework.lands.Claim;
import net.hyze.factions.framework.spawners.SpawnerType;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.Location;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class AdminAbandonSubCommand extends FactionSubCommand implements GroupCommandRestrictable {

    @Getter
    private final Group group = Group.GAME_MASTER;

    public AdminAbandonSubCommand() {
        super("abandonar");
    }

    @Override
    public void onCommand(Player player, FactionUser user, String[] args) {
        Location playerLocation = player.getLocation();

        final int playerChunkX = playerLocation.getBlockX() >> 4;
        final int playerChunkZ = playerLocation.getBlockZ() >> 4;

        Claim claim = FactionsProvider.Cache.Local.LANDS.provide().get(playerChunkX, playerChunkZ, Claim.class);

        if (claim == null) {
            Message.ERROR.send(player, "Esta terra não está dominada.");
            return;
        }

        Set<Claim> claims;

        if (args.length > 0 && "todas".equalsIgnoreCase(args[0])) {
            Map<SpawnerType, Integer> spawners = FactionsProvider.Repositories.SPAWNERS.provide().countPlaced(claim.getFaction());

            if (!spawners.isEmpty()) {
                Message.ERROR.send(player, "Você não pode abandonar terras com geradores.");
                return;
            }

            claims = FactionsProvider.Cache.Local.LANDS.provide().get(claim.getFaction());
        } else {
            boolean anySpawner = Arrays.stream(playerLocation.getChunk().getTileEntities())
                    .anyMatch(state -> state instanceof CreatureSpawner);

            if (anySpawner) {
                Message.ERROR.send(player, "Você não pode abandonar terras com geradores.");
                return;
            }

            claims = Collections.singleton(claim);
        }

        for (Claim c : claims) {
            FactionsProvider.Repositories.CLAIMS.provide().delete(c);
            CoreProvider.Redis.ECHO.provide().publish(new ClaimUpdatePacket(user.getId(), c, ClaimUpdatePacket.Reason.UNCLAIM));
        }

        Message.SUCCESS.send(player, String.format("Você abandonou %s terra(s).", claims.size()));
    }
}
