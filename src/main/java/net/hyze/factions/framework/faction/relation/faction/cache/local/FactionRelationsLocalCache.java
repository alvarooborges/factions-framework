package net.hyze.factions.framework.faction.relation.faction.cache.local;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import lombok.NonNull;
import net.hyze.core.shared.cache.local.LocalCache;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.relation.faction.FactionRelation;

import java.util.function.Function;
import java.util.function.Predicate;

public class FactionRelationsLocalCache implements LocalCache {

    private final HashMultimap<Faction, Faction> CACHE_ALLY = HashMultimap.create();
    private final HashMultimap<Faction, Faction> CACHE_ENEMY = HashMultimap.create();

    private static final Function<Faction, Predicate<Faction>> IS_ANY_FACTION = faction -> {
        return other -> other.equals(faction) || other.equals(faction);
    };

    public void clear() {
        CACHE_ALLY.clear();
        CACHE_ENEMY.clear();
    }

    public void put(@NonNull FactionRelation relation) {
        if (relation.getType() == FactionRelation.Type.ALLY) {
            CACHE_ALLY.put(relation.getFactionMin(), relation.getFactionMax());
            CACHE_ALLY.put(relation.getFactionMax(), relation.getFactionMin());
        } else {
            CACHE_ENEMY.put(relation.getFactionMin(), relation.getFactionMax());
            CACHE_ENEMY.put(relation.getFactionMax(), relation.getFactionMin());
        }
    }

    public void remove(@NonNull FactionRelation relation) {
        if (relation.getType() == FactionRelation.Type.ALLY) {
            CACHE_ALLY.get(relation.getFactionMin()).removeIf(IS_ANY_FACTION.apply(relation.getFactionMax()));
            CACHE_ALLY.get(relation.getFactionMax()).removeIf(IS_ANY_FACTION.apply(relation.getFactionMin()));
        } else {
            CACHE_ENEMY.get(relation.getFactionMin()).removeIf(IS_ANY_FACTION.apply(relation.getFactionMax()));
            CACHE_ENEMY.get(relation.getFactionMax()).removeIf(IS_ANY_FACTION.apply(relation.getFactionMin()));
        }
    }

    public void remove(@NonNull Faction faction) {
        CACHE_ALLY.removeAll(faction.getId());
        CACHE_ENEMY.removeAll(faction.getId());

        CACHE_ALLY.values().removeIf(IS_ANY_FACTION.apply(faction));
        CACHE_ENEMY.values().removeIf(IS_ANY_FACTION.apply(faction));
    }

    public ImmutableSet<Faction> get(@NonNull Faction faction, FactionRelation.Type type) {
        if (type == FactionRelation.Type.ALLY) {
            return ImmutableSet.copyOf(CACHE_ALLY.get(faction));
        } else {
            return ImmutableSet.copyOf(CACHE_ENEMY.get(faction));
        }
    }
}
