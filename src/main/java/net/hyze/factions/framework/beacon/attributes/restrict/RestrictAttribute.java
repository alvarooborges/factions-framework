package net.hyze.factions.framework.beacon.attributes.restrict;

import net.hyze.beacon.attributes.AttributeToggleable;
import net.hyze.beacon.attributes.data.buff.BuffAttribute;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class RestrictAttribute extends BuffAttribute implements AttributeToggleable {

    public static final String ID = "restrict";

    public RestrictAttribute() {
        super(
                ID,
                new ItemStack(Material.GRASS),
                "Restringir Efeitos",
                new String[]{
                    "&7Lorem ipsum dolor",
                    "&7sit amet."
                },
                Color.BLACK,
                new String[]{
                    "&7Lorem ipsum dolor sit amet"
                }
        );

        addLevel(new RestrictAttributeLevel(getName()));

    }

}
