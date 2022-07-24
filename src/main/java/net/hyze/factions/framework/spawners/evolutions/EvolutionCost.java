package net.hyze.factions.framework.spawners.evolutions;

import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.user.FactionUser;

import java.util.Collections;
import java.util.List;

public abstract class EvolutionCost {

    public abstract boolean has(FactionUser user, Faction faction);

    public abstract boolean transaction(FactionUser user, Faction faction);

    public abstract List<String> getDisplay(FactionUser user, Faction faction);

    public List<String> getInUseDisplay(FactionUser user, Faction faction) {
        return Collections.emptyList();
    }
}
