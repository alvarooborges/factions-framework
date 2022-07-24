package net.hyze.factions.framework.misc.offers;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.hyze.core.spigot.misc.customitem.CustomItem;
import net.hyze.core.spigot.misc.customitem.CustomItemRegistry;
import net.hyze.core.spigot.misc.utils.InventoryUtils;
import net.hyze.economy.Currency;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;

@Getter
@RequiredArgsConstructor
public class Offer {
    
    private final int id;

    /**
     * Nome da oferta / nome do pacote que é oferecido ao jogador.
     */
    private final String name;

    /**
     * 
     */
    private final Long expireTime;

    /**
     * Moeda utilizada para realizar a compra.
     */
    private final Currency currency;

    /**
     * Valor da oferta / valor pago por TODOS os itens desta oferta.
     */
    private final int price;

    /**
     * Preço original da oferta.
     */
    private final int oldPrice;

    /**
     * O itemID pode ser tanto um item serializado, quanto o id de um
     * CustomItem.
     */
    private final LinkedList<String> offerItems;

    public LinkedList<ItemStack> getItems() {
        LinkedList<ItemStack> list = Lists.newLinkedList();

        this.offerItems.forEach(itemId -> {

            CustomItem customItem = CustomItemRegistry.getItem(itemId);

            if (customItem == null) {
                try {

                    ItemStack[] itemSerialized = InventoryUtils.deserializeContents(itemId);

                    if (itemSerialized.length > 0) {
                        list.add(itemSerialized[0]);
                    }

                } catch (Exception e) {
                }
                return;
            }

            list.add(customItem.asItemStack());

        });

        return list;
    }

}
