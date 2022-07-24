package net.hyze.factions.framework.misc.crystalamplifier.cache.local;

import com.google.common.collect.Maps;
import net.hyze.core.shared.cache.local.LocalCache;
import net.hyze.factions.framework.misc.crystalamplifier.CrystalAmplifier;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.util.Map;

public class CrystalAmplifierLocalCache implements LocalCache {

    public final Map<Integer, CrystalAmplifier> CACHE = Maps.newHashMap();
    public final Map<Long, Integer> CACHE_BY_CHUNK = Maps.newHashMap();

    public boolean contains(Integer factionId) {
        return CACHE.containsKey(factionId);
    }

    public boolean hasEnded(Integer factionId) {
        return System.currentTimeMillis() > CACHE.get(factionId).getEndTime();
    }

    public void put(CrystalAmplifier crystalAmplifier) {
        CACHE.put(crystalAmplifier.getFactionId(), crystalAmplifier);
        CACHE_BY_CHUNK.put(chunkToLong(crystalAmplifier.getLocation().getChunk()), crystalAmplifier.getFactionId());
    }

    public CrystalAmplifier get(Integer factionId) {
        return CACHE.get(factionId);
    }

    public CrystalAmplifier get(Chunk chunk) {
        long id = chunkToLong(chunk);

        if (!CACHE_BY_CHUNK.containsKey(id)) {
            return null;
        }

        return CACHE.get(CACHE_BY_CHUNK.get(id));
    }

    public void remove(Integer factionId) {
        CACHE_BY_CHUNK.remove(chunkToLong(CACHE.get(factionId).getLocation().getChunk()));
        CACHE.remove(factionId);
    }

    public Location getLocation(Integer factionId) {
        return CACHE.get(factionId).getLocation();
    }

    public Long getEndTime(Integer factionId) {
        return CACHE.get(factionId).getEndTime();
    }

    private Long chunkToLong(Chunk chunk) {
        return (long) chunk.getX() & 4294967295L | ((long) chunk.getZ() & 4294967295L) << 32;
    }

}
