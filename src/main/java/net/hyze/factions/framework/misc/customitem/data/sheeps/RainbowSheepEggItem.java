package net.hyze.factions.framework.misc.customitem.data.sheeps;

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
import org.bukkit.entity.Sheep;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.greenrobot.eventbus.Subscribe;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RainbowSheepEggItem extends CustomItem {

    @Getter
    private final ItemBuilder itemBuilder;

    public RainbowSheepEggItem() {
        super("rainbow-sheep-item");

        this.itemBuilder = ItemBuilder.of(Material.MONSTER_EGG, (short) 91)
                .glowing(true)
                .name("&6Ovo de Ovelha Colorida II");
    }

    @Override
    public String getDisplayName() {
        return "&6Ovo de Ovelha Colorida II";
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

        Sheep sheep = (Sheep) world.spawnEntity(location, EntityType.SHEEP);

        sheep.setMetadata(StackMobsAPI.PREVENT_STACK_TAG, new FixedMetadataValue(FactionsPlugin.getInstance(), true));

        sheep.setMaxHealth(2048);
        sheep.setHealth(2048);
        
        sheep.setCustomName("jeb_");
        sheep.setCustomNameVisible(false);

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

                if (sheep.isDead()) {
                    this.cancel();
                    return;
                }

                Location location = sheep.getLocation();

                if (count.getAndIncrement() > 19) {
                    sheep.remove();
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

                sheep.setHealth(2048);
                //sheep.damage(1);
                
                sheep.setVelocity(new Vector(0, 0.1, 0));

                world.playSound(location, Sound.SHEEP_IDLE, 10L, 1L);
                world.playSound(location, Sound.ITEM_PICKUP, 10L, 0L);
            }
        }.runTaskTimer(FactionsPlugin.getInstance(), 10L, 10L);

        InventoryUtils.subtractOneOnHand(event);
    }
}
