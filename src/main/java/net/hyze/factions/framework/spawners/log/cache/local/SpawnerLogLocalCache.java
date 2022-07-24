package net.hyze.factions.framework.spawners.log.cache.local;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import net.hyze.core.shared.cache.local.LocalCache;
import net.hyze.core.shared.cache.local.utils.CaffeineScheduler;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.spawners.log.SpawnerLog;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class SpawnerLogLocalCache implements LocalCache {

    private LoadingCache<Faction, List<SpawnerLog>> cache = Caffeine.newBuilder()
            .scheduler(CaffeineScheduler.getInstance())
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .build(faction -> FactionsProvider.Repositories.SPAWNERS_LOG.provide().fetch(faction));

    public ImmutableList<SpawnerLog> get(Faction faction) {
        return ImmutableList.copyOf(cache.get(faction));
    }
}
