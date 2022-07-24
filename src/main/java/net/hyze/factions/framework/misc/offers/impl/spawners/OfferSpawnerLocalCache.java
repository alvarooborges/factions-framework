package net.hyze.factions.framework.misc.offers.impl.spawners;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import lombok.Getter;
import net.hyze.core.shared.cache.local.LocalCache;
import net.hyze.factions.framework.spawners.SpawnerType;

import java.util.Map;
import java.util.Map.Entry;

public class OfferSpawnerLocalCache implements LocalCache {

    private final Table<Integer, SpawnerType, Data<Integer, Long>> table = HashBasedTable.create();

    public void putSpawner(int factionId, SpawnerType type) {
        Entry<Integer, Long> entry = this.table.get(factionId, type);

        if (entry == null) {
            this.table.put(factionId, type, new Data(1, System.currentTimeMillis()));
            return;
        }

        this.table.put(factionId, type, new Data(entry.getKey() + 1, System.currentTimeMillis()));
    }

    public Map<SpawnerType, Integer> getSpawners(int factionId) {
        Map<SpawnerType, Integer> map = Maps.newHashMap();

        this.table.row(factionId).keySet().forEach(type -> {
            map.put(type, this.table.get(factionId, type).getKey());
        });

        return map;
    }

    public void clear(int factionId) {
        this.table.row(factionId).keySet()
                .forEach(type -> this.table.remove(factionId, type));
    }

    /**
     * Time é o tempo em delay, ou seja, para 2 horas de delay, o valor deve ser
     * 7200000L.
     *
     * @param time
     * @return
     */
    public Table<Integer, SpawnerType, Integer> get(Long time) {
        Table<Integer, SpawnerType, Integer> out = HashBasedTable.create();

        this.table.rowKeySet().forEach(factionId -> {

            this.table.row(factionId).keySet().forEach(type -> {

                Data<Integer, Long> data = this.table.get(factionId, type);

                if (System.currentTimeMillis() - data.getValue() > time) {
                    out.put(factionId, type, data.getKey());
                }

            });

        });

        return out;
    }

    @Getter
    private class Data<K, V> implements Map.Entry<K, V> {

        private final K key;
        private V value;

        public Data(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public V setValue(V value) {
            this.value = value;
            return value;
        }
    }

}
