package net.hyze.factions.framework.divinealtar.power.impl.electromagnetic;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.hyze.core.spigot.misc.utils.WorldCuboid;
import net.hyze.factions.framework.divinealtar.power.PowerInstance;
import org.bukkit.Location;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ElectromagneticPulseManager {

    private static final Multimap<Integer, Log> LOGS = HashMultimap.create();

    private static final Long COOLDOWN = 120000L; // 2 minutos.

    public static void log(Integer factionId, Location location) {
        LOGS.put(factionId, new Log(location));
    }

    public static void active(int factionId) {
        LOGS.get(factionId)
                .stream()
                .filter(log -> (System.currentTimeMillis() - log.getTime()) < COOLDOWN)
                .forEach(log -> {
                    log.setTime(System.currentTimeMillis());
                    log.setActive(true);
                });
    }

    public static boolean contains(Location location) {
        Log out = LOGS.values()
                .stream()
                .filter(log -> log.isActive())
                .filter(log -> (System.currentTimeMillis() - log.getTime()) < PowerInstance.ELECTROMAGNETIC_POWER.getPower().activeTime())
                .filter(log -> log.getCuboid().contains(location, true))
                .findFirst()
                .orElse(null);

        return out != null;
    }

    @Setter
    @Getter
    private static class Log {

        private Location location;
        private Long time;
        private WorldCuboid cuboid;
        private boolean active;

        public Log(Location location) {
            this.location = location;
            this.time = System.currentTimeMillis();
            this.cuboid = new WorldCuboid(
                    location.clone().add(30, 30, 30),
                    location.clone().add(-30, -30, -30)
            );

            this.active = false;
        }

    }

}
