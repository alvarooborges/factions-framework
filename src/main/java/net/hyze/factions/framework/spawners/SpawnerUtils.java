package net.hyze.factions.framework.spawners;

import net.hyze.factions.framework.FactionsPlugin;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.spawners.evolutions.EvolutionRegistry;
import net.hyze.factions.framework.spawners.evolutions.impl.SpawnDelayEvolution;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.TileEntityMobSpawner;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftCreatureSpawner;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Calendar;
import java.util.Date;

public class SpawnerUtils {

    public static void setupSpawnerBlock(Faction faction, Block block, SpawnerType spawnerType, Date placedAt) {
        block.setType(Material.MOB_SPAWNER);

        CraftCreatureSpawner creatureSpawner = (CraftCreatureSpawner) block.getState();
        creatureSpawner.setSpawnedType(spawnerType.getEntityType());

        block.setMetadata(SpawnersSetup.METADATA_TYPE_TAG, new FixedMetadataValue(FactionsPlugin.getInstance(), spawnerType.name()));
        block.setMetadata(SpawnersSetup.PLACED_AT_TAG, new FixedMetadataValue(FactionsPlugin.getInstance(), placedAt));

        block.getState().update();

        TileEntityMobSpawner spawner = creatureSpawner.getTileEntity();

        NBTTagCompound compound = new NBTTagCompound();
        spawner.getSpawner().b(compound);

        spawnerType.setup(compound);

        compound.setShort("SpawnCount", (short) 1);

        Integer currentSpawnDelay = EvolutionRegistry.getCurrentLevelValue(spawnerType, faction, SpawnDelayEvolution.class);

        if (currentSpawnDelay == null) {
            currentSpawnDelay = 60;
        }

        compound.setShort("MinSpawnDelay", (short) (currentSpawnDelay * 20));
        compound.setShort("MaxSpawnDelay", (short) ((currentSpawnDelay + 30) * 20));

        spawner.getSpawner().a(compound);
    }

    public static Date getPlacedAt(CreatureSpawner spawner) {
        if (spawner != null && spawner.hasMetadata(SpawnersSetup.PLACED_AT_TAG)) {
            return (Date) spawner.getMetadata(SpawnersSetup.PLACED_AT_TAG).get(0).value();
        }

        return null;
    }

    public static boolean hasEndedBreakCooldown(Faction faction, Spawner spawner) {
        // 1 min de bypass para remover
        if (new Date().getTime() - spawner.getTransactedAt().getTime() <= 1000 * 60) {
            return true;
        }

        return spawner.getTransactedAt().getTime() + SpawnersSetup.getBreakCooldown(faction, spawner.getType()) <= System.currentTimeMillis();
    }

    public static long getBreakCooldownLeft(Faction faction, Spawner spawner) {
        if (hasEndedBreakCooldown(faction, spawner)) {
            return 0L;
        }

        return spawner.getTransactedAt().getTime() + SpawnersSetup.getBreakCooldown(faction, spawner.getType()) - System.currentTimeMillis();
    }

    public static long getAutoRemoveDelayLeft(Spawner spawner) {
        long delayLeft = (spawner.getTransactedAt().getTime() + SpawnersSetup.getAutoRemoveDelay(spawner.getType())) - System.currentTimeMillis();

        if (delayLeft < 0) {
            return 0;
        }

        return delayLeft;
    }

    public static boolean canSpawn(Spawner spawner) {
        if (spawner.getState() == SpawnerState.PLACED) {
            Date placedAt = spawner.getTransactedAt();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.MINUTE, -1);

            if (placedAt == null || calendar.getTime().before(placedAt)) {
                return false;
            }

            return true;
        }

        return false;
    }
}
