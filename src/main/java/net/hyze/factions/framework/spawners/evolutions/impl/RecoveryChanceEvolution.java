package net.hyze.factions.framework.spawners.evolutions.impl;

import lombok.Getter;
import net.hyze.factions.framework.spawners.evolutions.Evolution;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
public class RecoveryChanceEvolution extends Evolution<Integer> {

    private final String id = "recovery_chance";

    private final String displayName = "Chance de recuperação";

    private final String[] description = new String[]{
            "Evolua para aumentar a chance do",
            "Gerador voltar para o armazém ao",
            "ser explodido por um creeper."
    };

    private final ItemStack icon = new ItemStack(Material.EYE_OF_ENDER);
}
