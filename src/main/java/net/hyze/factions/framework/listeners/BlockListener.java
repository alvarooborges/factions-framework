package net.hyze.factions.framework.listeners;

import dev.utils.shared.Printer;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.LocationUtils;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.lands.Claim;
import net.hyze.factions.framework.lands.Land;
import net.hyze.factions.framework.lands.Zone;
import net.hyze.factions.framework.misc.utils.LandUtils;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Dropper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.inventory.InventoryHolder;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

public class BlockListener implements Listener {

    private static final Set<Material> REDSTONE_MATERIALS = EnumSet.of(
            Material.DIODE_BLOCK_ON,
            Material.DIODE_BLOCK_OFF,
            Material.REDSTONE_COMPARATOR_OFF,
            Material.REDSTONE_COMPARATOR_ON,
            Material.REDSTONE_WIRE);

    private static final BlockFace[] FACES = {BlockFace.NORTH, BlockFace.DOWN, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP};

    @EventHandler(ignoreCancelled = true)
    public void onBlockDispense(BlockDispenseEvent event) {
        Block block = event.getBlock();

        if (block == null) {
            return;
        }

        if (event.getBlock().getState() instanceof Dropper) {

            if (Arrays.stream(FACES).anyMatch(face -> block.getRelative(face).getType().equals(Material.BREWING_STAND))) {
                event.setCancelled(true);

                Arrays.stream(FACES)
                        .filter(face -> REDSTONE_MATERIALS.contains(block.getRelative(face).getType()))
                        .forEach(face -> block.getRelative(face).breakNaturally());
            }
        } else if (event.getBlock().getState() instanceof Dispenser) {

            if (event.getItem() != null && (event.getItem().getType().equals(Material.WATER_BUCKET)
                    || event.getItem().getType().equals(Material.LAVA_BUCKET))) {
                event.setCancelled(true);
                return;
            }

            if (Arrays.stream(FACES).anyMatch(face -> block.getRelative(face).getType().equals(Material.BREWING_STAND))) {
                event.setCancelled(true);

                Arrays.stream(FACES)
                        .filter(face -> REDSTONE_MATERIALS.contains(block.getRelative(face).getType()))
                        .forEach(face -> block.getRelative(face).breakNaturally());
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void on(BlockPlaceEvent event) {
        if (event.getBlock().getLocation().getY() >= 253) {
            event.setCancelled(true);
            return;
        }

        if (LocationUtils.isOutsideBorder(event.getBlock().getLocation())) {
            event.setCancelled(true);
            return;
        }

        try {
            event.setCancelled(!handleBuild(event.getBlock(), event.getPlayer()));
        } catch (Exception e) {
            e.printStackTrace();
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void on(BlockBreakEvent event) {
        try {
            event.setCancelled(!handleBuild(event.getBlock(), event.getPlayer()));
        } catch (Exception e) {
            Printer.ERROR.print("=======");
            Printer.ERROR.print(event.getBlock(), event.getPlayer());
            e.printStackTrace();
            Printer.ERROR.print("=======");
            event.setCancelled(true);
        }
    }

    private static boolean handleBuild(Block block, Player player) {
        Location location = block.getLocation();

        if (block.getState() instanceof InventoryHolder) {

            if (!FactionsProvider.getSettings().isAllowInventoryHolderOutOfLands()) {
                Land land = FactionsProvider.Cache.Local.LANDS.provide().get(CoreProvider.getApp().getId(), location.getChunk().getX(), location.getChunk().getZ());

                if (!(land instanceof Claim)) {
                    Message.ERROR.send(player, "Você só pode colocar baús, ejetores, liberadores ou funís dentro de terras de sua facção.");
                    return false;
                }
            }
        }

        FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(player.getName());

        boolean canBuild = LandUtils.canBuildAt(user, location);


        if (block.getType() == Material.BEACON) {
            boolean canAccessBeacon = LandUtils.canAccessBeaconAt(user, location);
            if (canBuild && !canAccessBeacon) {
                Message.ERROR.send(player, "Você não tem permissão para utilizar sinalizadores nesta terra.");
            }

            canBuild = canAccessBeacon && canBuild;
        } else if (block.getState() instanceof InventoryHolder) {
            boolean canAccessContainers = LandUtils.canAccessContainerAt(user, location);
            if (canBuild && !canAccessContainers) {
                Message.ERROR.send(player, "Você não tem permissão para acessar containers nesta terra.");
            }

            canBuild = canAccessContainers && canBuild;
        }

        return canBuild;
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void on(BlockPistonExtendEvent event) {
        Block block = event.getBlock();
        Block relativeBlock = block.getRelative(event.getDirection(), event.getLength() + 1);

        Location blockLocation = block.getLocation();
        Location relativeLocation = relativeBlock.getLocation();

        Claim blockClaim = FactionsProvider.Cache.Local.LANDS.provide().get(
                blockLocation.getBlockX() >> 4,
                blockLocation.getBlockZ() >> 4,
                Claim.class
        );

        Claim relativeClaim = FactionsProvider.Cache.Local.LANDS.provide().get(
                relativeLocation.getBlockX() >> 4,
                relativeLocation.getBlockZ() >> 4,
                Claim.class
        );

        if (blockClaim == null
                || relativeClaim == null
                || !Objects.equals(blockClaim.getFactionId(), relativeClaim.getFactionId())) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void on(BlockPistonRetractEvent event) {

        Block block = event.getBlock();

        Location blockLocation = block.getLocation();

        Claim blockClaim = FactionsProvider.Cache.Local.LANDS.provide().get(
                blockLocation.getBlockX() >> 4,
                blockLocation.getBlockZ() >> 4,
                Claim.class
        );

        /**
         * Se o pistão estiver em zona livre
         */
        if (blockClaim == null) {
            event.setCancelled(true);
            return;
        }

        boolean matchDiff = event.getBlocks().stream()
                .anyMatch(b -> {
                    Claim bClaim = FactionsProvider.Cache.Local.LANDS.provide().get(
                            b.getLocation().getBlockX() >> 4,
                            b.getLocation().getBlockZ() >> 4,
                            Claim.class
                    );

                    return bClaim == null || !bClaim.getFactionId().equals(blockClaim.getFactionId());
                });

        event.setCancelled(matchDiff);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void on(BlockExplodeEvent event) {
        if (!FactionsProvider.getSettings().isExplosionsEnabled()) {
            event.setCancelled(true);
            event.blockList().clear();
            return;
        }

        Location eventLocation = event.getBlock().getLocation();

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
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void on(BlockBurnEvent event) {
        Location eventLocation = event.getBlock().getLocation();

        Zone zone = LandUtils.getZone(eventLocation);

        if (zone != null && !zone.getType().isBuildEnabled()) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void on(BlockSpreadEvent event) {
        Location eventLocation = event.getBlock().getLocation();

        if (LandUtils.is(eventLocation, Zone.Type.values())) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void on(BlockFadeEvent event) {
        Location eventLocation = event.getBlock().getLocation();

        if (LandUtils.is(eventLocation, Zone.Type.values())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHighest(BlockPhysicsEvent event) {
        Material material = event.getBlock().getType();

        if (material.equals(Material.SAND)
                || material.equals(Material.GRAVEL)
                || material.equals(Material.ANVIL)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void on(BlockFormEvent event) {
        Location eventLocation = event.getBlock().getLocation();

        if (LandUtils.is(eventLocation, Zone.Type.values())) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void on(LeavesDecayEvent event) {
        Location eventLocation = event.getBlock().getLocation();

        if (LandUtils.is(eventLocation, Zone.Type.values())) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void on(BlockFromToEvent event) {
        Material type = event.getBlock().getType();

        if (type.equals(Material.LAVA) || type.equals(Material.STATIONARY_LAVA) || type.equals(Material.DRAGON_EGG)) {
            event.setCancelled(true);
        }
    }
}
