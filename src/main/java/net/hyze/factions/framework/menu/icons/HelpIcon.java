package net.hyze.factions.framework.menu.icons;

import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.menu.MenuIcon;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class HelpIcon extends MenuIcon {

    public HelpIcon(FactionUser user, Supplier<Inventory> back) {
        super(user, back);
    }

    @Override
    public ItemStack getIcon() {
        ItemBuilder builder = ItemBuilder.of(Material.BOOK_AND_QUILL)
                .name("&aAjuda &8(/f ajuda)")
                .lore(
                        "&7Dúvidas? Clique e receba",
                        "&7um guia de comandos!"
                );

        return builder.make();
    }

    @Override
    public Runnable getRunnable() {
        return () -> {
            user.getPlayer().performCommand("f ajuda");
            user.getPlayer().closeInventory();
        };
    }

    @Override
    public Consumer<InventoryClickEvent> getEvent() {
        return null;
    }

}
