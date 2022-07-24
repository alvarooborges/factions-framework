package net.hyze.factions.framework.spawners.properties;

import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.misc.utils.RandomUtils;
import net.hyze.factions.framework.spawners.DropsList;
import net.hyze.factions.framework.spawners.SpawnerProperties;
import net.minecraft.server.v1_8_R3.EnchantmentManager;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SpiderProperties extends SpawnerProperties {

    @Override
    public List<ItemStack> getDrops(Player killer) {
        int loot = 0;

        if (killer != null) {
            loot = EnchantmentManager.getBonusMonsterLootEnchantmentLevel(((CraftPlayer) killer).getHandle());
        }

        List<ItemStack> out = new DropsList();

        out.add(new ItemStack(Material.STRING, RandomUtils.randomInt(0, 2 + loot)));

        if (CoreConstants.RANDOM.nextInt(3) == 0 || CoreConstants.RANDOM.nextInt(1 + loot) > 0) {
            out.add(new ItemStack(Material.SPIDER_EYE, 1));
        }

        return out;
    }

    @Override
    public int getExp(LivingEntity entity, Player killer) {
        return 5;
    }
}
