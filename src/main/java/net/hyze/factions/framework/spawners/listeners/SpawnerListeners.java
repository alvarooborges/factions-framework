package net.hyze.factions.framework.spawners.listeners;

import com.google.common.base.Enums;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.misc.cooldowns.UserCooldowns;
import net.hyze.core.shared.misc.utils.DateUtils;
import net.hyze.core.shared.world.location.SerializedLocation;
import net.hyze.core.spigot.CoreSpigotConstants;
import net.hyze.core.spigot.misc.blockdrops.BlockDropsManager;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.stackmobs.StackMobsAPI;
import net.hyze.core.spigot.misc.stackmobs.events.StackMobDeathEvent;
import net.hyze.core.spigot.misc.utils.InventoryUtils;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.core.spigot.misc.utils.LocationUtils;
import net.hyze.core.spigot.misc.utils.WorldCuboid;
import net.hyze.core.spigot.world.location.unserializer.BukkitLocationParser;
import net.hyze.factions.framework.FactionsPlugin;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.permission.FactionPermission;
import net.hyze.factions.framework.faction.relation.user.FactionRole;
import net.hyze.factions.framework.lands.Claim;
import net.hyze.factions.framework.misc.utils.FactionUtils;
import net.hyze.factions.framework.misc.utils.LandUtils;
import net.hyze.factions.framework.spawners.*;
import net.hyze.factions.framework.spawners.events.EntitySpawnerTypeDeathEvent;
import net.hyze.factions.framework.spawners.evolutions.EvolutionRegistry;
import net.hyze.factions.framework.spawners.evolutions.impl.*;
import net.hyze.factions.framework.spawners.log.LogAction;
import net.hyze.factions.framework.spawners.log.LogSourceType;
import net.hyze.factions.framework.spawners.log.SpawnerLog;
import net.hyze.factions.framework.user.FactionUser;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.TileEntityMobSpawner;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftCreatureSpawner;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PreSpawnerSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SpawnerListeners implements Listener {

    private final static Map<String, Long> COOLDOWNS = Maps.newHashMap();

    private final static int ADDITIONAL_DELAYF = 1;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onLow(StackMobDeathEvent event) {
        EntityDeathEvent deathEvent = event.getDeathEvent();

        LivingEntity entity = deathEvent.getEntity();

        SpawnerType spawnerType = SpawnerType.from(entity);

        if (spawnerType == null) {
            return;
        }

        Location location = entity.getLocation();
        Chunk chunk = location.getChunk();

        int stackSize = event.getStackedEntity().getSize();

        Claim claim = FactionsProvider.Cache.Local.LANDS.provide().get(chunk.getX(), chunk.getZ(), Claim.class);

        deathEvent.getDrops().clear();
        deathEvent.setDroppedExp(0);

        int deaths = 1;

        Player killer = entity.getKiller();

        if (claim != null) {
            Faction faction = claim.getFaction();

            Integer currentMultiDeaths = EvolutionRegistry.getCurrentLevelValue(spawnerType, faction, MultiDeathsEvolution.class);

            if (currentMultiDeaths != null) {
                deaths = Math.max(1, Math.min(currentMultiDeaths, stackSize));
            }

            Double currentDropsMultiplier = EvolutionRegistry.getCurrentLevelValue(spawnerType, faction, DropsMultiplierEvolution.class);

            if (currentDropsMultiplier == null) {
                currentDropsMultiplier = 1.0;
            }

            Double currentExpMultiplier = EvolutionRegistry.getCurrentLevelValue(spawnerType, faction, ExpMultiplierEvolution.class);

            if (currentExpMultiplier == null) {
                currentExpMultiplier = 1.0;
            }

            int expToDrop = 0;

            if (killer != null) {
                for (int i = 0; i < deaths; i++) {

                    EntitySpawnerTypeDeathEvent entitySpawnerTypeDeathEvent = new EntitySpawnerTypeDeathEvent(
                            spawnerType,
                            killer,
                            spawnerType.getDrops(killer),
                            spawnerType.getExp(entity, killer)
                    );

                    Bukkit.getPluginManager().callEvent(entitySpawnerTypeDeathEvent);

                    for (ItemStack drop : entitySpawnerTypeDeathEvent.getDrops()) {

                        if (ItemBuilder.of(drop, true).hasNbt("collection:owner")) {
                            continue;
                        }

                        double newAmount = drop.getAmount() * currentDropsMultiplier;
                        drop.setAmount((int) Math.round(newAmount));
                        deathEvent.getDrops().add(drop);
                    }

                    expToDrop += entitySpawnerTypeDeathEvent.getExp() * currentExpMultiplier;
                }
            }

            deathEvent.setDroppedExp(expToDrop);

            if (killer != null && killer.isOnline()) {
                deathEvent.setDroppedExp(0);
                killer.giveExp(expToDrop);
            }
        }

        event.setDeathAmount(deaths);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onHigh(PreSpawnerSpawnEvent event) {
        event.setCancelled(true);

        if (event.getSpawner() == null) {
            return;
        }

        SpawnerType spawnerType = SpawnerType.from(event.getSpawner());

        if (spawnerType == null) {
            return;
        }

        Claim claim = LandUtils.getClaim(event.getSpawner().getLocation());

        if (claim == null || claim.isTemporary() || claim.isContested()) {
            return;
        }

        Date placedAt = SpawnerUtils.getPlacedAt(event.getSpawner());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, -1);

        if (placedAt == null || calendar.getTime().before(placedAt)) {
            return;
        }

        Faction faction = claim.getFaction();

        SerializedLocation serialized = FactionsProvider.Cache.Local.SPAWNERS_SPAWN.provide().get(faction, spawnerType);

        if (serialized == null) {
            notifyUnsetSpawnerSpawn(faction, spawnerType);
            return;
        }

        Location spawnerLocation = event.getSpawner().getLocation();

        Location spawnerSpawnLocation = LocationUtils.center(new BukkitLocationParser().apply(serialized));
        spawnerSpawnLocation.setY(serialized.getY());

        WorldCuboid cuboid = new WorldCuboid(
                spawnerLocation.clone().add(8, 20, 8),
                spawnerLocation.clone().add(-8, -7, -8)
        );

        if (!cuboid.contains(spawnerSpawnLocation, true)) {
            return;
        }

        Claim spawnerSpawnClaim = LandUtils.getClaim(spawnerSpawnLocation);

        if (spawnerSpawnClaim == null
                || spawnerSpawnClaim.isTemporary()
                || spawnerSpawnClaim.isContested()
                || !spawnerSpawnClaim.getFactionId().equals(claim.getFactionId())) {

            notifyUnsetSpawnerSpawn(faction, spawnerType);
            return;
        }

        Integer currentMaxStackSize = EvolutionRegistry.getCurrentLevelValue(spawnerType, faction, StackSizeEvolution.class);

        if (currentMaxStackSize == null) {
            currentMaxStackSize = 1;
        }

        Entity newEntity = spawnerType.createEntity(spawnerSpawnLocation);

        if (newEntity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) newEntity;

            double newHealth = spawnerType.modifyHealth(living.getHealth());

            living.setMaxHealth(newHealth);
            living.setHealth(newHealth);
        }

        boolean spawned = StackMobsAPI.handle(
                newEntity,
                CreatureSpawnEvent.SpawnReason.SPAWNER,
                spawnerSpawnLocation,
                currentMaxStackSize
        );

        // Definindo delay de spawn
        {
            List<MetadataValue> metadataValueList = event.getSpawner().getBlock().getMetadata("current_spawn_delay");

            Integer currentSpawnDelay = EvolutionRegistry.getCurrentLevelValue(spawnerType, faction, SpawnDelayEvolution.class);

            if (currentSpawnDelay == null) {
                currentSpawnDelay = 60;
            }

            if (metadataValueList.isEmpty() || metadataValueList.get(0).asInt() != currentSpawnDelay) {
                event.getSpawner().getBlock().setMetadata("current_spawn_delay", new FixedMetadataValue(FactionsPlugin.getInstance(), currentSpawnDelay));

                CraftCreatureSpawner creatureSpawner = (CraftCreatureSpawner) event.getSpawner();
                TileEntityMobSpawner spawner = creatureSpawner.getTileEntity();

                NBTTagCompound compound = new NBTTagCompound();

                spawner.getSpawner().b(compound);

                compound.setShort("MinSpawnDelay", (short) (currentSpawnDelay * 20));
                compound.setShort("MaxSpawnDelay", (short) ((currentSpawnDelay + 30) * 20));

                spawner.getSpawner().a(compound);
            }
        }

        if (!spawned) {

            if (!CoreProvider.getApp().getId().equals(serialized.getAppId())) {
                notifyUnsetSpawnerSpawn(faction, spawnerType);
                return;
            }

            if (cuboid.contains(event.getSpawner().getLocation(), true)) {
                CraftWorld world = (CraftWorld) spawnerSpawnLocation.getWorld();

                net.minecraft.server.v1_8_R3.Entity newEntityHandle = ((CraftEntity) newEntity).getHandle();

                newEntityHandle.setPosition(spawnerSpawnLocation.getX(), spawnerSpawnLocation.getY(), spawnerSpawnLocation.getZ());
                newEntityHandle.fromMobSpawner = true;

                newEntity = world.addEntity(newEntityHandle, CreatureSpawnEvent.SpawnReason.CUSTOM);

                StackMobsAPI.setup(newEntity);
            }
        }
    }

    private void notifyUnsetSpawnerSpawn(Faction faction, SpawnerType spawnerType) {
        String notifyCooldownKey = String.format("%s-notify", faction.getId());

        if (COOLDOWNS.getOrDefault(notifyCooldownKey, 0L) <= System.currentTimeMillis()) {
            COOLDOWNS.put(notifyCooldownKey, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1));

            ComponentBuilder builder = new ComponentBuilder("")
                    .append(String.format(
                            " * Você precisa definir a localização dos seus geradores de %s.\n",
                            spawnerType.getRawDisplayName()
                    ))
                    .color(ChatColor.YELLOW)
                    .append(" * Digite /f setspawn para definir uma localização.");

            FactionUtils.broadcast(faction, builder.create(), FactionRole.CAPTAIN);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void on(PlayerInteractEvent event) {
        if (!event.hasBlock() || !event.hasItem() || event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack inHand = event.getPlayer().getItemInHand();

        if (inHand == null) {
            return;
        }

        if (inHand.getType() == Material.MONSTER_EGG || inHand.getType() == Material.MONSTER_EGGS) {
            if (event.getClickedBlock().getType() == Material.MOB_SPAWNER) {
                event.setCancelled(true);
                return;
            }
        }

        if (inHand.getType() == Material.MOB_SPAWNER) {
            event.setCancelled(true);
            return;
        }

        if (inHand.getType() != Material.SKULL_ITEM) {
            return;
        }

        ItemBuilder itemBuilder = new ItemBuilder(inHand);

        String typeRaw = itemBuilder.nbtString(SpawnersSetup.METADATA_TYPE_TAG);

        if (typeRaw == null) {
            return;
        }

        SpawnerType type = Enums.getIfPresent(SpawnerType.class, typeRaw).orNull();

        if (type == null) {
            return;
        }

        event.setCancelled(true);

        Block block = event.getClickedBlock().getRelative(event.getBlockFace());

        if (block.getType() != Material.AIR) {
            return;
        }

        if (block.getY() > 252) {
            Message.ERROR.send(event.getPlayer(), "Não é permitido colocar geradores nesta camada.");
            return;
        }

        FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(event.getPlayer().getName());

        if (!LandUtils.canBuildAt(user, block.getLocation())) {
            return;
        }

        Claim claim = FactionsProvider.Cache.Local.LANDS.provide().get(
                block.getChunk().getX(),
                block.getChunk().getZ(),
                Claim.class
        );

        if (claim == null) {
            Message.ERROR.send(event.getPlayer(), "Você só pode colocar geradores em terras dominadas.");
            return;
        }

        if (claim.isTemporary()) {
            Message.ERROR.send(event.getPlayer(), "Não é possível colocar geradores em terras temporárias.");
            return;
        }

        Faction faction = FactionsProvider.Cache.Local.FACTIONS.provide().get(claim.getFactionId());

        boolean canPlace = FactionsProvider.Repositories.SPAWNERS.provide().place(
                faction,
                type,
                BukkitLocationParser.serialize(block.getLocation())
        );

        if (!canPlace) {
            Message.ERROR.send(event.getPlayer(), "Algo de errado aconteceu ao tentar colocar o gerador.");
            return;
        }

        SpawnerUtils.setupSpawnerBlock(faction, block, type, new Date());

        event.getPlayer().getWorld().playSound(block.getLocation(), Sound.STEP_STONE, 1f, 1.5f);

        InventoryUtils.subtractOneOnHand(event.getPlayer());

        if (FactionsProvider.getSettings().getSpawnerMode().isBreakCooldownEnabled()) {
            Message.INFO.send(event.getPlayer(), "* Você pode retirar este gerador durante o próximo minuto.");
            Message.INFO.send(event.getPlayer(), "* Durante esse tempo, mobs não nascerão deste gerador.");
            Message.INFO.send(event.getPlayer(), String.format(
                    "* Após isso, ele só poderá ser retirado depois de %s.",
                    DateUtils.millisToHumanStr(SpawnersSetup.getBreakCooldown(faction, type))
            ));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void on(BlockBreakEvent event) {
        if (CoreSpigotConstants.STOPPING) {
            event.setCancelled(true);
            return;
        }

        Block block = event.getBlock();

        if (block.getType() != Material.MOB_SPAWNER) {
            return;
        }

        event.setExpToDrop(0);
        block.getDrops().clear();

        Player player = event.getPlayer();

        Collection<ItemStack> collectionsOfDrops = BlockDropsManager.getDrops(block, player, player.getItemInHand());
        ItemStack[] drops = collectionsOfDrops.toArray(new ItemStack[0]);

        if (drops.length < 1) {
            return;
        }

        event.setCancelled(true);

        FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(player);

        Claim claim = LandUtils.getClaim(block.getLocation());

        boolean userAdminModeEnabled = user.getOptions().isAdminModeEnabled();

        if (!userAdminModeEnabled && claim != null && !FactionPermission.BREAK_PLACED_SPAWNERS.allows(claim.getFaction(), user)) {
            Message.ERROR.send(player, "&cVocê não pode quebrar geradores.");
            return;
        }

        if (!event.getBlock().hasMetadata(SpawnersSetup.METADATA_TYPE_TAG) || !event.getBlock().hasMetadata(SpawnersSetup.PLACED_AT_TAG)) {
            Message.ERROR.send(player, "Não foi possível identificar o tipo do gerador.");
            return;
        }

        SpawnerType type = Enums.getIfPresent(SpawnerType.class, event.getBlock().getMetadata(SpawnersSetup.METADATA_TYPE_TAG).get(0).asString())
                .orNull();

        if (type == null) {
            Message.ERROR.send(player, "O tipo do gerador é inválido.");
            return;
        }

        Date placedAt = (Date) event.getBlock().getMetadata(SpawnersSetup.PLACED_AT_TAG).get(0).value();

        if (placedAt == null) {
            Message.ERROR.send(player, "A data é inválida.");
            return;
        }

        if (!ItemBuilder.of(player.getItemInHand()).enchantments().containsKey(Enchantment.SILK_TOUCH)) {
            Message.ERROR.send(player, String.format(
                    "Você precisa do encantamento &f%s &cpara remover Geradores.",
                    CoreSpigotConstants.TRANSLATE_ITEM.get(Enchantment.SILK_TOUCH)
            ));

            if (!player.getGameMode().equals(GameMode.CREATIVE)) {
                return;
            } else {
                Message.INFO.send(player, "Bypassando, pois você está no modo Criativo.");
            }
        }

        if (FactionsProvider.getSettings().getSpawnerMode().isBreakCooldownEnabled()) {
            Spawner spawner = new Spawner(
                    BukkitLocationParser.serialize(event.getBlock().getLocation()),
                    SpawnerState.PLACED,
                    type,
                    placedAt
            );

            if (claim != null && !SpawnerUtils.hasEndedBreakCooldown(claim.getFaction(), spawner)) {
                if (!user.getOptions().isAdminModeEnabled()) {
                    Message.ERROR.send(player, String.format("Este gerador apenas poderá ser retirado em %s.",
                            UserCooldowns.getFormattedTimeLeft(SpawnerUtils.getBreakCooldownLeft(claim.getFaction(), spawner))
                    ));
                    return;
                }

                Message.INFO.send(player, String.format("Este gerador só poderia ser retirado em %s.",
                        UserCooldowns.getFormattedTimeLeft(SpawnerUtils.getBreakCooldownLeft(claim.getFaction(), spawner))
                ));
            }
        }

        if (FactionsProvider.getSettings().getSpawnerMode().isUnderAttackEnabled()) {
            if (!userAdminModeEnabled) {
                if (claim != null && claim.getFaction().isUnderAttack()) {
                    Message.ERROR.send(player, "Seus Geradores não podem ser retirados pois sua facção está sob-ataque!");
                    return;
                }
            }
        }

        if (!InventoryUtils.fits(player.getInventory(), drops)) {
            Message.ERROR.send(player, "Seu inventário está cheio.");
            return;
        }

        boolean canBreak = FactionsProvider.Repositories.SPAWNERS.provide().break0(
                BukkitLocationParser.serialize(event.getBlock().getLocation())
        );

        if (!canBreak && !userAdminModeEnabled) {
            Message.ERROR.send(player, "Algo de errado aconteceu ao tentar remover o gerador.");
            return;
        }

        block.setType(Material.AIR);
        player.getInventory().addItem(drops);
        player.updateInventory();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void on(ChunkLoadEvent event) {
        if (CoreSpigotConstants.STOPPING) {
            return;
        }

        Chunk chunk = event.getChunk();

        List<CreatureSpawner> states = Lists.newArrayList(chunk.getTileEntities()).stream()
                .filter(state -> state.getType() == Material.MOB_SPAWNER)
                .filter(state -> !state.hasMetadata(SpawnersSetup.METADATA_TYPE_TAG))
                .map(state -> (CreatureSpawner) state)
                .collect(Collectors.toList());

        if (states.isEmpty()) {
            return;
        }

        Claim claim = LandUtils.getClaim(new Location(chunk.getWorld(), chunk.getX() << 4, 0, chunk.getZ() << 4));

        if (claim == null) {
            return;
        }

        Multimap<SpawnerType, Spawner> spawners = FactionsProvider.Repositories.SPAWNERS.provide().fetchPlaced(chunk);

        spawners.asMap().forEach((type, spawnersOfType) -> {
            spawnersOfType.forEach(spawner -> {
                Block block = spawner.getLocation().parser(new BukkitLocationParser()).getBlock();

                if (block.getType() == Material.MOB_SPAWNER) {
                    SpawnerUtils.setupSpawnerBlock(claim.getFaction(), block, spawner.getType(), spawner.getTransactedAt());
                }
            });
        });

        states.forEach(state -> {
            if (!state.hasMetadata(SpawnersSetup.METADATA_TYPE_TAG)) {
                SpawnerType type = SpawnerType.from(state);

                if (type != null) {

                    boolean canPlace = FactionsProvider.Repositories.SPAWNERS.provide().place(
                            claim.getFaction(),
                            type,
                            BukkitLocationParser.serialize(state.getBlock().getLocation())
                    );

                    if (canPlace) {
                        SpawnerUtils.setupSpawnerBlock(claim.getFaction(), state.getBlock(), type, new Date());
                    }
                }
            }

        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMonitor(PreSpawnerSpawnEvent event) {

        if (FactionsProvider.getSettings().isAutoRemoveSpawnersEnabled()) {
            if (event.getSpawner() == null) {
                return;
            }

            Block block = event.getSpawner().getBlock();

            if (block == null) {
                return;
            }

            Claim claim = LandUtils.getClaim(block.getLocation());

            if (claim == null) {
                return;
            }

            if (!block.hasMetadata(SpawnersSetup.METADATA_TYPE_TAG) || !block.hasMetadata(SpawnersSetup.PLACED_AT_TAG)) {
                return;
            }

            SpawnerType type = Enums.getIfPresent(SpawnerType.class, block.getMetadata(SpawnersSetup.METADATA_TYPE_TAG).get(0).asString())
                    .orNull();

            if (type == null) {
                return;
            }

            Date placedAt = (Date) block.getMetadata(SpawnersSetup.PLACED_AT_TAG).get(0).value();

            if (placedAt == null) {
                return;
            }

            Spawner spawner = new Spawner(
                    BukkitLocationParser.serialize(block.getLocation()),
                    SpawnerState.PLACED,
                    type,
                    placedAt
            );

            long delayLeft = SpawnerUtils.getAutoRemoveDelayLeft(spawner);

            if (delayLeft <= 0) {
                block.setType(Material.AIR);
                block.getState().update();

                Multimap<SpawnerType, SerializedLocation> inChunk = ArrayListMultimap.create();

                inChunk.put(type, BukkitLocationParser.serialize(block.getLocation()));

                FactionsProvider.Repositories.SPAWNERS.provide().collect(claim.getFaction(), inChunk);

                try {
                    SpawnerLog log = SpawnerLog.builder()
                            .faction(claim.getFaction())
                            .type(LogSourceType.AUTOMATED)
                            .typeValue(String.valueOf(SpawnersSetup.getAutoRemoveDelay(type)))
                            .action(LogAction.DEPOSIT_PLACED)
                            .amount(1)
                            .spawnerType(type)
                            .date(new Date())
                            .build();

                    FactionsProvider.Repositories.SPAWNERS_LOG.provide().insert(log);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
