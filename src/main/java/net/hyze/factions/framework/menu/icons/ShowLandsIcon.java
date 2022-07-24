package net.hyze.factions.framework.menu.icons;

import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.menu.MenuIcon;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ShowLandsIcon extends MenuIcon {

    public ShowLandsIcon(FactionUser user, Supplier<Inventory> back) {
        super(user, back);
    }

    @Override
    public ItemStack getIcon() {

        boolean enable = user.getOptions().isSeeChunksEnabled();

        ItemBuilder builder = ItemBuilder.of(Material.GRASS)
                .name("&aDelimitação de terras &8(/f verterras)")
                .lore(
                        "&7Clique para ativar ou desativar as",
                        "&7partículas que mostram as delimitações",
                        "&7de cada terra.",
                        "",
                        "&fEstado: " + (enable ? "&bAtivado" : "&cDesativado")
                );

        return builder.make();
    }

    @Override
    public Runnable getRunnable() {
        return () -> {
            Player player = user.getPlayer();

            player.closeInventory();
            player.performCommand("f verterras");
        };
    }

    @Override
    public Consumer<InventoryClickEvent> getEvent() {
        return null;
    }

}
