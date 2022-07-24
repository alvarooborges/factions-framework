package net.hyze.factions.framework.menu.icons;

import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.menu.MenuIcon;
import net.hyze.factions.framework.menu.inventories.lab.LabIndexInventory;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class LabIcon extends MenuIcon {

    public LabIcon(FactionUser user, Supplier<Inventory> back) {
        super(user, back);
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.CAULDRON_ITEM)
                .name("&2Laboratório")
                .lore(
                        "&7Abrace seu cientista interior",
                        "&7e realize loucuras no laboratório!",
                        "",
                        "&eClique para abrir."
                )
                .make();
    }

    @Override
    public Runnable getRunnable() {
        return () -> user.getPlayer().openInventory(new LabIndexInventory());
    }

    @Override
    public Consumer<InventoryClickEvent> getEvent() {
        return null;
    }
}
