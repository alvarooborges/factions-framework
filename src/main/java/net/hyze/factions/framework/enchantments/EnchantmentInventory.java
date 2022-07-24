package net.hyze.factions.framework.enchantments;

import com.google.common.collect.Maps;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.misc.customitem.CustomItemRegistry;
import net.hyze.core.spigot.misc.customitem.data.ExpBottle;
import net.hyze.core.spigot.misc.customitem.data.enchanted_books.*;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.shop.CustomShopItem;
import net.hyze.core.spigot.misc.shop.ShopInventory;
import net.hyze.core.spigot.misc.shop.module.currency.ExpPrice;
import net.hyze.core.spigot.misc.shop.module.currency.prices.CashPrice;
import net.hyze.core.spigot.misc.utils.InventoryUtils;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.core.spigot.misc.utils.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.function.Consumer;

public class EnchantmentInventory extends ShopInventory {

    private static Map<VanillaEnchantmentsBook, Integer> EXP_PRICE;

    public EnchantmentInventory(Player player, User user) {
        super(4 * 9, "Encantamentos", user);

        int slot = 10;

        for (Map.Entry<VanillaEnchantmentsBook, Integer> entry : getExpPrices().entrySet()) {

            CustomShopItem item = new CustomShopItem(
                    entry.getKey(),
                    new ExpPrice(entry.getValue()), 1,
                    (u, s, t) -> player.openInventory(new EnchantmentInventory(player, user))
            );

            Consumer<InventoryClickEvent> itemConsumer = item.getConsumer(user);
            ItemBuilder itemIcon = item.build(user);

            itemIcon.lore("", "&7Botão esquerdo: &fComprar", "&7Botão direito: &fVer encantamentos");

            setItem(
                    slot,
                    itemIcon.make(),
                    event -> {
                        if (event.getClick() == ClickType.LEFT) {
                            itemConsumer.accept(event);
                        } else if (event.getClick() == ClickType.RIGHT) {
                            player.openInventory(new ShowEnchantmentsInventory(player, user, entry.getKey()));
                        }
                    });

            slot += 2;
        }

        setItem(16, new CustomShopItem(
                CustomItemRegistry.getItem(DivineEnchantmentsBook.KEY),
                new CashPrice(375), 1,
                (u, s, t) -> player.openInventory(new EnchantmentInventory(player, user))
        ));


        // XP para frasco
        {
            int exp = (int) (PlayerUtils.getExp(player) * 0.95f);

            ItemBuilder expIcon;

            if (exp > 0) {
                expIcon = new ItemBuilder(new ExpBottle(exp).asItemStack())
                        .lore(
                                "",
                                "&cAo fazer isso, você perderá 5% do XP",
                                "&aClique para converter seu XP em Frasco."
                        );
            } else {
                expIcon = new ItemBuilder(Material.GLASS_BOTTLE)
                        .name("&cVocê não possui XP!");
            }

            setItem(31, expIcon.make(), event -> {
                int currentExp = (int) (PlayerUtils.getExp(player) * 0.95f);

                if (currentExp > 0) {

                    ExpBottle expBottle = new ExpBottle(currentExp);
                    ItemStack reward = expBottle.asItemStack();

                    if (!InventoryUtils.fits(player.getInventory(), reward)) {
                        Message.ERROR.send(player, "&cSeu inventário está cheio!");
                        return;
                    }

                    PlayerUtils.changeExp(player, -PlayerUtils.getExp(player));
                    player.getInventory().addItem(reward);
                }

                player.openInventory(new EnchantmentInventory(player, user));
            });
        }

        {
            ItemBuilder builder = ItemBuilder.of(Material.ANVIL)
                    .name("&eReciclar")
                    .lore(
                            "",
                            "&7Troque seus itens encantados",
                            "&7por XP!",
                            "",
                            "&8Você receberá um valor de XP",
                            "&8para cada encantamento do item.",
                            "",
                            "&7Use: &f/reciclar"
                    );

            setItem(32, builder.make(), event -> {
                player.openInventory(new RecyclerInventory());
            });
        }
    }

    public static Map<VanillaEnchantmentsBook, Integer> getExpPrices() {

        if (EXP_PRICE == null) {
            EXP_PRICE = Maps.newLinkedHashMap();

            EXP_PRICE.put((VanillaEnchantmentsBook) CustomItemRegistry.getItem(SimpleVanillaEnchantmentsBook.KEY), 1500);
            EXP_PRICE.put((VanillaEnchantmentsBook) CustomItemRegistry.getItem(NormalVanillaEnchantmentsBook.KEY), 3000);
            EXP_PRICE.put((VanillaEnchantmentsBook) CustomItemRegistry.getItem(AdvancedVanillaEnchantmentsBook.KEY), 5500);
        }

        return EXP_PRICE;
    }

}
