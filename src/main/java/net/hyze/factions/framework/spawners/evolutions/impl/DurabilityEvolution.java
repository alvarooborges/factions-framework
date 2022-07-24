package net.hyze.factions.framework.spawners.evolutions.impl;

import lombok.Getter;
import net.hyze.factions.framework.spawners.evolutions.Evolution;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
public class DurabilityEvolution extends Evolution<Integer> {

    private final String id = "durability";

    private final String displayName = "Durabilidade";

    private final String[] description = new String[]{
            "Evolua para aumentar a durabilidade",
            "deste tipo de Gerador."
    };

    private final ItemStack icon = new ItemStack(Material.NETHER_STAR);
}
