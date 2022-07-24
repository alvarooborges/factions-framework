package net.hyze.factions.framework.spawners.evolutions.impl;

import lombok.Getter;
import net.hyze.factions.framework.spawners.evolutions.Evolution;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
public class RemoveDelayEvolution extends Evolution<Long> {

    private final String id = "remove_delay";

    private final String displayName = "Tempo de remoção";

    private final String[] description = new String[]{
            "Evolua para diminuir o tempo de",
            "espera de remoção deste tipo",
            "de Gerador."
    };

    private final ItemStack icon = new ItemStack(Material.GOLD_PICKAXE);
}
