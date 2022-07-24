package net.hyze.factions.framework.misc.crystalamplifier;

import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.misc.crystalamplifier.cache.local.CrystalAmplifierLocalCache;

public class CrystalAmplifierUtils {

    public static void check(Integer factionId) {
        CrystalAmplifierLocalCache cache = FactionsProvider.Cache.Local.CRYSTAL_AMPLIFIER.provide();

        if (!cache.contains(factionId)) {
            return;
        }

        if (cache.hasEnded(factionId)) {
            remove(factionId);
        }
    }

    public static void remove(Integer factionId) {
        CrystalAmplifierLocalCache cache = FactionsProvider.Cache.Local.CRYSTAL_AMPLIFIER.provide();

        CrystalAmplifier crystal = cache.get(factionId);

        if (crystal == null) {
            return;
        }

        FactionsProvider.Repositories.CRYSTAL_AMPLIFIER.provide().update(factionId);

        crystal.destroy();

        cache.remove(factionId);
    }

}
