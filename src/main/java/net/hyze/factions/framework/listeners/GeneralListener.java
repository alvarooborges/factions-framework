package net.hyze.factions.framework.listeners;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.spigot.events.chat.PlayerLocalChatEvent;
import net.hyze.core.spigot.misc.tpa.events.TPAcceptEvent;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.FactionsConstants;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.lands.Claim;
import net.hyze.factions.framework.lands.Zone;
import net.hyze.factions.framework.misc.chat.ChatManager;
import net.hyze.factions.framework.misc.utils.LandUtils;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.painting.PaintingPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.stream.Stream;

public class GeneralListener implements Listener {

    @EventHandler
    public void on(TPAcceptEvent event) {
        if (!CoreProvider.getApp().getType().equals(AppType.FACTIONS_VIP)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void on(PaintingPlaceEvent event) {
        event.setCancelled(true);
    }

    /*
    @EventHandler
    public void on(ChunkLoadEvent event) {
        Stream.of(event.getChunk().getEntities())
                .forEach(
                        entity -> {

                            if (entity instanceof Painting) {
                                entity.remove();
                                return;
                            }

                            if (CoreProvider.getApp().getType().equals(AppType.FACTIONS_WORLD)) {
                                if (entity instanceof LivingEntity && !(entity instanceof Player)) {
                                    StackedEntityWrapper stacked = StackMobsAPI.getStackedEntity(entity);

                                    if (stacked.getSize() < 10) {
                                        System.out.println("Removed " + entity.getType());
                                        entity.remove();
                                    }
                                }
                            }


                        }
                );

        if (CoreProvider.getApp().getType().equals(AppType.FACTIONS_WORLD)
                || CoreProvider.getApp().getType().equals(AppType.FACTIONS_TESTS)) {
            Claim claim = FactionsProvider.Cache.Local.LANDS.provide().get(
                    CoreProvider.getApp().getId(),
                    event.getChunk().getX(),
                    event.getChunk().getZ(),
                    Claim.class);

            if (claim == null || claim.getFaction() == null) {
                Stream.of(event.getChunk().getTileEntities())
                        .filter(blockState -> blockState instanceof InventoryHolder)
                        .forEach(entity -> entity.getBlock().setType(Material.AIR));
            }
        }
    }
     */

    @EventHandler
    public void on(ChunkUnloadEvent event) {
        if (CoreProvider.getApp().getType().equals(AppType.FACTIONS_WORLD)
                || CoreProvider.getApp().getType().equals(AppType.FACTIONS_TESTS)) {
            Claim claim = FactionsProvider.Cache.Local.LANDS.provide().get(
                    CoreProvider.getApp().getId(),
                    event.getChunk().getX(),
                    event.getChunk().getZ(),
                    Claim.class);

            if (claim == null || claim.getFaction() == null) {
                Stream.of(event.getChunk().getTileEntities())
                        .filter(blockState -> blockState instanceof InventoryHolder)
                        .forEach(entity -> entity.getBlock().setType(Material.AIR));
            }
        }
    }


    @EventHandler
    public void on(PlayerPortalEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void on(WeatherChangeEvent event) {
        if (event.toWeatherState()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void on(AsyncPlayerChatEvent event) {
        event.setCancelled(true);

        Player player = event.getPlayer();

        FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(player.getName());

        PlayerLocalChatEvent localEvent = new PlayerLocalChatEvent(player, user.getHandle(), event.getMessage());

        Bukkit.getPluginManager().callEvent(localEvent);

        if (localEvent.isCancelled()) {
            return;
        }

        ChatManager.sendLocalChatMessage(user, localEvent.getMessage());
    }

    public void on(FoodLevelChangeEvent event) {
        if (event.getFoodLevel() < ((Player) event.getEntity()).getFoodLevel()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void on(PlayerPickupItemEvent event) {

        ItemBuilder itemBuilder = ItemBuilder.of(event.getItem().getItemStack());

        if (itemBuilder.hasNbt(FactionsConstants.NBT_ITEM_GROUP)) {
            event.setCancelled(true);

            itemBuilder.removeNbt(FactionsConstants.NBT_ITEM_GROUP);

            event.getItem().remove();

            Player player = event.getPlayer();

            player.getInventory().addItem(itemBuilder.make());
            player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1, 1);
        }
    }

    @EventHandler
    public void on(PlayerArmorStandManipulateEvent event) {
        if (LandUtils.getZone(event.getRightClicked().getLocation()).getType() == Zone.Type.PROTECTED) {
            Player player = event.getPlayer();

            FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(player.getName());

            event.setCancelled(!user.getOptions().isAdminModeEnabled());
        }
    }

    @EventHandler
    public void on(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof ArmorStand) {
            Zone zone = LandUtils.getZone(event.getEntity().getLocation());

            if (zone != null && zone.getType() == Zone.Type.PROTECTED) {

                if (event.getDamager() instanceof Player) {
                    Player player = (Player) event.getDamager();

                    FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(player.getName());

                    event.setCancelled(!user.getOptions().isAdminModeEnabled());
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }
}
