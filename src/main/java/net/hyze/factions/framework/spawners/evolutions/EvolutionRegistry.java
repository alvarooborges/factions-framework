package net.hyze.factions.framework.spawners.evolutions;

import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.spawners.SpawnerType;
import net.hyze.factions.framework.spawners.evolutions.impl.MultiDeathsEvolution;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EvolutionRegistry {

    private static final Map<SpawnerType, Map<String, Evolution<?>>> EVOLUTIONS = new LinkedHashMap<>();

    public static void register(SpawnerType spawnerType, Evolution<?>... evolutions) {
        Map<String, Evolution<?>> spawnerTypeEvolutions = EVOLUTIONS.getOrDefault(spawnerType, new LinkedHashMap<>());

        for (Evolution<?> evolution : evolutions) {
            spawnerTypeEvolutions.put(evolution.getClass().getName(), evolution);
        }

        EVOLUTIONS.put(spawnerType, spawnerTypeEvolutions);
    }

    public static List<Evolution<?>> getEvolutions(SpawnerType spawnerType) {
        return ImmutableList.copyOf(EVOLUTIONS.get(spawnerType).values());
    }

    public static <T extends Evolution<?>> T getEvolution(SpawnerType spawnerType, Class<T> evolutionClass) {
        Map<String, Evolution<?>> map = EVOLUTIONS.get(spawnerType);

        if (map == null) {
            return null;
        }

        return (T) map.get(evolutionClass.getName());
    }

    public static <T, E extends Evolution<T>> T getCurrentLevelValue(SpawnerType spawnerType, Faction faction, Class<E> evolutionClass) {
        E evolution = EvolutionRegistry.getEvolution(spawnerType, evolutionClass);

        if (evolution == null) {
            return null;
        }

        int levelIndex = FactionsProvider.Cache.Local.SPAWNER_EVOLUTIONS.provide()
                .getEvolutionLevel(evolution, faction, spawnerType);


        EvolutionLevel<T> level = evolution.getLevels()
                .get(Math.min(Math.max(levelIndex, 0), evolution.getLevels().size() - 1));

        return level.getValue(spawnerType);
    }
}
