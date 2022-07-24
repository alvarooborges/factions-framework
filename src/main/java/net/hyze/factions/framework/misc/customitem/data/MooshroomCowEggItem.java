package net.hyze.factions.framework.misc.customitem.data;

import com.google.common.collect.Lists;
import lombok.Getter;
import net.hyze.core.shared.CoreConstants;
import net.hyze.core.spigot.misc.customitem.CustomItem;
import net.hyze.core.spigot.misc.stackmobs.StackMobsAPI;
import net.hyze.core.spigot.misc.utils.InventoryUtils;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.FactionsConstants;
import net.hyze.factions.framework.FactionsPlugin;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.MushroomCow;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.greenrobot.eventbus.Subscribe;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MooshroomCowEggItem extends CustomItem {

    @Getter
    private final ItemBuilder itemBuilder;

    public MooshroomCowEggItem() {
        super("mooshroom-cow-item");

        this.itemBuilder = ItemBuilder.of(Material.MONSTER_EGG, (short) 96)
                .glowing(true)
                .name("&6Ovo de CoguVaca");
    }

    @Override
    public String getDisplayName() {
        return "&6Ovo de CoguVaca";
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

        Location location = event.getClickedBlock()
                .getRelative(event.getBlockFace())
                .getLocation()
                .clone()
                .add(.5, 0, .5);

        World world = location.getWorld();

        MushroomCow cow = (MushroomCow) world.spawnEntity(location, EntityType.MUSHROOM_COW);

        cow.setMetadata(StackMobsAPI.PREVENT_STACK_TAG, new FixedMetadataValue(FactionsPlugin.getInstance(), true));
                
        cow.setMaxHealth(2048);
        cow.setHealth(2048);

        AtomicInteger count = new AtomicInteger();

        List<Vector> velocities = Lists.newArrayList(
                new Vector(0.2, 0.5, 0),
                new Vector(0, 0.5, 0.2),
                new Vector(0.2, 0.5, 0.2),
                new Vector(0, 0.5, 0),
                new Vector(-0.2, 0.5, 0),
                new Vector(0, 0.5, -0.2),
                new Vector(-0.2, 0.5, -0.2)
        );

        List<Material> material = Lists.newArrayList(
                Material.DIAMOND,
                Material.GOLD_INGOT,
                Material.IRON_INGOT
        );

        new BukkitRunnable() {
            @Override
            public void run() {

                if (cow.isDead()) {
                    this.cancel();
                    return;
                }

                Location location = cow.getLocation();

                if (count.getAndIncrement() > 19) {
                    cow.remove();
                    world.spigot().playEffect(location, Effect.EXPLOSION_HUGE);
                    world.playSound(location, Sound.EXPLODE, 10L, 1L);
                    this.cancel();
                    return;
                }

                for (int i = 0; i < 3; i++) {
                    Collections.shuffle(velocities);
                    Collections.shuffle(material);
                    Entity item = world.dropItem(location, ItemBuilder.of(material.get(0)).nbt(FactionsConstants.NBT_ITEM_GROUP, CoreConstants.RANDOM.nextInt()).make());
                    item.setVelocity(velocities.get(0));
                }

                cow.setHealth(2048);
                cow.damage(1);

                world.playSound(location, Sound.COW_HURT, 10L, 1L);
                world.playSound(location, Sound.COW_HURT, 10L, 1L);
            }
        }.runTaskTimer(FactionsPlugin.getInstance(), 10L, 10L);

        InventoryUtils.subtractOneOnHand(event);
    }
}
