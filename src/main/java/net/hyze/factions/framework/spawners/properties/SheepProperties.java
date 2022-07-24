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

public class SheepProperties extends SpawnerProperties {

    @Override
    public List<ItemStack> getDrops(Player killer) {
        int loot = 0;

        if (killer != null) {
            loot = EnchantmentManager.getBonusMonsterLootEnchantmentLevel(((CraftPlayer) killer).getHandle());
        }

        List<ItemStack> out = new DropsList();

        out.add(new ItemStack(Material.WOOL, RandomUtils.randomInt(0, 2 + loot)));

            /*
            if (CoreConstants.RANDOM.nextInt(1000000) / 10000f <= 0.15) {
                CustomItem sheepEgg = CustomItemRegistry.getItem(SuicideSheepEggItem.SHEEP_NBT_KEY);

                if (sheepEgg != null) {
                    ItemStack sheepEggDrop = sheepEgg.asItemStack(1);

                    ItemBuilder.of(sheepEggDrop, true).nbt("bonus_drop_disabled", true);

                    out.add(sheepEggDrop);
                }
            }
             */
        double bonusChance = 0.33;

        if (loot > 0) {
            switch (loot) {
                case 1:
                    bonusChance = 0.5;
                    break;
                case 2:
                    bonusChance = 0.66;
                    break;
                default:
                    bonusChance = 0.75;
                    break;
            }
        }

        if (CoreConstants.RANDOM.nextDouble() * 100 <= bonusChance) {
            out.add(new ItemStack(Material.WOOL, 1));
        }

        return out;
    }

    @Override
    public int getExp(LivingEntity entity, Player killer) {
        return 1;
    }
}
