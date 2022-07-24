package net.hyze.factions.framework.ranking;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface Ranking<T> {

    String getName();

    ItemStack getIcon();

    <R extends RankIcon<T>> List<R> getItems();

    void initialize();
}
