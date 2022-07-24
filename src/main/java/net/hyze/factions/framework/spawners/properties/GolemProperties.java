package net.hyze.factions.framework.spawners.properties;

import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.misc.utils.RandomUtils;
import net.hyze.core.spigot.misc.customitem.CustomItemRegistry;
import net.hyze.factions.framework.misc.customitem.data.MasterLightningItem;
import net.hyze.factions.framework.spawners.DropsList;
import net.hyze.factions.framework.spawners.SpawnerProperties;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class GolemProperties extends SpawnerProperties {

    @Override
    public List<ItemStack> getDrops(Player killer) {

        if (CoreConstants.RANDOM.nextInt(100) < 70) {
            return Collections.emptyList();
        }

//            int amount = RandomUtils.randomInt(3, 5);
        int amount = RandomUtils.randomInt(0, 1);

        return Collections.singletonList(new ItemStack(Material.IRON_INGOT, amount));
    }

    @Override
    public int getExp(LivingEntity entity, Player killer) {
        return 0;
    }
}
