package net.hyze.factions.framework.divinealtar.cache.local;

import com.google.common.collect.Maps;
import lombok.Getter;
import net.hyze.core.shared.cache.local.LocalCache;
import net.hyze.factions.framework.divinealtar.inventory.AltarInventory;

import java.util.HashMap;

public class AltarLocalCache implements LocalCache {

    @Getter
    private final HashMap<Integer, AltarInventory> cache = Maps.newHashMap();

    public AltarInventory get(Integer factionId) {
        return this.cache.getOrDefault(factionId , null);
    }
    
    public void put(Integer factionId, AltarInventory inventory){
        this.cache.put(factionId, inventory);
    }
    
    public void remove(Integer factionId){
        this.cache.remove(factionId);
    }

}
