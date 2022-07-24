package net.hyze.factions.framework.divinealtar.manager;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AltarManager {

    public static ItemStack buildAltarItem() {
        return new ItemBuilder(new ItemStack(Material.ENDER_PORTAL_FRAME))
                .name("&bAltar Divino")
                .make();
    }

}
