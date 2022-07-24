package net.hyze.factions.framework.enchantments;

import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.inventory.PaginateInventory;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantment;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantmentRegistry;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class AllEnchantmentsInventory extends PaginateInventory {

    public AllEnchantmentsInventory(Player player, User user) {
        super("Todos Encantamentos");

        for (CustomEnchantment enchantment : CustomEnchantmentRegistry.getItems()) {
            ItemBuilder builder = new ItemBuilder(Material.ENCHANTED_BOOK)
                    .name("&e" + enchantment.getDisplayName())
                    .lore(enchantment.getDescription());

            addItem(builder.make());
        }

        backItem(new EnchantmentInventory(player, user));
    }

}
