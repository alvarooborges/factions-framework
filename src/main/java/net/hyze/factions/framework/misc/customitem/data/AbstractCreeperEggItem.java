package net.hyze.factions.framework.misc.customitem.data;

import net.hyze.core.spigot.misc.customitem.CustomItem;
import net.hyze.core.spigot.misc.stackmobs.StackMobsAPI;
import net.hyze.core.spigot.misc.utils.InventoryUtils;
import net.hyze.core.spigot.misc.utils.NMS;
import net.hyze.factions.framework.FactionsPlugin;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.permission.FactionPermission;
import net.hyze.factions.framework.lands.Claim;
import net.hyze.factions.framework.user.FactionUser;
import net.minecraft.server.v1_8_R3.EntityCreeper;
import org.bukkit.Location;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.greenrobot.eventbus.Subscribe;

public abstract class AbstractCreeperEggItem extends CustomItem {

    public AbstractCreeperEggItem(String key) {
        super(key);
    }

    public abstract Class<? extends EntityCreeper> getCreeperClass();

    @Subscribe
    public void on(PlayerInteractEvent event) {
        if (event.isCancelled()) {
            return;
        }

        event.setCancelled(true);

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Location location = event.getClickedBlock()
                .getRelative(event.getBlockFace())
                .getLocation()
                .clone()
                .add(.5, 0, .5);

        FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(event.getPlayer().getName());

        Claim claim = FactionsProvider.Cache.Local.LANDS.provide().get(location.getBlockX() >> 4, location.getBlockZ() >> 4, Claim.class);

        if (claim != null && !FactionPermission.EXPLOSIONS.allows(claim.getFaction(), user)) {
            return;
        }

        spawnCreeper(getCreeperClass(), location, user);

        InventoryUtils.subtractOneOnHand(event);
    }

    public static void spawnCreeper(Class<? extends EntityCreeper> clasz, Location location, FactionUser user) {
        NMS.spawnCustomEntity(clasz, EntityCreeper.class, location, entity -> {
            entity.getBukkitEntity().setMetadata(StackMobsAPI.PREVENT_STACK_TAG, new FixedMetadataValue(FactionsPlugin.getInstance(), true));

            if (user != null) {
                entity.getBukkitEntity().setMetadata("owner", new FixedMetadataValue(FactionsPlugin.getInstance(), user));
            }
        });
    }
}
