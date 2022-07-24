package net.hyze.factions.framework.commands.factioncommand.subcommands;

import com.google.common.collect.Sets;
import lombok.Getter;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.spigot.inventory.ConfirmInventory;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.commands.factioncommand.FactionSubCommand;
import net.hyze.factions.framework.echo.packets.ClaimUpdatePacket;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.permission.FactionPermission;
import net.hyze.factions.framework.faction.relation.user.FactionRole;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.lands.Claim;
import net.hyze.factions.framework.misc.utils.LandUtils;
import net.hyze.factions.framework.spawners.SpawnerType;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.v1_8_R3.util.LongHash;
import org.bukkit.craftbukkit.v1_8_R3.util.LongObjectHashMap;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class AbandonSubCommand extends FactionSubCommand {

    @Getter
    private final CommandRelationType commandRelationType = CommandRelationType.FACTION_TO_ADM;

    public AbandonSubCommand() {
        super("abandonar", FactionRole.RECRUIT);
    }

    @Override
    public void onCommand(Player player, FactionUser user, FactionUserRelation relation, String[] args) {

        Faction faction = relation.getFaction();
        if (!FactionPermission.COMMAND_ABANDON.allows(faction, user)) {
            Message.ERROR.send(player, "Você não tem permissão para abandonar terras da sua facção.");
            return;
        }

        if (args.length > 0 && "todas".equalsIgnoreCase(args[0])) {
            final Set<Claim> claims = Collections.synchronizedSet(Sets.newHashSet());

            Supplier<Boolean> checker = () -> {
                if (faction.isUnderAttack()) {
                    Message.ERROR.send(player, "Você não pode abandonar terras enquanto sua facção estiver sob ataque.");
                    return false;
                }

                if (relation.getRole() != FactionRole.LEADER) {
                    Message.ERROR.send(player, "Apenas o líder pode abandonar todas as terras.");
                    return false;
                }

                claims.clear();
                claims.addAll(FactionsProvider.Cache.Local.LANDS.provide().get(faction));

                if (claims.isEmpty()) {
                    Message.ERROR.send(player, "Sua facção não dominou nenhuma terra.");
                    return false;
                }

                boolean anyContested = claims.stream().anyMatch(Claim::isContested);

                if (anyContested) {
                    Message.ERROR.send(player, "Você não pode abandonar terras contestadas.");
                    return false;
                }

                Map<SpawnerType, Integer> spawners = FactionsProvider.Repositories.SPAWNERS.provide().countPlaced(faction);

                if (!spawners.isEmpty()) {
                    Message.ERROR.send(player, "Você não pode abandonar terras com geradores.");
                    return false;
                }

                return true;
            };

            if (!checker.get()) {
                return;
            }

            ConfirmInventory confirmInventory = new ConfirmInventory(() -> {

                if (!checker.get()) {
                    return;
                }

                synchronized (claims) {
                    for (Claim claim : claims) {
                        FactionsProvider.Repositories.CLAIMS.provide().delete(claim);
                        CoreProvider.Redis.ECHO.provide().publish(new ClaimUpdatePacket(user.getId(), claim, ClaimUpdatePacket.Reason.UNCLAIM));
                    }
                }

                Message.SUCCESS.send(player, "Você abandonou todas as terras da sua facção.");
            }, () -> {

                Message.ERROR.send(player, "Você desistiu de abandonar todas as terras da sua facção.");

            }, ItemBuilder.of(Material.GRASS).name("Abandonar tudo?")
                    .lore("&7Você irá abandonar todas as terras da sua facção!")
                    .make());

            player.openInventory(confirmInventory.make("Abandonar tudo?"));
            return;
        }

        Location playerLocation = player.getLocation();

        final int playerChunkX = playerLocation.getBlockX() >> 4;
        final int playerChunkZ = playerLocation.getBlockZ() >> 4;

        Claim claim = FactionsProvider.Cache.Local.LANDS.provide().get(playerChunkX, playerChunkZ, Claim.class);

        if (claim == null) {
            Message.ERROR.send(player, "Esta terra não está dominada.");
            return;
        }

        if (!Objects.equals(claim.getFactionId(), faction.getId())) {
            Message.ERROR.send(player, "Você não pode abandonar terras de outra facção.");
            return;
        }

        if (claim.isContested()) {
            Message.ERROR.send(player, "Você não pode abandonar terras contestadas.");
            return;
        }

        if (!claim.isTemporary() && faction.isUnderAttack()) {
            Message.ERROR.send(player, "Você não pode abandonar terras enquanto sua facção estiver sob ataque.");
            return;
        }

        boolean anySpawner = Arrays.stream(playerLocation.getChunk().getTileEntities())
                .anyMatch(state -> state instanceof CreatureSpawner);

        if (anySpawner) {
            Message.ERROR.send(player, "Você não pode abandonar terras com geradores.");
            return;
        }

        if (!FactionsProvider.getSettings().isAllowInventoryHolderOutOfLands()) {

            if (Stream.of(playerLocation.getChunk().getTileEntities())
                    .anyMatch(blockState -> blockState instanceof InventoryHolder)) {
                Message.ERROR.send(player, "Para abandonar esta terra, você precisa remover todos os baús, ejetores, liberadores ou funís que estejam colocados.");
                return;
            }
        }

        Set<Claim> claims = LandUtils.getPermanentClaims(claim.getFaction());

        int minX = claims.stream().mapToInt(Claim::getChunkX).min().getAsInt();
        int maxX = claims.stream().mapToInt(Claim::getChunkX).max().getAsInt();

        int minZ = claims.stream().mapToInt(Claim::getChunkZ).min().getAsInt();
        int maxZ = claims.stream().mapToInt(Claim::getChunkZ).max().getAsInt();

        boolean[][] input = new boolean[(maxX - minX) + 1][(maxZ - minZ) + 1];

        LongObjectHashMap<Claim> longHashMap = new LongObjectHashMap<>();

        claims.forEach(c -> longHashMap.put(LongHash.toLong(c.getChunkX(), c.getChunkZ()), c));

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                boolean v = false;

                if (!(claim.getChunkX() == x && claim.getChunkZ() == z)
                        && longHashMap.containsKey(LongHash.toLong(x, z))) {
                    v = true;
                }

                input[x - minX][z - minZ] = v;
            }
        }

        if (claims.size() > 1) {
            GFG gfg = new GFG(input);

            int maxConnected = gfg.computeLargestConnectedGrid();

            if (maxConnected < claims.size() - 1) {
                Message.ERROR.send(player, "&cNão é possível abandonar esta terra, pois ela é a única ligação entre dois grupos de terras.");
                return;
            }
        }

        FactionsProvider.Repositories.CLAIMS.provide().delete(claim);

        CoreProvider.Redis.ECHO.provide().publish(new ClaimUpdatePacket(user.getId(), claim, ClaimUpdatePacket.Reason.UNCLAIM));

        Message.SUCCESS.send(player, "Você abandonou esta terra.");
    }

    static class GFG {
        final int maxX;
        final int maxZ;

        final boolean[][] visited;

        final boolean[][] input;

        GFG(boolean[][] input) {
            this.maxX = input.length;
            this.maxZ = input[0].length;

            this.visited = new boolean[this.maxX][this.maxZ];

            this.input = input;
        }

        int count(int x, int z) {
            if (x < 0 || z < 0) {
                return -1;
            }

            if (x >= this.maxX || z >= this.maxZ) {
                return -1;
            }

            if (visited[x][z]) {
                return -1;
            }

            visited[x][z] = true;

            if (!input[x][z]) {
                return 0;
            }

            int count = 1;

            int[] x_move = {0, 0, 1, -1};
            int[] y_move = {1, -1, 0, 0};

            for (int i = 0; i < 4; i++) {
                int c = count(x + x_move[i], z + y_move[i]);

                if (c > 0) {
                    count += c;
                }
            }

            return count;
        }

        int computeLargestConnectedGrid() {
            for (int x = 0; x < this.maxX; x++) {
                for (int z = 0; z < this.maxZ; z++) {
                    int c = count(x, z);

                    if (c > 0) {
                        return c;
                    }
                }
            }

            return 0;
        }
    }
}
