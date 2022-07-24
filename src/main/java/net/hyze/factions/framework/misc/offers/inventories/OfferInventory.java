package net.hyze.factions.framework.misc.offers.inventories;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.messages.MessageUtils;
import net.hyze.core.shared.misc.utils.TimeCode;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.inventory.ConfirmInventory;
import net.hyze.core.spigot.inventory.PaginateInventory;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.InventoryUtils;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.economy.Currency;
import net.hyze.economy.EconomyAPI;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.misc.offers.Offer;
import net.hyze.factions.framework.misc.offers.OfferLogType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;

public class OfferInventory extends PaginateInventory {

    public OfferInventory(LinkedList<Offer> offers) {
        super("Ofertas");

        offers.stream()
                .filter(offer -> offer.getExpireTime() > System.currentTimeMillis())
                .forEach(offer -> {

                    ItemBuilder icon = new ItemBuilder(Material.STORAGE_MINECART)
                            .name("&e" + offer.getName())
                            .lore("&fConteúdo da oferta:");

                    offer.getItems()
                            .forEach(item -> {
                                icon.lore(
                                        String.format(
                                                " &8▪ &e%sx %s",
                                                item.getAmount(),
                                                MessageUtils.stripColor(item.getItemMeta().getDisplayName())
                                        )
                                );
                            });

                    icon.lore(
                            "",
                            "&fPreço original: &8&m" + offer.getCurrency().format(offer.getOldPrice()),
                            "&fPreço da oferta: &6" + offer.getCurrency().format(offer.getPrice()),
                            "",
                            String.format("&eOferta válida durante &a%s&e.", TimeCode.toText(offer.getExpireTime() - System.currentTimeMillis(), 4)),
                            "",
                            "&aClique para comprar!"
                    );

                    addItem(
                            icon.make(),
                            event -> {

                                ConfirmInventory confirmInventory = new ConfirmInventory(
                                        event_ -> {

                                            Player player = (Player) event_.getWhoClicked();

                                            User user = CoreProvider.Cache.Local.USERS.provide().get(player.getName());

                                            boolean hasCurrency;

                                            if (offer.getCurrency().equals(Currency.CASH)) {
                                                hasCurrency = user.getRealCash() > offer.getPrice();
                                            } else {
                                                hasCurrency = EconomyAPI.get(user, Currency.COINS) > offer.getPrice();
                                            }

                                            if (!hasCurrency) {
                                                Message.ERROR.send(
                                                        player,
                                                        String.format(
                                                                "Ops, você precisa de %s para realizar esta transação.",
                                                                offer.getCurrency().format(offer.getPrice())
                                                        )
                                                );

                                                player.closeInventory();
                                                return;
                                            }

                                            LinkedList<ItemStack> items0 = offer.getItems();

                                            ItemStack[] items = items0.toArray(new ItemStack[items0.size()]);

                                            if (!InventoryUtils.fits(player.getInventory(), items)) {
                                                Message.ERROR.send(
                                                        player,
                                                        "Ops, você não tem espaço suficiente em seu inventário!"
                                                );

                                                player.closeInventory();
                                                return;
                                            }

                                            FactionsProvider.Repositories.OFFERS.provide().delete(offer.getId());
                                            FactionsProvider.Repositories.OFFERS.provide().log(OfferLogType.MADE_THE_PURCHASE, user.getId(), offer);

                                            player.getInventory().addItem(items);

                                            if (offer.getCurrency().equals(Currency.CASH)) {
                                                user.decrementCash(offer.getPrice());
                                            } else {
                                                EconomyAPI.remove(user, Currency.COINS, (double) offer.getPrice());
                                            }

                                        },
                                        event_ -> {
                                            Player player = (Player) event_.getWhoClicked();
                                            player.closeInventory();
                                            Message.ERROR.send(player, "Operação cancelada.");
                                        },
                                        new ItemBuilder(Material.STORAGE_MINECART)
                                        .name("&e" + offer.getName())
                                        .make()
                                );

                                event.getWhoClicked().openInventory(confirmInventory.make("Esta ação não pode ser desfeita!"));

                            }
                    );

                });

        addMenu(
                49,
                new ItemBuilder(Material.BOOK)
                .name("&eInformações")
                .lore(
                        "&7Neste menu você pode encontrar",
                        "&7ofertas de diversos itens! As",
                        "&7ofertas são grandes descontos",
                        "&7em um pacote de itens.",
                        "",
                        "&7Sempre que você receber uma",
                        "&7oferta, te enviaremos uma",
                        "&7notificação no chat!"
                )
                .make()
        );

        setEmptyIcon(
                new ItemBuilder(Material.WEB)
                .name("&cVocê não possui nenhuma")
                .lore("&coferta disponível!")
                .make()
        );

        setDefaultHotbar(false);

    }

}
