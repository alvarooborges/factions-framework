package net.hyze.factions.framework.listeners.player;

import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.misc.cooldowns.UserCooldowns;
import net.hyze.core.shared.misc.utils.Plural;
import net.hyze.core.spigot.CoreSpigotConstants;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.EntityUtils;
import net.hyze.core.spigot.misc.utils.LocationUtils;
import net.hyze.core.spigot.misc.utils.WorldCuboid;
import net.hyze.factions.framework.FactionsPlugin;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.lands.Zone;
import net.hyze.factions.framework.misc.utils.LandUtils;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.event.entity.EntityMountEvent;

import java.util.concurrent.TimeUnit;

public class PlayerInteractListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onHigh(EntityMountEvent event) {
        if (event.getEntity() instanceof Player) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onHigh(PlayerInteractEntityEvent event) {
        EntityType type = event.getRightClicked().getType();

        if (type == EntityType.BOAT || type == EntityType.MINECART || type == EntityType.HORSE) {
            event.setCancelled(true);
            return;
        }

        if (LocationUtils.isOutsideBorder(event.getRightClicked().getLocation())) {
            event.setCancelled(true);
            return;
        }

        if (!AppType.FactionsAppType.isCurrentAllowClaim()) {
            FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(event.getPlayer());

            if (event.getRightClicked() instanceof ItemFrame) {
                event.setCancelled(!LandUtils.canBuildAt(user, event.getRightClicked().getLocation()));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onHigh(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        ItemStack item = player.getItemInHand();

        if (item == null) {
            return;
        }

        FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(player);

        switch (item.getType()) {

            case ITEM_FRAME: {
                if (event.hasBlock() && !event.isCancelled()) {

                    Location location = event.getClickedBlock().getRelative(event.getBlockFace()).getLocation();

                    event.setCancelled(!LandUtils.canBuildAt(user, location));
                }

                break;
            }

            case BANNER: {

                if (!event.isCancelled()) {
                    if (!user.getOptions().isAdminModeEnabled()) {
                        event.setUseItemInHand(Event.Result.DENY);
                        Message.ERROR.send(player, "&cNão é possível utilizar banners. :(");
                    }
                }

                break;
            }

            case BOAT: {
                if (!user.getOptions().isAdminModeEnabled()) {
                    event.setUseItemInHand(Event.Result.DENY);
                }

                break;
            }

            case ENDER_PEARL: {
                if (event.useItemInHand() != Event.Result.DENY) {
                    if (!UserCooldowns.hasEnded(user.getHandle(), "enderpearl")) {
                        event.setUseItemInHand(Event.Result.DENY);

                        int secs = UserCooldowns.getSecondsLeft(user.getHandle(), "enderpearl");

                        Message.ERROR.send(player, String.format(
                                "Você precisa esperar %s %s para jogar outra %s.",
                                secs, Plural.SECOND.of(secs), CoreSpigotConstants.TRANSLATE_ITEM.get(item)
                        ));

                        return;
                    }

                    if (EntityUtils.isStucked(player)) {
                        event.setUseItemInHand(Event.Result.DENY);
                        Message.ERROR.send(player, "Você não pode jogar uma Ender Pearl estando dentro de um bloco.");
                        return;
                    }

                    if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        if (!AppType.FACTIONS_TESTS.isCurrent()) {
                            UserCooldowns.start(user.getHandle(), "enderpearl", 15, TimeUnit.SECONDS);
                        }
                    }
                }
                break;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onLowest(PlayerInteractEvent event) {
        try {
            Block block = event.getClickedBlock();
            Location location = block.getLocation();

            FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(event.getPlayer().getName());

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {

                if (block.getType().equals(Material.BED_BLOCK)) {
                    event.setCancelled(true);
                    Message.ERROR.send(event.getPlayer(), "Não há tempo para dormir. D:");
                    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.VILLAGER_NO, 10, 1);
                    return;
                }

                if (block.getState() instanceof InventoryHolder) {
                    event.setCancelled(!LandUtils.canAccessContainerAt(user, location));
                }

                switch (block.getType()) {
                    case BEACON:
                        event.setCancelled(!LandUtils.canAccessBeaconAt(user, location));
                        break;
                    case LEVER:
                    case STONE_BUTTON:
                    case WOOD_BUTTON:
                    case REDSTONE_COMPARATOR_OFF:
                    case REDSTONE_COMPARATOR_ON:
                    case DIODE_BLOCK_OFF:
                    case DIODE_BLOCK_ON:
                    case DAYLIGHT_DETECTOR:
                    case DAYLIGHT_DETECTOR_INVERTED:
                    case TNT:
                        event.setCancelled(!LandUtils.canActivateRedstoneAt(user, location));
                        break;
                    case ANVIL:

                        Zone zone = LandUtils.getZone(location);
                        if (zone != null && zone.getType() != Zone.Type.PROTECTED) {
                            event.setCancelled(!LandUtils.canAccessContainerAt(user, location));
                        }

                        break;
                }
            }

            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                if (block.getType() == Material.ANVIL) {
                    if (AppType.FACTIONS_SAFE.isCurrent()) {
                        return;
                    }

                    event.setCancelled(!LandUtils.canAccessContainerAt(user, location));
                }
            }

            if (event.isCancelled()) {
                event.setUseInteractedBlock(Event.Result.DENY);
                event.setUseItemInHand(Event.Result.ALLOW);
            } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {

                if (event.getItem() != null && event.getItem().getType().isBlock()) {

                    Location relativeLocation = block.getRelative(event.getBlockFace()).getLocation();
                    Location roundPlayerLocation = event.getPlayer().getLocation().getBlock().getLocation();

                    if (!LandUtils.canBuildAt(user, block.getRelative(event.getBlockFace()).getLocation())) {

                        WorldCuboid cuboid = new WorldCuboid(
                                roundPlayerLocation.clone().add(-1, -2, -1),
                                roundPlayerLocation.clone().add(1, -1, 1)
                        );

                        if (cuboid.contains(relativeLocation, false)) {
                            event.setCancelled(true);

                            Location fixLocation = event.getPlayer().getLocation();
                            event.getPlayer().teleport(fixLocation);
                            event.getPlayer().teleport(fixLocation);

                            Bukkit.getScheduler().runTask(FactionsPlugin.getInstance(), () -> {
                                if (event.getPlayer().isOnline()) {
                                    event.getPlayer().teleport(fixLocation);
                                    event.getPlayer().teleport(fixLocation);
                                }
                            });
                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            event.setCancelled(true);
        }
    }
}
