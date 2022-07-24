package net.hyze.factions.framework.spawners.evolutions.impl;

import lombok.Getter;
import net.hyze.factions.framework.spawners.evolutions.Evolution;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
public class MultiDeathsEvolution extends Evolution<Integer> {

    private final String id = "multi_deaths";

    private final String displayName = "Múltiplas eliminações";

    private final String[] description = new String[]{
            "Evolua para aumentar a quantidade",
            "de criaturas eliminadas de uma",
            "única vez."
    };

    private final ItemStack icon = new ItemStack(Material.SKULL_ITEM);
}
