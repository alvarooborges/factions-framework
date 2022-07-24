package net.hyze.factions.framework.spawners.listeners;

import com.google.common.base.Enums;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import dev.utils.echo.IEchoListener;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.world.location.SerializedLocation;
import net.hyze.core.spigot.CoreSpigotConstants;
import net.hyze.core.spigot.world.location.unserializer.BukkitLocationParser;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.echo.packets.FactionPlaceCollectedSpawnersRequest;
import net.hyze.factions.framework.echo.packets.FactionRemovePlacedSpawnersRequest;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.lands.Claim;
import net.hyze.factions.framework.spawners.*;
import net.hyze.factions.framework.spawners.log.LogAction;
import net.hyze.factions.framework.spawners.log.LogSourceType;
import net.hyze.factions.framework.spawners.log.SpawnerLog;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.metadata.MetadataValue;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SpawnersEchoListeners implements IEchoListener {

    @Subscribe
    public synchronized void on(FactionPlaceCollectedSpawnersRequest request) {
        if (CoreSpigotConstants.STOPPING) {
            return;
        }

        Faction faction = request.getFaction();

        Set<Claim> claims = FactionsProvider.Cache.Local.LANDS.provide().get(faction, CoreProvider.getApp());

        if (claims.isEmpty()) {
            return;
        }

        Map<SpawnerType, Integer> total = Maps.newHashMap();

        claims.forEach(claim -> {
            Chunk chunk = Bukkit.getWorlds().get(0).getChunkAt(claim.getChunkX(), claim.getChunkZ());

            if (!chunk.isLoaded()) {
                chunk.load();
            }

            Multimap<SpawnerType, Spawner> spawners = FactionsProvider.Repositories.SPAWNERS.provide().fetchCollectedWithLocation(chunk);

            spawners.asMap().forEach((type, spawnersOfType) -> {
                List<SerializedLocation> emptyLocations = spawnersOfType.stream()
                        .map(Spawner::getLocation)
                        .filter(location -> location.parser(new BukkitLocationParser()).getBlock().getType() == Material.AIR)
                        .collect(Collectors.toList());

                if (emptyLocations.isEmpty()) {
                    return;
                }

                int deleted = FactionsProvider.Repositories.SPAWNERS.provide().withdrawCollected(faction, type, emptyLocations);

                if (deleted < 1) {
                    return;
                }

                emptyLocations.stream()
                        .limit(deleted)
                        .forEach(loc -> {
                            Location location = loc.parser(new BukkitLocationParser());

                            SpawnerUtils.setupSpawnerBlock(faction, location.getBlock(), type, new Date());

                            total.put(type, total.getOrDefault(type, 0) + 1);
                        });

                try {
                    SpawnerLog log = SpawnerLog.builder()
                            .faction(faction)
                            .type(LogSourceType.PLAYER)
                            .typeValue(String.valueOf(request.getUserId()))
                            .action(LogAction.PLACE_COLLECTED)
                            .amount(deleted)
                            .spawnerType(type)
                            .date(new Date())
                            .build();

                    FactionsProvider.Repositories.SPAWNERS_LOG.provide().insert(log);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });

        request.setResponse(new FactionPlaceCollectedSpawnersRequest.FactionPlaceCollectedSpawnersResponse(total));
    }

    @Subscribe
    public synchronized void on(FactionRemovePlacedSpawnersRequest request) {
        if (CoreSpigotConstants.STOPPING) {
            return;
        }

        Faction faction = request.getFaction();
        FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(request.getUserId());

        Set<Claim> claims = FactionsProvider.Cache.Local.LANDS.provide().get(faction, CoreProvider.getApp());

        if (claims.isEmpty()) {
            return;
        }

        Map<SpawnerType, Integer> total = Maps.newHashMap();

        for (Claim claim : claims) {
            Chunk chunk = Bukkit.getWorlds().get(0).getChunkAt(claim.getChunkX(), claim.getChunkZ());

            if (!chunk.isLoaded()) {
                chunk.load();
            }

            Multimap<SpawnerType, SerializedLocation> inChunk = ArrayListMultimap.create();

            List<BlockState> states = Lists.newArrayList(chunk.getTileEntities());

            for (BlockState state : states) {
                if (state.getType() == Material.MOB_SPAWNER) {
                    if (state.hasMetadata(SpawnersSetup.METADATA_TYPE_TAG)) {
                        List<MetadataValue> metadataValues = state.getMetadata(SpawnersSetup.METADATA_TYPE_TAG);

                        String typeRaw = metadataValues.get(0).asString();
                        SpawnerType type = Enums.getIfPresent(SpawnerType.class, typeRaw).orNull();

                        Date placedAt = (Date) state.getBlock().getMetadata(SpawnersSetup.PLACED_AT_TAG).get(0).value();

                        if (type != null && placedAt != null) {

                            Spawner spawner = new Spawner(
                                    BukkitLocationParser.serialize(state.getBlock().getLocation()),
                                    SpawnerState.PLACED,
                                    type,
                                    placedAt
                            );

                            if (!user.getOptions().isAdminModeEnabled()) {

                                if (FactionsProvider.getSettings().getSpawnerMode().isBreakCooldownEnabled()) {
                                    if (!SpawnerUtils.hasEndedBreakCooldown(faction, spawner)) {
                                        continue;
                                    }
                                }

                                if (FactionsProvider.getSettings().getSpawnerMode().isUnderAttackEnabled()) {
                                    if (claim.getFaction().isUnderAttack()) {
                                        continue;
                                    }
                                }
                            }

                            total.put(type, total.getOrDefault(type, 0) + 1);
                            state.getBlock().setType(Material.AIR);
                            state.update();

                            Location loc = state.getLocation();

                            inChunk.put(type, new SerializedLocation(
                                    CoreProvider.getApp().getId(),
                                    loc.getWorld().getName(),
                                    loc.getBlockX(),
                                    loc.getBlockY(),
                                    loc.getBlockZ()
                            ));
                        }
                    }
                }
            }

            if (inChunk.isEmpty()) {
                continue;
            }

            FactionsProvider.Repositories.SPAWNERS.provide().collect(faction, inChunk);

            try {
                inChunk.asMap().forEach((type, locations) -> {
                    SpawnerLog log = SpawnerLog.builder()
                            .faction(faction)
                            .type(LogSourceType.PLAYER)
                            .typeValue(String.valueOf(user.getId()))
                            .action(LogAction.DEPOSIT_PLACED)
                            .amount(locations.size())
                            .spawnerType(type)
                            .date(new Date())
                            .build();

                    FactionsProvider.Repositories.SPAWNERS_LOG.provide().insert(log);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        request.setResponse(new FactionRemovePlacedSpawnersRequest.FactionRemovePlacedSpawnersResponse(total));
    }
}
