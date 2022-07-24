package net.hyze.factions.framework.misc.offers;

import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import net.hyze.core.spigot.misc.utils.InventoryUtils;
import net.hyze.economy.Currency;
import net.hyze.factions.framework.FactionsPlugin;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.misc.utils.FactionUtils;
import net.hyze.factions.framework.spawners.SpawnerType;
import net.hyze.signshop.SignShop;
import net.hyze.signshop.SignShopProvider;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;

public class OfferRunnable implements Runnable {

    @Override
    public void run() {

        Table<Integer, SpawnerType, Integer> table = FactionsProvider.Cache.Local.OFFER_SPAWNER.provide().get(30L * 60000L);

        table.rowKeySet().forEach(factionId -> {

            try {

                if (factionId == null) {
                    return;
                }

                Faction faction = FactionsProvider.Cache.Local.FACTIONS.provide().get(factionId);

                if (faction == null) {
                    return;
                }

                int leaderId = FactionUtils.getLeader(faction).getId();

                table.row(factionId).forEach((type, amount) -> {

                    LinkedList<String> offerItems = Lists.newLinkedList();

                    ItemStack stack = type.getCustomItem().asItemStack(amount);

                    offerItems.add(InventoryUtils.serializeContents(new ItemStack[]{stack}));

                    SignShop shop = SignShopProvider.Cache.Local.SHOPS.provide()
                            .get(type.getCustomItem().getKey())
                            .stream().min((o1, o2) -> o2.getBuyFromShop().compareTo(o1.getBuyFromShop()))
                            .orElse(null);

                    if (shop == null) {
                        return;
                    }

                    int oldPrice = shop.getBuyFromShop().intValue() * amount;
                    int price = (int) (shop.getBuyFromShop() - (shop.getBuyFromShop() * 60d / 100d)) * amount;

                    Offer offer = FactionsProvider.Repositories.OFFERS.provide().insert(
                            leaderId,
                            "Super Oferta!",
                            System.currentTimeMillis() + 86400000L,
                            Currency.COINS,
                            price,
                            oldPrice,
                            offerItems
                    );

                    FactionsProvider.Repositories.OFFERS.provide().log(OfferLogType.RECEIVED_THE_OFFER, leaderId, offer);

                });

            } catch (Exception ignored) {
            }

        });

        Bukkit.getScheduler().runTaskLater(FactionsPlugin.getInstance(), () -> {
            table.rowKeySet().forEach(factionId -> FactionsProvider.Cache.Local.OFFER_SPAWNER.provide().clear(factionId));
        }, 100L);
    }

}
