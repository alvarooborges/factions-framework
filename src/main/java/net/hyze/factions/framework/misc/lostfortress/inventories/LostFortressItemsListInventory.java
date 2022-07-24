package net.hyze.factions.framework.misc.lostfortress.inventories;

import net.hyze.core.spigot.inventory.CustomInventory;
import net.hyze.core.spigot.inventory.PaginateInventory;
import net.hyze.core.spigot.misc.utils.InventoryUtils;
import net.hyze.factions.framework.misc.lostfortress.LostFortress;
import net.hyze.factions.framework.user.FactionUser;

public class LostFortressItemsListInventory extends PaginateInventory {

    public LostFortressItemsListInventory(LostFortress log, FactionUser user, CustomInventory backInventory) {
        super("Itens pegos por " + user.getHandle().getNick());

        if (!log.getItems().containsKey(user.getId())) {
            return;
        }

        log.getItems().get(user.getId()).forEach(
                string -> {
                    addItem(InventoryUtils.deserializeContents(string)[0]);
                }
        );

        if (backInventory != null) {
            backItem(backInventory);
        }

    }

}
