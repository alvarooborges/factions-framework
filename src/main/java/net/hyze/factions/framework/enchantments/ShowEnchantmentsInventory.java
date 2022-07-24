package net.hyze.factions.framework.enchantments;

import net.hyze.core.shared.misc.utils.Pair;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.inventory.PaginateInventory;
import net.hyze.core.spigot.misc.customitem.data.enchanted_books.VanillaEnchantmentsBook;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantment;
import net.hyze.core.spigot.misc.utils.ItemStackUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ShowEnchantmentsInventory extends PaginateInventory {

    protected ShowEnchantmentsInventory(Player player, User user, VanillaEnchantmentsBook book) {
        super("Encantamentos: " + book.getName());

        {
            List<Pair<CustomEnchantment, Integer>> enchantments = book.getCustomEnchantments().keySet()
                    .stream()
                    .sorted(Comparator.comparing(o -> o.getLeft().getKey()))
                    .collect(Collectors.toCollection(LinkedList::new));

            for (Pair<CustomEnchantment, Integer> pair : enchantments) {
                addItem(pair.getLeft().getBook(pair.getRight(), 1));
            }
        }

        {
            List<Pair<Enchantment, Integer>> enchantments = book.getEnchantments().keySet()
                    .stream()
                    .sorted(Comparator.comparing(o -> o.getLeft().getName()))
                    .collect(Collectors.toCollection(LinkedList::new));

            for (Pair<Enchantment, Integer> pair : enchantments) {
                ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);

                ItemStackUtils.addBookEnchantment(item, pair.getLeft(), pair.getRight());

                addItem(item);
            }
        }

        backItem(new EnchantmentInventory(player, user));
    }
}
