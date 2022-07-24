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

public class LeaveIcon extends MenuIcon {

    public LeaveIcon(FactionUser user, Supplier<Inventory> back) {
        super(user, back);
    }

    @Override
    public ItemStack getIcon() {
        ItemBuilder builder = ItemBuilder.of(Material.DARK_OAK_DOOR_ITEM)
                .name("&cSair da Facção")
                .lore(
                        "&7Ao clicar neste ícone você",
                        "&7abandonará a sua facção.",
                        "",
                        "&eDica: &fUse /f sair",
                        "",
                        "&aClique para sair."
                );

        return builder.make();
    }

    @Override
    public Runnable getRunnable() {
        return () -> {
            user.getPlayer().performCommand("f sair");
        };
    }

    @Override
    public Consumer<InventoryClickEvent> getEvent() {
        return null;
    }

}
