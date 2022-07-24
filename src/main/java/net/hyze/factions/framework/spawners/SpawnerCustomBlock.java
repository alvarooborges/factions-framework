package net.hyze.factions.framework.spawners;

import net.hyze.core.shared.CoreConstants;
import net.hyze.core.spigot.world.location.unserializer.BukkitLocationParser;
import net.hyze.factions.framework.FactionsPlugin;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.lands.Claim;
import net.hyze.factions.framework.spawners.evolutions.EvolutionRegistry;
import net.hyze.factions.framework.spawners.evolutions.impl.DurabilityEvolution;
import net.hyze.factions.framework.spawners.evolutions.impl.MultiDeathsEvolution;
import net.hyze.factions.framework.spawners.evolutions.impl.RecoveryChanceEvolution;
import net.hyze.factions.framework.spawners.log.LogAction;
import net.hyze.factions.framework.spawners.log.LogSourceType;
import net.hyze.factions.framework.spawners.log.SpawnerLog;
import net.hyze.obsidiandestroyer.api.CustomBlock;
import org.apache.logging.log4j.core.Core;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.Collections;
import java.util.Date;

public class SpawnerCustomBlock implements CustomBlock {

    @Override
    public Material getMaterial() {
        return Material.MOB_SPAWNER;
    }

    @Override
    public int getDurability(Location location) {
        Chunk chunk = location.getChunk();
        Claim claim = FactionsProvider.Cache.Local.LANDS.provide().get(chunk.getX(), chunk.getZ(), Claim.class);

        if (claim == null) {
            return 0;
        }

        if (!(location.getBlock().getState() instanceof CreatureSpawner)) {
            return 0;
        }

        CreatureSpawner creatureSpawner = (CreatureSpawner) location.getBlock().getState();

        SpawnerType type = SpawnerType.from(creatureSpawner);

        if (type == null) {
            return 0;
        }

        Faction faction = claim.getFaction();
        Integer currentDurability = EvolutionRegistry.getCurrentLevelValue(type, faction, DurabilityEvolution.class);

        return currentDurability == null ? 0 : currentDurability;
    }

    @Override
    public boolean canDrop(Location location, EntityExplodeEvent event) {

        int failChance = 40;

        if (event.getEntity() instanceof TNTPrimed) {
            failChance = 70;
        }

        if (CoreConstants.RANDOM.nextInt(100) < failChance) {
            return false;
        }

        Chunk chunk = location.getChunk();
        Claim claim = FactionsProvider.Cache.Local.LANDS.provide().get(chunk.getX(), chunk.getZ(), Claim.class);

        if (claim == null) {
            return false;
        }

        if (!(location.getBlock().getState() instanceof CreatureSpawner)) {
            return false;
        }

        CreatureSpawner creatureSpawner = (CreatureSpawner) location.getBlock().getState();

        SpawnerType type = SpawnerType.from(creatureSpawner);

        if (type == null) {
            return false;
        }

        Faction faction = claim.getFaction();
        Integer currentRecoveryChance = EvolutionRegistry.getCurrentLevelValue(type, faction, RecoveryChanceEvolution.class);

        if (currentRecoveryChance == null) {
            currentRecoveryChance = 0;
        }

        if (CoreConstants.RANDOM.nextInt(100) < currentRecoveryChance) {
            FactionsProvider.Repositories.SPAWNERS.provide().deposit(claim.getFaction(), Collections.singletonMap(type, 1));

            Bukkit.getScheduler().runTaskAsynchronously(FactionsPlugin.getInstance(), () -> {
                try {
                    SpawnerLog log = SpawnerLog.builder()
                            .faction(claim.getFaction())
                            .type(LogSourceType.SYSTEM)
                            .typeValue("Chance de recuperação")
                            .action(LogAction.DEPOSIT_PLACED)
                            .amount(1)
                            .spawnerType(type)
                            .date(new Date())
                            .build();

                    FactionsProvider.Repositories.SPAWNERS_LOG.provide().insert(log);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            return false;
        }

        return true;
    }

    @Override
    public boolean onExplode(Location loc, EntityExplodeEvent event) {
        FactionsProvider.Repositories.SPAWNERS.provide().break0(BukkitLocationParser.serialize(loc));
        return true;
    }
}
