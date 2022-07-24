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

public class FactionsRankIcon extends MenuIcon {

    public FactionsRankIcon(FactionUser user, Supplier<Inventory> back) {
        super(user, back);
    }

    @Override
    public ItemStack getIcon() {
        ItemBuilder builder = ItemBuilder.of(Material.GOLD_BLOCK)
                .name("&aLiga de Facções &8(/f liga)")
                .lore(
                        "&7Confira a classificação",
                        "&7das facções do servidor.",
                        "",
                        "&eClique para mais informações."
                );

        return builder.make();
    }

    @Override
    public Runnable getRunnable() {
        return () -> {
            user.getPlayer().performCommand("f rank");
        };
    }

    @Override
    public Consumer<InventoryClickEvent> getEvent() {
        return null;
    }

}
