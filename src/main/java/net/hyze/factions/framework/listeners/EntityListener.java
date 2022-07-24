package net.hyze.factions.framework.listeners;

import com.google.common.collect.Sets;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.misc.cooldowns.Cooldowns;
import net.hyze.core.shared.misc.utils.Pair;
import net.hyze.core.spigot.CoreSpigotConstants;
import net.hyze.core.spigot.CoreSpigotPlugin;
import net.hyze.core.spigot.misc.combat.CombatManager;
import net.hyze.core.spigot.misc.scoreboard.bukkit.GroupScoreboard;
import net.hyze.core.spigot.misc.scoreboard.bukkit.IHealthBoard;
import net.hyze.factions.framework.FactionsPlugin;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.echo.packets.FactionUnderAttackPacket;
import net.hyze.factions.framework.entities.SuperCreeperEntity;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.lands.Claim;
import net.hyze.factions.framework.lands.LandState;
import net.hyze.factions.framework.lands.Zone;
import net.hyze.factions.framework.misc.customitem.data.AbstractCreeperEggItem;
import net.hyze.factions.framework.misc.utils.FactionUtils;
import net.hyze.factions.framework.misc.utils.LandUtils;
import net.hyze.factions.framework.user.FactionUser;
import net.hyze.obsidiandestroyer.tnts.FakeTNT;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.util.LongHash;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEntityEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Date;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class EntityListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onMonitor(EntitySpawnEvent event) {
        if (CoreSpigotPlugin.getInstance().isNPC(event.getEntity())) {
            for (Player target : Bukkit.getOnlinePlayers()) {
                if (!target.isOnline()) {
                    continue;
                }

                FactionUser targetUser = FactionsProvider.Cache.Local.USERS.provide().get(target.getName());

                if (!(targetUser.getBoard() instanceof GroupScoreboard)) {
                    continue;
                }

                ((GroupScoreboard) targetUser.getBoard()).registerNPC(event.getEntity().getName());
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void on(CreatureSpawnEvent event) {

        Set<CreatureSpawnEvent.SpawnReason> blacklist = EnumSet.of(
                CreatureSpawnEvent.SpawnReason.NATURAL,
                CreatureSpawnEvent.SpawnReason.JOCKEY,
                CreatureSpawnEvent.SpawnReason.INFECTION,
                CreatureSpawnEvent.SpawnReason.BUILD_IRONGOLEM,
                CreatureSpawnEvent.SpawnReason.BUILD_SNOWMAN,
                CreatureSpawnEvent.SpawnReason.BUILD_WITHER,
                CreatureSpawnEvent.SpawnReason.CHUNK_GEN,
                CreatureSpawnEvent.SpawnReason.MOUNT,
                CreatureSpawnEvent.SpawnReason.SLIME_SPLIT
        );

        if (blacklist.contains(event.getSpawnReason())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void on(BlockDispenseEvent event) {

    }

    @EventHandler
    public void on(ExplosionPrimeEvent event) {
        if (event.getEntityType() == EntityType.WITHER || event.getEntityType() == EntityType.WITHER_SKULL) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onMonitor(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get((Player) event.getEntity());

            if (user.getBoard() instanceof IHealthBoard) {
                Bukkit.getScheduler().runTask(FactionsPlugin.getInstance(), () -> ((IHealthBoard) user.getBoard()).updateHealth());
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onMonitor(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player) {
            FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get((Player) event.getEntity());

            if (user.getBoard() instanceof IHealthBoard) {
                Bukkit.getScheduler().runTask(FactionsPlugin.getInstance(), () -> ((IHealthBoard) user.getBoard()).updateHealth());
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onLowest(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent) {
            return;
        }

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Zone zone = LandUtils.getZone(event.getEntity().getLocation());

        if (zone != null && !zone.getType().equals(Zone.Type.NEUTRAL)) {
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                event.setCancelled(true);
                return;
            }

            if (!zone.getType().isPvpEnabled()) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void on(CreeperPowerEvent event) {

        if (event.getEntity() != null) {
            Creeper creeper = event.getEntity();

            creeper.remove();

            AbstractCreeperEggItem.spawnCreeper(
                    SuperCreeperEntity.class,
                    creeper.getLocation(),
                    null
            );
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player victim = (Player) event.getEntity();

        Zone victimZone = LandUtils.getZone(victim.getLocation());

        if (victimZone != null) {
            if (!victimZone.getType().isPvpEnabled()) {
                event.setCancelled(true);
                return;
            }
        }

        Pair<FactionUser, FactionUser> pair = extractPair(event);

        if (pair == null) {
            return;
        }

        FactionUser victimUser = pair.getLeft();
        FactionUser damagerUser = pair.getRight();

        Player damager = damagerUser.getPlayer();

        Zone damagerZone = LandUtils.getZone(damager.getLocation());

        if (damagerZone != null) {
            if (!damagerZone.getType().isPvpEnabled()) {
                event.setCancelled(true);
                return;
            }
        }

        if (!FactionUtils.isHostile(damagerUser, victimUser)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDamageByEntityMonitor(EntityDamageByEntityEvent event) {
        Pair<FactionUser, FactionUser> pair = extractPair(event);

        if (pair == null) {
            return;
        }

        FactionUser victimUser = pair.getLeft();
        FactionUser damagerUser = pair.getRight();

        boolean bypass = damagerUser.getOptions().isAdminModeEnabled() || victimUser.getOptions().isAdminModeEnabled();

        if (!bypass) {
            CombatManager.tag(victimUser.getHandle(), damagerUser.getHandle());
            CombatManager.tag(damagerUser.getHandle(), victimUser.getHandle());
        }
    }

    private Pair<FactionUser, FactionUser> extractPair(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player)) {
            return null;
        }

        Player victim = (Player) event.getEntity();

        Player damager = null;

        if (event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();

            if (projectile.getShooter() instanceof Player) {
                damager = (Player) projectile.getShooter();
            }
        }

        if (event.getDamager().hasMetadata(CoreSpigotConstants.NBTKeys.ENTITY_OWNER_DAMAGE)) {
            Entity ownerDamage = (Entity) event.getDamager().getMetadata(CoreSpigotConstants.NBTKeys.ENTITY_OWNER_DAMAGE).get(0).value();

            if (ownerDamage instanceof Player) {
                damager = (Player) ownerDamage;
            }
        }

        if (damager == null) {
            return null;
        }

        FactionUser victimUser = FactionsProvider.Cache.Local.USERS.provide().get(victim);
        FactionUser damagerUser = FactionsProvider.Cache.Local.USERS.provide().get(damager);

        return new Pair<>(victimUser, damagerUser);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void on(EntityExplodeEvent event) {
        if (!FactionsProvider.getSettings().isExplosionsEnabled()) {
            event.setCancelled(true);
            event.blockList().clear();
            return;
        }

        /*
        tnts de impulsão não deixam uma facção sob-ataque
         */
        if (event.getEntity() == null || event.getEntity().hasMetadata(FakeTNT.ITEM_NBT)) {
            return;
        }

        Location eventLocation = event.getLocation();

        Zone zone = LandUtils.getZone(eventLocation);

        if (zone != null) {
            if (!zone.getType().isExplosionsEnabled()) {
                event.setCancelled(true);
                return;
            }
        }

        event.blockList().removeIf(block -> {
            Location location = block.getLocation();

            Zone z = LandUtils.getZone(location);

            if (z == null) {
                return false;
            }

            return !z.getType().isExplosionsEnabled();
        });

        Set<Block> blocks = Sets.newHashSet();
        blocks.add(eventLocation.getBlock());
        blocks.addAll(event.blockList());

        Claim claim;

        for (Block block : blocks) {
            claim = LandUtils.getClaim(block.getLocation());

            if (claim != null) {
                Faction faction = claim.getFaction();

                String key = "faction-under-attack-start-" + faction.getId();

                Date underAttackAt = new Date();

                long hash = LongHash.toLong(claim.getChunkX(), claim.getChunkZ());
                LandState.UNDER_ATTACK_CHUNK.put(hash, underAttackAt);

                if (Cooldowns.hasEnded(key)) {

                    Cooldowns.start(key, 30, TimeUnit.SECONDS);

                    faction.setUnderAttackAt(underAttackAt);

                    FactionsProvider.Repositories.FACTIONS.provide().update(faction);

                    CoreProvider.Redis.ECHO.provide().publish(new FactionUnderAttackPacket(
                            faction.getId(),
                            underAttackAt,
                            claim.getChunkX(),
                            claim.getChunkZ()
                    ));
                }

                break;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {

        if (event.getEntity() instanceof FallingBlock) {

            if (event.getTo() == Material.AIR) {

                if (event.getBlock().getType() == Material.SAND
                        || event.getBlock().getType() == Material.GRAVEL
                        || event.getBlock().getType() == Material.ANVIL) {

                    event.setCancelled(true);

                    event.getBlock().getState().update(false, false);
                }
            }

            return;
        }

        Block block = event.getBlock();
        Player player = null;

        if (event.getEntity() instanceof Player) {
            player = (Player) event.getEntity();
        }

        if (event.getEntity() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getEntity();
            if (projectile.getShooter() != null && projectile.getShooter() instanceof Player) {
                player = (Player) projectile.getShooter();
            }
        }

        Zone zone = LandUtils.getZone(block.getLocation());

        if (player == null && zone != null) {
            event.setCancelled(true);
            return;
        }

        if (player != null) {
            FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(player.getName());

            event.setCancelled(!LandUtils.canBuildAt(user, block.getLocation()));
        }
    }

    @EventHandler
    public void on(HangingPlaceEvent event) {
        Location location = event.getEntity().getLocation();

        FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(event.getPlayer());

        event.setCancelled(!LandUtils.canBuildAt(user, location));
    }

    @EventHandler
    public void on(HangingBreakByEntityEvent event) {
        Location location = event.getEntity().getLocation();

        if (event.getRemover() instanceof Player) {
            Player player = (Player) event.getRemover();

            FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(player);

            event.setCancelled(!LandUtils.canBuildAt(user, location));

        } else {
            boolean cancelled = LandUtils.is(location, Zone.Type.PROTECTED, Zone.Type.WAR);

            event.setCancelled(cancelled);
        }
    }

    @EventHandler
    public void on(BlockDispenseEntityEvent event) {
        if (event.getEntity() instanceof TNTPrimed) {
            Claim claim = LandUtils.getClaim(event.getBlock().getLocation());

            if (claim == null) {
                return;
            }

            event.getEntity().setMetadata("owner", new FixedMetadataValue(FactionsPlugin.getInstance(), claim.getFaction()));
        }
    }
}
