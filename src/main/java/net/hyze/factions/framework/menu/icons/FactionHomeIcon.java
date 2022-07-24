package net.hyze.factions.framework.menu.icons;

import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.menu.MenuIcon;
import net.hyze.factions.framework.misc.utils.FactionUtils;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class FactionHomeIcon extends MenuIcon {

    public FactionHomeIcon(FactionUser user, Supplier<Inventory> back) {
        super(user, back);
    }

    @Override
    public ItemStack getIcon() {
        ItemBuilder builder = ItemBuilder.of(Material.ENDER_PORTAL_FRAME)
                .name("&aBase &8(/f base)")
                .lore(
                        "&7Clique para teleportar-se até a",
                        "&7a base da sua facção.",
                        "",
                        "&fClique direito: &7Definir base",
                        "&fClique esquerdo: &7Ir até a base",
                        "&fShift + Clique direito: &7Desfazer base"
                );

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

            InventoryAction action = event.getAction();
            if (action == InventoryAction.PICKUP_ALL) {
                 player.performCommand("f base");
            } else if (action == InventoryAction.PICKUP_HALF) {
                player.performCommand("f setbase");
            } else if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {

                FactionUserRelation relation = user.getRelation();
                if (relation == null) {
                    Message.ERROR.send(player,"Não foi possível identificar sua facção.");
                    return;
                }

                Faction faction = relation.getFaction();

                if (FactionUtils.setFactionHome(user, faction, null)) {
                    Message.SUCCESS.send(player,"Base da facção desfeita com sucesso.");
                } else {
                    Message.ERROR.send(player,"Não foi possível desfazer a base da facção.");
                }

            }

        };
    }

}
