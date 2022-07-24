package net.hyze.factions.framework.spawners.evolutions.impl;

import lombok.Getter;
import net.hyze.factions.framework.spawners.evolutions.Evolution;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
public class StackSizeEvolution extends Evolution<Integer> {

    private final String id = "stack_size";

    private final String displayName = "Agrupamento";

    private final String[] description = new String[]{
            "Evolua para aumentar a quantidade",
            "de criaturas vivas simultaneamente."
    };

    private final ItemStack icon = new ItemStack(Material.NAME_TAG);
}
