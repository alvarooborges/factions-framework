package net.hyze.factions.framework.listeners.player;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.misc.cooldowns.UserCooldowns;
import net.hyze.core.shared.user.User;
import net.hyze.core.shared.world.location.SerializedLocation;
import net.hyze.core.spigot.CoreSpigotConstants;
import net.hyze.core.spigot.CoreSpigotPlugin;
import net.hyze.core.spigot.CoreSpigotSettings;
import net.hyze.core.spigot.events.PlayerTeleportManagerEvent;
import net.hyze.core.spigot.misc.customitem.CustomItem;
import net.hyze.core.spigot.misc.customitem.data.LauncherItem;
import net.hyze.core.spigot.misc.customitem.data.PropellantItem;
import net.hyze.core.spigot.misc.customitem.data.SupremeLauncherItem;
import net.hyze.core.spigot.misc.customitem.events.PlayerUseCustomItemEvent;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.lands.Zone;
import net.hyze.factions.framework.lands.Zone.Type;
import net.hyze.factions.framework.misc.customitem.data.AbstractCreeperEggItem;
import net.hyze.factions.framework.misc.customitem.data.MasterLightningItem;
import net.hyze.factions.framework.misc.utils.FactionUtils;
import net.hyze.factions.framework.misc.utils.LandUtils;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.Collection;
import java.util.EnumSet;

public class PlayerListener implements Listener {

    @EventHandler
    public void on(PlayerTeleportManagerEvent event) {
        FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(event.getPlayer());

        if (user.getHandle().hasGroup(Group.ARCANE) && FactionUtils.canTeleportTo(user, event.getPlayer().getLocation())) {
            user.getStats().setBackLocation(event.getPlayer().getLocation());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onHighest(PlayerPickupItemEvent event) {
        User user = CoreProvider.Cache.Local.USERS.provide().get(event.getPlayer().getName());
        event.setCancelled(!UserCooldowns.hasEnded(user, "user-death"));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();
        if (event.getFoodLevel() < player.getFoodLevel()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on(PlayerRespawnEvent event) {
        if (CoreSpigotPlugin.getInstance().isNPC(event.getPlayer())) {
            return;
        }

        event.getPlayer().setFallDistance(0);

        event.setRespawnLocation(SerializedLocation.of(
                CoreProvider.getApp().getId(),
                CoreSpigotSettings.getInstance().getWorldName(),
                CoreSpigotSettings.getInstance().getSpawnPosition()
        ).parser(CoreSpigotConstants.LOCATION_PARSER));
    }

    private final Multimap<Class<? extends CustomItem>, Type> blockedCustomItemInZone = HashMultimap.create();

    {
        blockedCustomItemInZone.putAll(LauncherItem.class, EnumSet.of(Type.PROTECTED, Type.WAR, Type.VOID));
        blockedCustomItemInZone.putAll(SupremeLauncherItem.class, EnumSet.of(Type.PROTECTED, Type.WAR, Type.VOID));
        blockedCustomItemInZone.putAll(PropellantItem.class, EnumSet.of(Type.PROTECTED, Type.WAR, Type.VOID));
        blockedCustomItemInZone.putAll(AbstractCreeperEggItem.class, EnumSet.of(Type.PROTECTED, Type.WAR, Type.VOID));
        blockedCustomItemInZone.putAll(MasterLightningItem.class, EnumSet.of(Type.PROTECTED));
    }

    @EventHandler
    public void on(PlayerUseCustomItemEvent event) {
        if (event.getItem().getKey().equalsIgnoreCase("creeper-item") || event.getItem().getKey().equalsIgnoreCase("super-creeper-item")) {
            if(!AppType.FactionsAppType.isCurrentAllowClaim()){
                Message.ERROR.send(event.getPlayer(), "Você não pode utilizar este item aqui.");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHighest(PlayerUseCustomItemEvent event) {
        Collection<Zone.Type> zones = blockedCustomItemInZone.get(event.getItem().getClass());

        if (!zones.isEmpty()) {
            Zone zone = LandUtils.getZone(event.getPlayer().getLocation());

            if (zone != null && zones.contains(zone.getType())) {
                event.setCancelled(true);
                Message.ERROR.send(event.getPlayer(), "Você não pode utilizar este item aqui.");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void on(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();

        FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(player);

        Location location = event.getBlockClicked().getRelative(event.getBlockFace()).getLocation();

        event.setCancelled(!LandUtils.canBuildAt(user, location));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void on(PlayerBucketFillEvent event) {
        Player player = event.getPlayer();

        FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(player);

        Location location = event.getBlockClicked().getRelative(event.getBlockFace()).getLocation();

        event.setCancelled(!LandUtils.canBuildAt(user, location));
    }
}
