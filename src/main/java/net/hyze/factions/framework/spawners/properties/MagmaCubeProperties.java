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

public class MagmaCubeProperties extends SpawnerProperties {

    @Override
    public List<ItemStack> getDrops(Player killer) {
        int loot = 0;

        if (killer != null) {
            loot = EnchantmentManager.getBonusMonsterLootEnchantmentLevel(((CraftPlayer) killer).getHandle());
        }

        List<ItemStack> out = new DropsList();

        int amount = RandomUtils.randomInt(0, 2 + loot);
        if (amount > 0) {
            out.add(new ItemStack(Material.MAGMA_CREAM, amount));
        }

        return out;
    }

    @Override
    public int getExp(LivingEntity entity, Player killer) {
        return 1;
    }

    public double modifyHealth(double health) {
        return 85;
    }

}
