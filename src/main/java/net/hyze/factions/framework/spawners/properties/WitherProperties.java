package net.hyze.factions.framework.spawners.properties;

import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.misc.utils.RandomUtils;
import net.hyze.core.spigot.misc.customitem.CustomItem;
import net.hyze.core.spigot.misc.customitem.CustomItemRegistry;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.misc.customitem.data.CreeperEggItem;
import net.hyze.factions.framework.spawners.DropsList;
import net.hyze.factions.framework.spawners.SpawnerProperties;
import net.hyze.mysterybox.MysteryBoxProvider;
import net.hyze.mysterybox.box.Box;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class WitherProperties extends SpawnerProperties {

    @Override
    public List<ItemStack> getDrops(Player killer) {
        List<ItemStack> out = new DropsList();

        int max = RandomUtils.randomInt(1, 3);
        for (int i = 0; i < max; i++) {
            out.add(new ItemStack(Material.NETHER_STAR));
        }

        if (CoreConstants.RANDOM.nextInt(1000000) / 10000f <= 0.0015) {
            CustomItem creeperEgg = CustomItemRegistry.getItem(CreeperEggItem.NBT_KEY);

            if (creeperEgg != null) {
                ItemStack creeperEggDrop = creeperEgg.asItemStack(1);

                ItemBuilder.of(creeperEggDrop, true).nbt("bonus_drop_disabled", true);

                out.add(creeperEggDrop);
            }
        }

        if (CoreConstants.RANDOM.nextInt(10000000) / 100000f <= 0.00005) {
            Box box = MysteryBoxProvider.Cache.Local.BOX.provide().getMysteryBox("Divina");

            if (box != null) {
                ItemStack boxDrop = box.create(1, null);
                ItemBuilder.of(boxDrop, true).nbt("bonus_drop_disabled", true);

                out.add(boxDrop);
            }
        }

        return out;
    }

    @Override
    public double modifyHealth(double health) {
        return 160;
    }

    @Override
    public int getExp(LivingEntity entity, Player killer) {
        return 1;
    }
}
