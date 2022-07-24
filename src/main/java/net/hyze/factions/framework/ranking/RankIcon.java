package net.hyze.factions.framework.ranking;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public abstract class RankIcon<T> {

    public abstract ItemStack getIcon();

    public abstract T getElement();

    public void onClick(InventoryClickEvent event) {
    }
}
