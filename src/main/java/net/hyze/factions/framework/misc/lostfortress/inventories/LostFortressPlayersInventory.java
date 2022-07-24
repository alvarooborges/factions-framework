package net.hyze.factions.framework.misc.lostfortress.inventories;

import net.hyze.core.spigot.inventory.CustomInventory;
import net.hyze.core.spigot.inventory.PaginateInventory;
import net.hyze.core.spigot.misc.utils.HeadTexture;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.misc.lostfortress.LostFortress;
import net.hyze.factions.framework.user.FactionUser;

public class LostFortressPlayersInventory extends PaginateInventory {

    public LostFortressPlayersInventory(LostFortress log, CustomInventory backInventory) {
        super("Jogadores que pegaram itens");

        log.getItems().forEach((userId, items) -> {
            LostFortress.UserInfo userInfo = log.getUsers().get(userId);

            FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(userId);

            ItemBuilder icon = ItemBuilder.of(HeadTexture.getPlayerHead(user.getNick()))
                    .name(userInfo.getDisplayName())
                    .lore("&fQuantidade de itens coletados: &7" + items.size());

            addItem(
                    icon.make(),
                    event -> {
                        event.getWhoClicked().openInventory(
                                new LostFortressItemsListInventory(
                                        log,
                                        user,
                                        this
                                )
                        );
                    }
            );
        });

        if (backInventory != null) {
            backItem(backInventory);
        }
    }

}
