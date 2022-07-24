package net.hyze.factions.framework.spawners.properties;

import com.google.common.collect.Lists;
import net.hyze.core.shared.misc.utils.RandomUtils;
import net.hyze.factions.framework.spawners.SpawnerProperties;
import net.minecraft.server.v1_8_R3.EnchantmentManager;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ZombieProperties extends SpawnerProperties {

    @Override
    public List<ItemStack> getDrops(Player killer) {
        int loot = 0;

        if (killer != null) {
            loot = EnchantmentManager.getBonusMonsterLootEnchantmentLevel(((CraftPlayer) killer).getHandle());
        }

        return Lists.newArrayList(
//                    new ItemStack(Material.ROTTEN_FLESH, RandomUtils.randomInt(0, 2 + loot))
                new ItemStack(Material.ROTTEN_FLESH, RandomUtils.randomInt(0, 1 + loot))
        );
    }

    @Override
    public int getExp(LivingEntity entity, Player killer) {
        return 1;
    }
}
