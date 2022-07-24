package net.hyze.factions.framework.divinealtar.power.impl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.misc.utils.RandomList;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.misc.customitem.CustomItemRegistry;
import net.hyze.core.spigot.misc.customitem.data.LauncherItem;
import net.hyze.core.spigot.misc.stackmobs.events.StackMobDeathEvent;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.divinealtar.inventory.AltarInventory;
import net.hyze.factions.framework.divinealtar.power.Power;
import net.hyze.factions.framework.divinealtar.power.PowerCurrency;
import net.hyze.factions.framework.divinealtar.power.PowerInstance;
import net.hyze.factions.framework.lands.Claim;
import net.hyze.factions.framework.misc.utils.LandUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.stream.Stream;

public class ProsperityPower implements Power, Listener {

    private final Multimap<Integer, String> DROPS = ArrayListMultimap.create();

    @Override
    public String getId() {
        return "prosperity";
    }

    @Override
    public String getName() {
        return "Prosperidade";
    }

    @Override
    public String getGemName() {
        return "Gema da Prosperidade";
    }

    @Override
    public ItemBuilder getIcon() {
        return ItemBuilder.of(Material.GOLD_INGOT);
    }

    @Override
    public String[] getDescription() {
        return new String[]{
            "Ao matar mobs em terrenos",
            "da sua facção, eles podem",
            "dropar itens especiais."
        };
    }

    @Override
    public Long rechargeTime() {
        /**
         * 60 minutos.
         */
        return 60L * 60000L;
    }

    @Override
    public Long activeTime() {
        /**
         * 20 minutos.
         */
        return 20L * 60000L;
    }

    @Override
    public PowerCurrency getCurrency() {
        return PowerCurrency.CASH;
    }

    @Override
    public Integer getPrice() {
        return 700;
    }

    @Override
    public void onActivate(User user) {

        Player player = Bukkit.getPlayerExact(user.getNick());
        Location location = player.getLocation();
        Claim claim = LandUtils.getClaim(location);

        if (claim == null) {
            return;
        }

        DROPS.removeAll(claim.getFactionId());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void on(StackMobDeathEvent event) {

        Entity entity = event.getStackedEntity().getEntity();

        Location location = entity.getLocation();

        Claim claim = LandUtils.getClaim(location);

        if (claim == null) {
            return;
        }

        AltarInventory out = FactionsProvider.Cache.Local.ALTAR.provide().getCache().values().stream()
                .filter(
                        inventory -> inventory.getAltarProperties()
                        .getActivePowers()
                        .containsKey(PowerInstance.PROSPERITY)
                )
                .filter(inventory -> {
                    return inventory.getFactionId() == claim.getFactionId();
                })
                .findFirst()
                .orElse(null);

        if (out == null) {
            return;
        }

        /**
         * DROPA ITEM ESPECIAL.
         */
        RandomList<Drop> random = new RandomList<>();

        Stream.of(Drop.values()).forEach(drop -> random.add(drop, drop.getWeight()));

        for (int i = 0; i < event.getDeathAmount(); i++) {

            Drop drop = random.raffle();

            ItemStack item = drop.getItem();

            if (item.getType() != Material.AIR) {

                int amount = (int) DROPS.get(claim.getFactionId()).stream().filter(tgt -> tgt.equalsIgnoreCase(drop.name())).count();

                if (amount < drop.getMaxDrops()) {
                    location.getWorld().dropItem(location, item);
                    DROPS.put(claim.getFactionId(), drop.name());
                }

            }
        }

    }

    @Getter
    @RequiredArgsConstructor
    private enum Drop {

        DIAMOND_HELMET(
                ItemBuilder.of(Material.DIAMOND_HELMET)
                .enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .enchantment(Enchantment.DURABILITY, 3)
                .make(),
                20,
                6
        ),
        DIAMOND_CHESTPLATE(
                ItemBuilder.of(Material.DIAMOND_CHESTPLATE)
                .enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .enchantment(Enchantment.DURABILITY, 3)
                .make(),
                20,
                6
        ),
        DIAMOND_LEGGINGS(
                ItemBuilder.of(Material.DIAMOND_LEGGINGS)
                .enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .enchantment(Enchantment.DURABILITY, 3)
                .make(),
                20,
                6
        ),
        DIAMOND_BOOTS(
                ItemBuilder.of(Material.DIAMOND_BOOTS)
                .enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .enchantment(Enchantment.DURABILITY, 3)
                .make(),
                20,
                6
        ),
        REPAIR_COIN(
                CustomItemRegistry.getItem("custom_item_repair_coin").asItemStack(),
                90,
                64
        ),
        DIAMOND(
                new ItemStack(Material.DIAMOND, 6),
                100,
                320
        ),
        LAUNCHER(
                CustomItemRegistry.getItem(LauncherItem.KEY).asItemStack(1),
                40,
                15
        ),
//        MAX_POWER(
//                CustomItemRegistry.getItem("max-power-item").asItemStack(),
//                1,
//                1
//        ),
        GOLDEN_APPLE(
                new ItemStack(Material.GOLDEN_APPLE, 9),
                20,
                45
        ),
        IRON_BLOCK(
                new ItemStack(Material.IRON_BLOCK, 9),
                102,
                640
        ),
        AIR(
                new ItemStack(Material.AIR),
                25000,
                1
        );

        private final ItemStack item;
        private final int weight;
        private final int maxDrops;

    }

}
