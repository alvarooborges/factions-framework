package net.hyze.factions.framework.menu.icons;

import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.menu.MenuIcon;
import net.hyze.factions.framework.misc.tpa.inventories.TpaAcceptLogInventory;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class TpaAcceptLogIcon extends MenuIcon {

    public TpaAcceptLogIcon(FactionUser user, Supplier<Inventory> back) {
        super(user, back);
    }

    @Override
    public ItemStack getIcon() {
        ItemBuilder builder = ItemBuilder.of(Material.BOOK_AND_QUILL)
                .glowing(true)
                .name("&aHistórico de TPA")
                .lore(
                        "&7Veja quem aceitou tpa",
                        "&7nas terras da sua facção.",
                        "",
                        "&aClique para visualizar."
                );

        return builder.make();
    }

    @Override
    public Runnable getRunnable() {
        return () -> {
            user.getPlayer().openInventory(new TpaAcceptLogInventory(user, user.getRelation().getFaction()));
        };
    }

    @Override
    public Consumer<InventoryClickEvent> getEvent() {
        return null;
    }


}
