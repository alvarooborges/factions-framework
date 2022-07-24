package net.hyze.factions.framework.misc.customitem.data;

import lombok.Getter;
import net.hyze.core.spigot.misc.customitem.CustomItem;
import net.hyze.core.spigot.misc.customitem.INonStackable;
import net.hyze.core.spigot.misc.utils.InventoryUtils;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class MasterLightningItem extends CustomItem implements INonStackable {

    @Getter
    private final ItemBuilder itemBuilder;

    public static final String KEY = "master-lightning-item";

    public MasterLightningItem() {
        super(KEY);

        this.itemBuilder = ItemBuilder.of(Material.BLAZE_ROD)
                .glowing(true)
                .name("&6Raio Mestre");
    }

    @Override
    public String getDisplayName() {
        return "&6Raio Mestre";
    }


    @Subscribe
    public void on(PlayerInteractEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        event.setCancelled(true);

        call(event.getPlayer(), event.getRightClicked().getLocation());
    }

    @Subscribe
    public void on(PlayerInteractEvent event) {

        if (event.isCancelled()) {
            return;
        }

        event.setCancelled(true);

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        call(event.getPlayer(), event.getClickedBlock()
                .getRelative(event.getBlockFace())
                .getLocation()
                .clone()
                .add(.5, 0, .5));
    }

    private void call(Player player, Location location) {
        WorldServer world = ((CraftWorld) location.getWorld()).getHandle();

        EntityLightning lightning = new EntityLightning(world, location.getX(), location.getY(), location.getZ(), true) {

            @Override
            public void t_() {
                super.t_();

                double d0 = 3.0D;
                List<Entity> list = this.world.getEntities(this, new AxisAlignedBB(this.locX - d0, this.locY - d0, this.locZ - d0, this.locX + d0, this.locY + 6.0D + d0, this.locZ + d0));

                for (Entity entity : list) {
                    if (entity instanceof EntityCreeper) {
                        entity.onLightningStrike(this);
                    }
                }
            }
        };

        world.strikeLightning(lightning);

        InventoryUtils.subtractOneOnHand(player);
    }
}
