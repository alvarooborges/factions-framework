package net.hyze.factions.framework.misc.power;

import com.google.common.collect.Maps;
import lombok.NonNull;

import java.util.Map;

public class PowerManager {

    /**
     * Cache que armazena id do jogador e a quando foi a última vez que o power
     * foi regenerado em ms
     */
    private static final Map<Integer, Long> CACHE = Maps.newHashMap();

    public static void update(@NonNull Integer userId, long time) {
        CACHE.put(userId, time);
    }

    public static long get(@NonNull Integer userId) {
        return CACHE.getOrDefault(userId, 0l);
    }
}
