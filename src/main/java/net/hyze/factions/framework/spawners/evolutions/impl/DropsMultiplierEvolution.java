package net.hyze.factions.framework.spawners.evolutions.impl;

import lombok.Getter;
import net.hyze.factions.framework.spawners.evolutions.Evolution;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
public class DropsMultiplierEvolution extends Evolution<Double> {

    private final String id = "drops_multiplier";

    private final String displayName = "Multiplicador de drops";

    private final String[] description = new String[]{
            "Evolua para aumentar a quantidade",
            "de itens dropados pelas criaturas",
            "deste tipo de Gerador."
    };

    private final ItemStack icon = new ItemStack(Material.DIAMOND);
}
