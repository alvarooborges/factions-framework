package net.hyze.factions.framework.spawners;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class SpawnerProperties {

    public abstract List<ItemStack> getDrops(Player killer);

    public abstract int getExp(LivingEntity entity, Player killer);

    public double modifyHealth(double health) {
        return health;
    }
}
