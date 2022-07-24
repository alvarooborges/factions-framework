package net.hyze.factions.framework.spawners;

import com.google.common.base.Enums;
import com.google.common.collect.Maps;
import dev.utils.echo.Echo;
import dev.utils.shared.setup.Setup;
import dev.utils.shared.setup.SetupException;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.spigot.misc.blockdrops.BlockDropsManager;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.spawners.evolutions.EvolutionRegistry;
import net.hyze.factions.framework.spawners.evolutions.impl.RemoveDelayEvolution;
import net.hyze.factions.framework.spawners.listeners.SpawnerListeners;
import net.hyze.factions.framework.spawners.listeners.SpawnersEchoListeners;
import net.hyze.obsidiandestroyer.api.ObsidianDestroyerAPI;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class SpawnersSetup implements Setup<JavaPlugin> {

    public static final String METADATA_TYPE_TAG = "factions:SpawnerType";
    public static final String PLACED_AT_TAG = "factions:PlacedAt";

    public static Long DEFAULT_AUTOREMOVE_DELAY = TimeUnit.HOURS.toMillis(24);
    public static final HashMap<SpawnerType, Long> AUTOREMOVE_DELAYS = Maps.newHashMap();

    public static Long DEFAULT_BREAK_COOLDOWN = TimeUnit.HOURS.toMillis(6);

    private static final HashMap<SpawnerType, Long> BREAK_COOLDOWNS = Maps.newHashMap();

    @Override
    public void enable(JavaPlugin instance) throws SetupException {

        // Listeners
        {
            PluginManager pluginManager = instance.getServer().getPluginManager();

            pluginManager.registerEvents(new SpawnerListeners(), instance);
        }

        // Echo Listeners
        {
            Echo echo = CoreProvider.Redis.ECHO.provide();

            echo.registerListener(new SpawnersEchoListeners());
        }

        // Misc

        ObsidianDestroyerAPI.addCustomBlock(new SpawnerCustomBlock());

        BlockDropsManager.registerHandler(Material.MOB_SPAWNER, (block, player, tool) -> {
            BlockState state = block.getState();

            if (!state.hasMetadata(METADATA_TYPE_TAG)) {
                return null;
            }

            MetadataValue metadataValue = state.getMetadata(METADATA_TYPE_TAG).get(0);

            SpawnerType type = Enums.getIfPresent(SpawnerType.class, metadataValue.asString()).orNull();

            if (type == null) {
                return null;
            }

            return Collections.singleton(type.getCustomItem().asItemStack());
        });
    }

    public static void registerAutoRemoveDelay(SpawnerType type, Long millis) {
        AUTOREMOVE_DELAYS.put(type, millis);
    }

    public static long getAutoRemoveDelay(SpawnerType type) {
        return AUTOREMOVE_DELAYS.getOrDefault(type, DEFAULT_AUTOREMOVE_DELAY);
    }

    public static long getBreakCooldown(Faction faction, SpawnerType type) {
        Long removeDelay = EvolutionRegistry.getCurrentLevelValue(type, faction, RemoveDelayEvolution.class);

        if (removeDelay == null) {
            removeDelay = DEFAULT_BREAK_COOLDOWN;
        }

        return removeDelay;
    }
}
