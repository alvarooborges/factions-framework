package net.hyze.factions.framework.spawners.evolutions;

import com.google.common.collect.ImmutableList;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public abstract class Evolution<T> {

    private final List<EvolutionLevel<T>> levels = new LinkedList<>();

    public abstract String getId();

    public abstract String getDisplayName();

    public abstract String[] getDescription();

    public abstract ItemStack getIcon();

    @SafeVarargs
    public final void registerLevels(EvolutionLevel<T>... lvls) {
        levels.addAll(Arrays.asList(lvls));
    }

    public List<EvolutionLevel<T>> getLevels() {
        return ImmutableList.copyOf(levels);
    }
}
