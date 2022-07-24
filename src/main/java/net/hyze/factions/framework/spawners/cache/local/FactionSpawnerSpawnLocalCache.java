package net.hyze.factions.framework.spawners.cache.local;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.Maps;
import net.hyze.core.shared.cache.local.LocalCache;
import net.hyze.core.shared.world.location.SerializedLocation;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.spawners.SpawnerType;

import java.util.Map;
import java.util.Optional;

public class FactionSpawnerSpawnLocalCache implements LocalCache {

    public final Map<Faction, LoadingCache<SpawnerType, Optional<SerializedLocation>>> CACHE = Maps.newHashMap();

    public SerializedLocation get(Faction faction, SpawnerType type) {
        LoadingCache<SpawnerType, Optional<SerializedLocation>> cache = get(faction);

        return cache.get(type).orElse(null);
    }

    public void put(Faction faction, SpawnerType type, SerializedLocation location) {
        LoadingCache<SpawnerType, Optional<SerializedLocation>> cache = get(faction);

        cache.put(type, Optional.ofNullable(location));

        CACHE.put(faction, cache);
    }

    private LoadingCache<SpawnerType, Optional<SerializedLocation>> get(Faction faction) {
        LoadingCache<SpawnerType, Optional<SerializedLocation>> cache = CACHE.get(faction);

        if (cache == null) {
            cache = Caffeine.newBuilder()
                    .build(type -> {
                        SerializedLocation location
                                = FactionsProvider.Repositories.SPAWNERS.provide().fetchSpawnerSpawnLocation(faction, type);

                        return Optional.ofNullable(location);
                    });
        }

        return cache;
    }
}
