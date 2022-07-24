package net.hyze.factions.framework.spawners.properties;

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

public class SlimeProperties extends SpawnerProperties {

    @Override
    public List<ItemStack> getDrops(Player killer) {
        int loot = 0;

        if (killer != null) {
            loot = EnchantmentManager.getBonusMonsterLootEnchantmentLevel(((CraftPlayer) killer).getHandle());
        }

        List<ItemStack> out = new DropsList();

        out.add(new ItemStack(Material.SLIME_BALL, RandomUtils.randomInt(0, 2 + loot)));

        return out;
    }

    @Override
    public int getExp(LivingEntity entity, Player killer) {
        return 2;
    }

    public double modifyHealth(double health) {
        return 60;
    }
}
