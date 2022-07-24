package net.hyze.factions.framework.divinealtar.misc.customitems;

import net.hyze.core.spigot.misc.customitem.CustomItem;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.InventoryUtils;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.FactionsPlugin;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.greenrobot.eventbus.Subscribe;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class MeteorRainItem extends CustomItem implements Listener {

    public static final Long COOLDOWN = 3L * 60000L; // 3 minutos.

    private final String CREATED_AT_KEY = "meteor_rain_created_at";

    public MeteorRainItem() {
        super("meteor_rain");
    }

    @Override
    public ItemBuilder getItemBuilder() {
        return ItemBuilder.of(Material.REDSTONE_TORCH_ON)
                .name(getDisplayName())
                .lore(
                        "&7Utilize este item no local onde",
                        "&7você deseja lançar a chuva de",
                        "&7meteoros!"
                );
    }

    @Override
    public String getDisplayName() {
        return "&cSinalizador de Ataque";
    }

    @Subscribe
    public void on(PlayerInteractEvent event) {
        
        event.setCancelled(true);

        if (!(event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK))) {
            return;
        }

        Player player = event.getPlayer();
        ItemBuilder item = ItemBuilder.of(event.getPlayer().getItemInHand());
        InventoryUtils.subtractOneOnHand(player);

        if (!item.hasNbt(CREATED_AT_KEY)) {
            return;
        }

        if ((System.currentTimeMillis() - item.nbtLong(CREATED_AT_KEY)) > COOLDOWN) {
            Message.ERROR.send(player, "Ops, este item já expirou!");
            return;
        }

        Random random = new Random();

        playParticleEffect(player.getLocation());

        Location location = player.getLocation().add(0, 20, 0);
        AtomicInteger rainCound = new AtomicInteger();

        Bukkit.getScheduler().runTaskLater(FactionsPlugin.getInstance(), () -> {

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (rainCound.getAndIncrement() >= 16) {
                        this.cancel();
                        return;
                    }

                    FallingBlock falling = location.getWorld().spawnFallingBlock(
                            location.clone().add(
                                    random.nextInt(10 + 8) + 8,
                                    0,
                                    random.nextInt(10 + 8) + 8
                            ),
                            Material.COAL_BLOCK,
                            (byte) 0
                    );

                    falling.setDropItem(false);

                    location.getWorld().playSound(falling.getLocation(), Sound.EXPLODE, 1, 15);

                    Vector target = location.toVector().subtract(falling.getLocation().toVector()).normalize();
                    target.multiply(0.7);
                    falling.setVelocity(target);

                }
            }.runTaskTimer(FactionsPlugin.getInstance(), 7L, 7L);

        }, 4 * 20L);

    }

    @Override
    public ItemStack asItemStack() {
        return ItemBuilder.of(super.asItemStack())
                .nbt(CREATED_AT_KEY, System.currentTimeMillis())
                .make();
    }

    @EventHandler
    public void on(EntityChangeBlockEvent event) {
        if (!(event.getEntity() instanceof FallingBlock)) {
            return;
        }

        event.setCancelled(true);

        if (event.getTo().equals(Material.COAL_BLOCK)) {
            Location location = event.getBlock().getLocation();

            TNTPrimed tnt = (TNTPrimed) location.getWorld().spawn(location, TNTPrimed.class);
            tnt.setFuseTicks(0);

            location.getWorld().spigot().playEffect(location, Effect.LARGE_SMOKE);
        }
    }

    private void playParticleEffect(Location location) {

        World world = location.getWorld();

        AtomicInteger particleCount = new AtomicInteger();

        Location loc0 = location.clone().add(10, 0, 10);
        Location loc1 = location.clone().add(-10, 0, -10);

        int minX = Math.min(loc0.getBlockX(), loc1.getBlockX());
        int minY = Math.min(loc0.getBlockY(), loc1.getBlockY());
        int minZ = Math.min(loc0.getBlockZ(), loc1.getBlockZ());
        int maxX = Math.max(loc0.getBlockX(), loc1.getBlockX());
        int maxY = Math.max(loc0.getBlockY(), loc1.getBlockY());
        int maxZ = Math.max(loc0.getBlockZ(), loc1.getBlockZ());

        Random random = new Random();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (particleCount.getAndIncrement() == 30) {
                    this.cancel();
                }

                location.getWorld().playSound(location, Sound.FIZZ, 1, 15);

                for (int x = minX; x <= maxX; x++) {
                    for (int y = minY; y <= maxY; y++) {
                        for (int z = minZ; z <= maxZ; z++) {

                            float chance = random.nextFloat();

                            if (chance <= 0.10f) {

                                Block block = world.getHighestBlockAt(x, z);

                                Packet packet = new PacketPlayOutWorldParticles(
                                        EnumParticle.REDSTONE,
                                        true,
                                        (float) x,
                                        (float) (block.getY() + .2),
                                        (float) z,
                                        (float) 255,
                                        (float) 0,
                                        (float) 0,
                                        (float) 0,
                                        0,
                                        0
                                );

                                Bukkit.getOnlinePlayers().forEach(player -> {
                                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
                                });

                            }

                        }
                    }
                }

            }
        }.runTaskTimer(FactionsPlugin.getInstance(), 3L, 3L);

    }

}
