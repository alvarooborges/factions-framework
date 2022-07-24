package net.hyze.factions.framework.spawners.evolutions.impl;

import lombok.Getter;
import net.hyze.factions.framework.spawners.evolutions.Evolution;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
public class SpawnDelayEvolution extends Evolution<Integer> {

    private final String id = "spawn_delay";

    private final String displayName = "Intervalo de geração";

    private final String[] description = new String[]{
            "Evolua para diminuir o tempo entre",
            "cada geração das criaturas."
    };

    private final ItemStack icon = new ItemStack(Material.WATCH);
}
