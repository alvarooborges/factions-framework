package net.hyze.factions.framework.spawners.properties;

import net.hyze.core.shared.misc.utils.RandomUtils;
import net.hyze.core.spigot.misc.customitem.CustomItemRegistry;
import net.hyze.factions.framework.misc.customitem.data.MasterLightningItem;
import net.hyze.factions.framework.spawners.DropsList;
import net.hyze.factions.framework.spawners.SpawnerProperties;
import net.minecraft.server.v1_8_R3.EnchantmentManager;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BlazeProperties extends SpawnerProperties {

    @Override
    public List<ItemStack> getDrops(Player killer) {
        int loot = 0;

        if (killer != null) {
            loot = EnchantmentManager.getBonusMonsterLootEnchantmentLevel(((CraftPlayer) killer).getHandle());
        }

        List<ItemStack> out = new DropsList();

        out.add(new ItemStack(Material.BLAZE_ROD, RandomUtils.randomInt(0, 1 + loot)));

        return out;
    }

    @Override
    public int getExp(LivingEntity entity, Player killer) {
        return 1;
    }

    public double modifyHealth(double health) {
        return 45;
    }
}
