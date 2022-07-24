package net.hyze.factions.framework.spawners.evolutions.impl;

import lombok.Getter;
import net.hyze.factions.framework.spawners.evolutions.Evolution;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
public class ExpMultiplierEvolution extends Evolution<Double> {

    private final String id = "exp_multiplier";

    private final String displayName = "Multiplicador de Experiência";

    private final String[] description = new String[]{
            "Evolua para multiplicar a quantidade",
            "de experiência adquirida ao matar",
            "criaturas deste tipo."
    };

    private final ItemStack icon = new ItemStack(Material.EXP_BOTTLE);
}
