package net.hyze.factions.framework.spawners.evolutions.cache.local;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.cache.local.LocalCache;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.spawners.SpawnerType;
import net.hyze.factions.framework.spawners.evolutions.Evolution;
import net.hyze.factions.framework.spawners.evolutions.EvolutionRegistry;

import java.util.Collection;

public class EvolutionLocalCache implements LocalCache {

    private final Multimap<Integer, KeyLookup> keys = HashMultimap.create();

    private final LoadingCache<KeyLookup, Integer> cache = Caffeine.newBuilder()
            .build(lookup -> {
                int index = FactionsProvider.Repositories.SPAWNER_EVOLUTIONS.provide()
                        .selectLevelIndex(lookup.evolutionId, lookup.factionId, lookup.typeId);

                keys.put(lookup.factionId, lookup);

                return index;
            });

    public int getEvolutionLevel(Evolution<?> evolution, Faction faction, SpawnerType type) {
        Integer value = cache.get(new KeyLookup(evolution.getId(), faction.getId(), type.name()));

        if (value == null) {
            return 0;
        }

        return Math.max(0, Math.min(value, evolution.getLevels().size() - 1));
    }

    public void refresh(Faction faction) {
        Collection<KeyLookup> factionKeys = keys.removeAll(faction.getId());

        cache.invalidateAll(factionKeys);
        cache.getAll(factionKeys);
    }

    @EqualsAndHashCode
    @RequiredArgsConstructor
    private static class KeyLookup {
        private final String evolutionId;
        private final int factionId;
        private final String typeId;
    }
}
