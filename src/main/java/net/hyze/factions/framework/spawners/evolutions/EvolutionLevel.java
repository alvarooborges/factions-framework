package net.hyze.factions.framework.spawners.evolutions;

import net.hyze.factions.framework.spawners.SpawnerType;

public interface EvolutionLevel<T> {

    T getValue(SpawnerType type);

    EvolutionCost[] getCosts(SpawnerType type);

    String[] getDisplay(SpawnerType type);

}
