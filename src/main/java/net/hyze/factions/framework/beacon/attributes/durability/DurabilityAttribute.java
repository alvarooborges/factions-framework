package net.hyze.factions.framework.beacon.attributes.durability;

import net.hyze.beacon.attributes.data.buff.BuffAttribute;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class DurabilityAttribute extends BuffAttribute {

    public static final String ID = "durability";

    public DurabilityAttribute() {
        super(
                ID,
                new ItemStack(Material.BEDROCK),
                "Durabilidade",
                new String[]{
                    "&7Aumente o número de explosões necessárias",
                    "&7para que o sinalizador seja explodido."
                },
                Color.BLACK,
                new String[]{
                    "&7Este atributo serve para deixar o",
                    "&7seu Sinalizador mais resistente a",
                    "&7explosões. Dessa forma, quando um",
                    "&7inimigo tentar roubá-lo, serão",
                    "&7necessárias várias explosões para",
                    "&7conseguir destruir seu Sinalizador."
                }
        );

        addLevel(new DurabilityAttributeLevel(1));
        addLevel(new DurabilityAttributeLevel(2));
        addLevel(new DurabilityAttributeLevel(3));
        addLevel(new DurabilityAttributeLevel(4));
        addLevel(new DurabilityAttributeLevel(5));
        addLevel(new DurabilityAttributeLevel(6));
        addLevel(new DurabilityAttributeLevel(7));
        addLevel(new DurabilityAttributeLevel(8));
        addLevel(new DurabilityAttributeLevel(9));
        addLevel(new DurabilityAttributeLevel(10));

    }

}
