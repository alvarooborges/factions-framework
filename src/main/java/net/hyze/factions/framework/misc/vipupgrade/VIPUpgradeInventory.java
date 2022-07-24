package net.hyze.factions.framework.misc.vipupgrade;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.inventory.CustomInventory;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.economy.Currency;
import net.hyze.factions.framework.commands.UpgradeCommand;
import org.bukkit.Material;

public class VIPUpgradeInventory extends CustomInventory {

    public VIPUpgradeInventory(User user, Group currentVIP, Group upgradeVIP) {
        super(9 * 5, "VIP Upgrade");

        String currentVIPTag = currentVIP.getDisplayTag();
        String upgradeVIPTag = upgradeVIP.getDisplayTag();

        Integer price = UpgradeCommand.getPrice(upgradeVIP);

        if (price == null) {
            return;
        }

        String priceString = Currency.CASH.format(price);

        {
            /*
             * INFORMATION ITEM.
             */
            ItemBuilder item = new ItemBuilder(Material.PAPER)
                    .name(String.format("%s &7➜ %s", currentVIPTag, upgradeVIPTag))
                    .lore(
                            "",
                            "&7Ao realizar este upgrade, seu grupo passa",
                            String.format("&7a ser %s&7 até o final deste servidor.", upgradeVIPTag),
                            "",
                            "&fCusto: &6" + priceString
                    );

            setItem(13, item.make());
        }

        {
            /*
             * CONFIRM ITEM.
             */
            ItemBuilder item = new ItemBuilder(Material.STAINED_GLASS_PANE)
                    .durability(5)
                    .name("&aConfirmar")
                    .lore(
                            "&7Cuidado! Esta operação não",
                            "pode ser desfeita."
                    );

            setItem(
                    30,
                    item.make(),
                    event -> {

                        event.getWhoClicked().closeInventory();

                        if (user.getRealCash() < price) {
                            event.getWhoClicked().closeInventory();
                            Message.ERROR.send(
                                    event.getWhoClicked(),
                                    String.format(
                                            "Você precisa de &6%s &cpara comprar.",
                                            priceString
                                    )
                            );
                            return;
                        }

//                        if (CoreProvider.Repositories.SKY_PURCHASES.provide().hasPendentPurchases(user)) {
//                            Message.ERROR.send(event.getWhoClicked(), "Ops, você tem uma compra pendente. Tente novamente mais tarde.");
//                            return;
//                        }

                        User user0 = CoreProvider.Repositories.USERS.provide().fetchById(user.getId());

                        if (user0.getHighestGroup(CoreProvider.getApp().getServer()).equals(upgradeVIP)) {
                            Message.INFO.send(event.getWhoClicked(), "Upgrade realizado com sucesso! Basta relogar.");
                            return;
                        }

//                        CoreProvider.Repositories.SKY_PURCHASES.provide().insertPurchase(
//                                user,
//                                user,
//                                CoreProvider.getApp().getServer(),
//                                "UPGRADE",
//                                0d,
//                                "VIP",
//                                upgradeVIP.name(),
//                                "-1"
//                        );

                        user.decrementCash(price/*, "VIP-UPGRADE-" + upgradeVIP.name() */);

                        CoreProvider.Repositories.GROUPS.provide().removeGroup(user, CoreProvider.getApp().getServer(), currentVIP);
                        CoreProvider.Repositories.GROUPS.provide().addGroup(user, CoreProvider.getApp().getServer(), upgradeVIP);

                        Message.SUCCESS.send(
                                event.getWhoClicked(),
                                "Upgrade realizado com sucesso!"
                        );

                    }
            );
        }

        {
            /*
             * CANCEL ITEM.
             */
            ItemBuilder item = new ItemBuilder(Material.STAINED_GLASS_PANE)
                    .durability(14)
                    .name("&cCancelar");

            setItem(
                    32,
                    item.make(),
                    event -> {
                        event.getWhoClicked().closeInventory();
                    }
            );
        }

    }

}
