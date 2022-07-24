package net.hyze.factions.framework.faction.cache.local;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import lombok.NonNull;
import net.hyze.core.shared.cache.local.LocalCache;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;

import java.util.List;

public class FactionLocalCache implements LocalCache {

    private final LoadingCache<String, Faction> CACHE_BY_TAG = Caffeine.newBuilder()
            .build(tag -> FactionsProvider.Repositories.FACTIONS.provide().fetchByTag(tag));

    private final LoadingCache<Integer, Faction> CACHE_BY_ID = Caffeine.newBuilder()
            .build(id -> FactionsProvider.Repositories.FACTIONS.provide().fetchById(id));

    public Faction getIfPresent(@NonNull Integer id) {
        return CACHE_BY_ID.getIfPresent(id);
    }

    public Faction getIfPresent(@NonNull String tag) {
        return CACHE_BY_TAG.getIfPresent(tag.toUpperCase());
    }

    public Faction get(@NonNull Integer id) {
        Faction out = CACHE_BY_ID.get(id);

        if (out != null) {
            CACHE_BY_TAG.put(out.getTag(), out);
        }

        return out;
    }

    public Faction get(@NonNull String tag) {
        Faction out = CACHE_BY_TAG.get(tag);

        if (out != null) {
            CACHE_BY_ID.put(out.getId(), out);
        }

        return out;
    }

    public void put(@NonNull Faction faction) {
        CACHE_BY_ID.put(faction.getId(), faction);
        CACHE_BY_TAG.put(faction.getTag(), faction);
    }

    public void remove(@NonNull Faction faction) {
        CACHE_BY_ID.invalidate(faction.getId());
        CACHE_BY_TAG.invalidate(faction.getTag());
    }

    public List<Faction> get() {
        return ImmutableList.copyOf(CACHE_BY_ID.asMap().values());
    }
}
