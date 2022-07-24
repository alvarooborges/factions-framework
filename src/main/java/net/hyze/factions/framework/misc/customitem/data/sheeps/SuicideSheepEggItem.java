package net.hyze.factions.framework.misc.customitem.data.sheeps;

import com.google.common.collect.Lists;
import lombok.Getter;
import net.hyze.core.shared.CoreConstants;
import net.hyze.core.spigot.misc.customitem.CustomItem;
import net.hyze.core.spigot.misc.stackmobs.StackMobsAPI;
import net.hyze.core.spigot.misc.utils.InventoryUtils;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.core.spigot.misc.utils.NMS;
import net.hyze.factions.framework.FactionsConstants;
import net.hyze.factions.framework.FactionsPlugin;
import net.minecraft.server.v1_8_R3.EntitySheep;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Sheep;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.greenrobot.eventbus.Subscribe;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SuicideSheepEggItem extends CustomItem {

    public static final String SHEEP_NBT_KEY = "suicide-sheep-item";

    @Getter
    private final ItemBuilder itemBuilder;

    private final List<Vector> velocities = Lists.newArrayList(
            new Vector(0.2, 0.5, 0),
            new Vector(0, 0.5, 0.2),
            new Vector(0.2, 0.5, 0.2),
            new Vector(0, 0.5, 0),
            new Vector(-0.2, 0.5, 0),
            new Vector(0, 0.5, -0.2),
            new Vector(-0.2, 0.5, -0.2)
    );

    public SuicideSheepEggItem() {
        super(SHEEP_NBT_KEY);

        this.itemBuilder = ItemBuilder.of(Material.MONSTER_EGG, (short) 91)
                .glowing(true)
                .name("&6Ovo de Ovelha Explosiva");
    }

    @Override
    public String getDisplayName() {
        return "&6Ovo de Ovelha Explosiva";
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

        EntitySheep sheep0 = NMS.spawnCustomEntity(EntitySheep.class, EntitySheep.class, location, entity -> {
            entity.getBukkitEntity().setMetadata(StackMobsAPI.PREVENT_STACK_TAG, new FixedMetadataValue(FactionsPlugin.getInstance(), true));
        });
        
        Sheep sheep = (Sheep) sheep0.getBukkitEntity();

        sheep.setMaxHealth(2048);
        sheep.setHealth(2048);

        AtomicInteger count = new AtomicInteger();

        DyeColor color = DyeColor.values()[CoreConstants.RANDOM.nextInt(DyeColor.values().length)];

        List<ItemStack> drops = Lists.newArrayList(
                new ItemStack(Material.BONE),
                new ItemStack(Material.MUTTON)
        );

        List<Material> material = Lists.newArrayList(
                Material.DIAMOND,
                Material.DIAMOND,
                Material.DIAMOND,
                Material.GOLD_INGOT,
                Material.GOLD_INGOT,
                Material.GOLD_INGOT,
                Material.GOLD_INGOT,
                Material.GOLD_INGOT,
                Material.GOLD_INGOT,
                Material.IRON_INGOT,
                Material.IRON_INGOT,
                Material.IRON_INGOT,
                Material.IRON_INGOT,
                Material.IRON_INGOT,
                Material.IRON_INGOT,
                Material.IRON_INGOT,
                Material.IRON_INGOT,
                Material.IRON_INGOT,
                Material.IRON_INGOT,
                Material.IRON_INGOT,
                Material.IRON_INGOT
        );

        drops.add(
                new ItemStack(Material.WOOL, 1, color.getWoolData()));

        sheep.setVelocity(
                new Vector(0, 1.5, 0));

        sheep.setColor(color);

        world.playSound(location, Sound.FIREWORK_LAUNCH,
                10, 0);

        new BukkitRunnable() {
            @Override
            public void run() {

                if (sheep.isDead()) {
                    this.cancel();
                    return;
                }

                Location location = sheep.getLocation();

                if (count.getAndIncrement() > 8) {
                    sheep.remove();
                    world.spigot().playEffect(location, Effect.EXPLOSION_HUGE);
                    world.playSound(location, Sound.EXPLODE, 10L, 1L);
                    world.playSound(location, Sound.SHEEP_IDLE, 10L, 1L);

                    for (int i = 0; i < 10; i++) {
                        Collections.shuffle(velocities);
                        Collections.shuffle(drops);
                        Entity item = world.dropItem(
                                location,
                                ItemBuilder.of(drops.get(0))
                                .nbt(FactionsConstants.NBT_ITEM_GROUP, CoreConstants.RANDOM.nextInt())
                                .make()
                        );
                        item.setVelocity(velocities.get(0));
                    }

                    for (Material targetMaterial : material) {
                        Collections.shuffle(velocities);
                        Entity item = world.dropItem(
                                location,
                                ItemBuilder.of(targetMaterial)
                                .nbt(FactionsConstants.NBT_ITEM_GROUP, CoreConstants.RANDOM.nextInt())
                                .make()
                        );
                        item.setVelocity(velocities.get(0));
                    }
                    this.cancel();
                }

            }
        }
                .runTaskTimer(FactionsPlugin.getInstance(), 3L, 3L);

        InventoryUtils.subtractOneOnHand(event);
    }
}
