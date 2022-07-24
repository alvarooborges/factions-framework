package net.hyze.factions.framework.lands.cache.local;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.NonNull;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.apps.App;
import net.hyze.core.shared.cache.local.LocalCache;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.events.LandLocalCacheGetEvent;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.lands.Claim;
import net.hyze.factions.framework.lands.Land;
import net.hyze.factions.framework.lands.Zone;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.util.LongHash;
import org.bukkit.craftbukkit.v1_8_R3.util.LongObjectHashMap;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class LandLocalCache implements LocalCache {

    private final Map<String, LongObjectHashMap<Land>> CACHE = Maps.newConcurrentMap();

    private boolean needBeRemoved(Land land) {
        if (land instanceof Claim) {
            Claim claim = (Claim) land;

            if (claim.isTemporary()) {

                return claim.getCreatedAt().getTime() < (System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(FactionsProvider.getSettings().getTemporaryClaimMinutes()));
            }
        }

        return false;
    }

    public void put(@NonNull Land land) {
        LongObjectHashMap<Land> claims = CACHE.getOrDefault(land.getAppId(), new LongObjectHashMap<>());

        claims.put(LongHash.toLong(land.getChunkX(), land.getChunkZ()), land);

        CACHE.put(land.getAppId(), claims);
    }

    public Land get(int x, int z) {
        return get(CoreProvider.getApp().getId(), x, z);
    }

    public Land get(String appId, int x, int z) {
        return get(appId, x, z, Land.class);
    }

    public <T extends Land> T get(int x, int z, Class<T> clzz) {
        return get(CoreProvider.getApp().getId(), x, z, clzz);
    }

    public <T extends Land> T get(String appId, int x, int z, Class<T> clzz) {

        LandLocalCacheGetEvent event = new LandLocalCacheGetEvent<>(appId, x, z, clzz);

        Bukkit.getServer().getPluginManager().callEvent(event);

        Land land = event.getResult();
        Land byEvent = land;

        boolean isNeutral = land instanceof Zone && ((Zone) land).getType() == Zone.Type.NEUTRAL;

        // zonas neutras podem ser protegidas, logo podem ser sobrescritas por claim
        if (land == null || isNeutral) {

            LongObjectHashMap<Land> cache = CACHE.get(appId);

            if (cache != null) {
                long hash = LongHash.toLong(x, z);
                land = cache.get(hash);

                if (needBeRemoved(land)) {
                    cache.remove(hash);
                }
            }
        }

        // se nenhum claim foi encontro e a zona era neutra, o retorno volta ser a zona neutra
        if (isNeutral && land == null) {
            land = byEvent;
        }

        if (clzz.isInstance(land)) {
            return (T) land;
        }

        return null;
    }

    public void remove(Land land) {
        remove(land.getAppId(), land.getChunkX(), land.getChunkZ());
    }

    public void remove(int x, int z) {
        remove(CoreProvider.getApp().getId(), x, z);
    }

    public void remove(String appId, int x, int z) {
        CACHE.getOrDefault(appId, new LongObjectHashMap<>())
                .remove(LongHash.toLong(x, z));
    }

    public void remove(@NonNull Faction faction) {
        Collection<Claim> lands = Lists.newArrayList();

        CACHE.values().forEach(hash -> {
            hash.values().forEach(land -> {

                if (!(land instanceof Claim)) {
                    return;
                }

                Claim claim = (Claim) land;

                if (!claim.getFactionId().equals(faction.getId())) {
                    return;
                }

                lands.add(claim);
            });
        });

        lands.forEach(land -> {
            LongObjectHashMap<Land> hashes = CACHE.get(land.getAppId());

            if (hashes != null) {
                hashes.remove(LongHash.toLong(land.getChunkX(), land.getChunkZ()));
            }
        });
    }

    public Set<Claim> get(@NonNull Faction faction) {
        return get(faction, null);
    }

    public Set<Claim> get(@NonNull Faction faction, App app) {


        if (app == null) {
            Set<Claim> out = Sets.newHashSet();

            for (LongObjectHashMap<Land> cache : CACHE.values()) {
                filter(cache, faction, out);
            }

            return out;
        } else {
            LongObjectHashMap<Land> cache = CACHE.get(app.getId());

            if (cache != null) {
                Set<Claim> out = Sets.newHashSet();

                filter(cache, faction, out);

                return out;
            }

            return Collections.EMPTY_SET;
        }
    }

    private void filter(LongObjectHashMap<Land> cache, Faction faction, Set<Claim> out) {

        Iterator<Land> lands = cache.values().iterator();

        while(lands.hasNext()) {
            Land land = lands.next();

            if (land instanceof Claim) {

                if (((Claim) land).getFactionId().equals(faction.getId())) {

                    if (needBeRemoved(land)) {
                        lands.remove();
                    } else {
                        out.add((Claim) land);
                    }
                }
            }
        }
    }
}
