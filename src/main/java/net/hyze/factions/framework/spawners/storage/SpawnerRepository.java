package net.hyze.factions.framework.spawners.storage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import lombok.NonNull;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.providers.MysqlDatabaseProvider;
import net.hyze.core.shared.storage.repositories.MysqlRepository;
import net.hyze.core.shared.world.location.SerializedLocation;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.spawners.Spawner;
import net.hyze.factions.framework.spawners.SpawnerType;
import net.hyze.factions.framework.spawners.storage.specs.*;
import net.hyze.factions.framework.spawners.storage.specs.spawnerspawn.InsertOrUpdateSpawnerSpawnLocationSpec;
import net.hyze.factions.framework.spawners.storage.specs.spawnerspawn.SelectSpawnerSpawnLocationSpec;
import org.bukkit.Chunk;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class SpawnerRepository extends MysqlRepository {

    public SpawnerRepository(MysqlDatabaseProvider databaseProvider) {
        super(databaseProvider);
    }

    public Map<SpawnerType, Integer> withdraw(@NonNull Faction faction) {
        Map<SpawnerType, Integer> out = Maps.newHashMap();

        for (SpawnerType type : SpawnerType.values()) {
            out.put(type, withdraw(faction, type, -1));
        }

        return out;
    }

    public int withdraw(@NonNull Faction faction, SpawnerType type, int amount) {
        return query(new DeleteCollectedSpawnersByTypeSpec(faction, type, amount));
    }

    public int withdrawCollected(@NonNull Faction faction, @NonNull SpawnerType type, @NonNull List<SerializedLocation> locations) {
        return query(new UpdateCollectedSpawnersToPlacedByLocationSpec(faction, type, locations));
    }

    public boolean deposit(@NonNull Faction faction, @NonNull Map<SpawnerType, Integer> spawners) {
        return query(new InsertDepositedSpawnersSpec(faction, spawners));
    }

    public boolean place(@NonNull Faction faction, @NonNull SpawnerType type, @NonNull SerializedLocation location) {
        query(new UpdateCollectedSpawnersLocationsToNullSpec(Lists.newArrayList(location)));
        return query(new InsertPlacedSpawnerSpec(faction, type, location));
    }

    public boolean break0(@NonNull SerializedLocation location) {
        return query(new DeletePlacedSpawnerByLocationSpec(location));
    }

    public int collect(@NonNull Faction faction, @NonNull Multimap<SpawnerType, SerializedLocation> spawners) {
        query(new UpdateCollectedSpawnersLocationsToNullSpec(Lists.newArrayList(spawners.values())));
        return query(new InsertCollectedSpawnerSpec(faction, spawners));
    }

    public Map<SpawnerType, Integer> countCollected(@NonNull Faction faction) {
        return query(new SelectCountCollectedSpawnersByFactionSpec(faction));
    }

    public Map<SpawnerType, Integer> countPlaced(@NonNull Faction faction) {
        return query(new SelectCountPlacedSpawnersByFactionSpec(faction));
    }
    
    public Map<SpawnerType, Integer> countPlaced(@NonNull Faction faction, Date before) {
        return query(new SelectCountPlacedSpawnersByFactionSpec(faction, before));
    }

    public Multimap<SpawnerType, Spawner> fetchPlaced(@NonNull Chunk chunk) {
        return query(new SelectPlacedSpawnersByChunkSpec(CoreProvider.getApp(), chunk));
    }

    public Multimap<SpawnerType, Spawner> fetchPlaced(@NonNull Faction faction) {
        return query(new SelectPlacedSpawnersByFactionSpec(faction));
    }

    public Multimap<SpawnerType, Spawner> fetchCollectedWithLocation(@NonNull Chunk chunk) {
        return query(new SelectCollectedSpawnersWithLocationByChunkSpec(CoreProvider.getApp(), chunk));
    }

    public Table<Integer, SpawnerType, Integer> calculatePlacedRank() {
        return query(new CalculatePlacedSpawnerRankSpec());
    }

    public void defineSpawnerSpawnLocation(Faction faction, SpawnerType type, SerializedLocation location) {
        query(new InsertOrUpdateSpawnerSpawnLocationSpec(faction, type, location));
    }

    public SerializedLocation fetchSpawnerSpawnLocation(Faction faction, SpawnerType type) {
        return query(new SelectSpawnerSpawnLocationSpec(faction, type));
    }
}
