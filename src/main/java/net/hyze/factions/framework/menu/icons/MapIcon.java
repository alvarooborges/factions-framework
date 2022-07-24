package net.hyze.factions.framework.menu.icons;

import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.menu.MenuIcon;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class MapIcon extends MenuIcon {

    public MapIcon(FactionUser user, Supplier<Inventory> back) {
        super(user, back);
    }

    @Override
    public ItemStack getIcon() {
        ItemBuilder builder = ItemBuilder.of(Material.MAP)
                .name("&aMapa &8(/f mapa)")
                .lore(
                        "&7Veja no chat um mapa que exibe",
                        "&7todas as terras das facções",
                        "&7próximas a você!",
                        "",
                        "&fClique esquerdo: &7Receber o mapa.",
                        "&fClique direito: &7Ativar o modo mapa."
                )
                .flags(ItemFlag.HIDE_ATTRIBUTES);

        return builder.make();
    }

    @Override
    public Runnable getRunnable() {
        return null;
    }

    @Override
    public Consumer<InventoryClickEvent> getEvent() {
        return event -> {
            Player player = (Player) event.getWhoClicked();

            player.closeInventory();

            if (event.getAction() == InventoryAction.PICKUP_ALL) {
                player.performCommand("f mapa");
            } else if (event.getAction() == InventoryAction.PICKUP_HALF) {

               if (user.getOptions().isAutoMapEnabled()) {
                   player.performCommand("f mapa off");
               } else {
                   player.performCommand("f mapa on");
               }
            }
        };
    }

}
