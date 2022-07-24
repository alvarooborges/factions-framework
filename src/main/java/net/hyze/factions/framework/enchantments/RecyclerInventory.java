package net.hyze.factions.framework.enchantments;

import net.hyze.core.spigot.inventory.CustomInventory;
import net.hyze.core.spigot.misc.customitem.CustomItem;
import net.hyze.core.spigot.misc.customitem.CustomItemRegistry;
import net.hyze.core.spigot.misc.customitem.data.ExpBottle;
import net.hyze.core.spigot.misc.customitem.data.enchanted_books.VanillaEnchantmentsBook;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantment;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantmentUtil;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.HeadTexture;
import net.hyze.core.spigot.misc.utils.InventoryUtils;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class RecyclerInventory extends CustomInventory {

    private final int[] slots = IntStream.rangeClosed(9, 53).toArray();

    public RecyclerInventory() {
        super(54, "Reciclador");

        for (int i = 0; i < 9; i++) {
            setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).durability(7).name(" ").make());
        }

        setItem(6, new ItemBuilder(HeadTexture.ARROW_WHITE_UP.getHead())
                        .name("&aReciclar livros")
                        .lore("&7Clique para reciclar todos",
                                "&7os livros do seu inventário!")
                        .make(),
                getAllBooksCallback());

        setItem(8, new ItemBuilder(Material.STAINED_GLASS_PANE).durability(5).name("&aAceitar!")
                        .lore("&7Clique para reciclar",
                                "&7os items selecionados!")
                        .make(),
                getAcceptCallback());

        setItem(0, new ItemBuilder(Material.STAINED_GLASS_PANE).durability(14).name("&cCancelar!")
                        .lore("&7Clique para cancelar!")
                        .make(),
                event -> event.getWhoClicked().closeInventory());

        updateResultSlot();
    }

    private Consumer<InventoryClickEvent> getAllBooksCallback() {
        return event -> {
            event.setCancelled(true);

            ItemStack[] contents = event.getWhoClicked().getInventory().getContents();

            for (int slot = 0; slot < contents.length; slot++) {

                ItemStack stack = contents[slot];

                if (stack != null && stack.getType() != Material.AIR) {
                    int freeSlot = findFirstFreeSlot();

                    if (freeSlot < 0) {
                        break;
                    }

                    if (stack.getType() == Material.ENCHANTED_BOOK && getExpReward(stack) > 0) {
                        event.getView().getTopInventory().setItem(freeSlot, stack);
                        event.getWhoClicked().getInventory().setItem(slot, null);
                    }
                }
            }

            updateResultSlot();
        };
    }

    private Consumer<InventoryClickEvent> getAcceptCallback() {
        return event -> {

            if (event.getClick().isCreativeAction() || event.getClick().isKeyboardClick()
                    || event.getClick().isShiftClick() || event.getClickedInventory() == null) {
                return;
            }

            Player player = (Player) event.getWhoClicked();

            int reward = getTotalExpReward();

            for (int slot : slots) {
                if (getItem(slot) != null && getItem(slot).getType() != Material.AIR) {
                    setItem(slot, null);
                }
            }

            if (reward <= 0) {
                player.closeInventory();
                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 0);
                return;
            }

            ExpBottle bottle = new ExpBottle(reward);
            ItemStack stack = bottle.asItemStack();

            if (InventoryUtils.fits(player.getInventory(), stack)) {
                player.getInventory().addItem(stack);
            } else {
                player.getLocation().getWorld().dropItemNaturally(player.getLocation(), stack);
            }

            player.closeInventory();
            Message.EMPTY.send(player, "&aReciclagem feita com sucesso!");
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 0);
        };
    }

    public int getTotalExpReward() {
        int xp = 0;

        for (int slot : slots) {

            if (getItem(slot) != null && getItem(slot).getType() != Material.AIR) {
                xp += getExpReward(getItem(slot));
            }
        }

        return xp;
    }

    public static int getExpReward(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return 0;
        }

        ItemBuilder builder = ItemBuilder.of(item);

        if (builder.hasNbt("exp_bottle_value")) {
            return builder.nbtInt("exp_bottle_value");
        }

        Map<Enchantment, Integer> enchantments;

        if (item.getType().equals(Material.ENCHANTED_BOOK)) {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
            enchantments = meta.getStoredEnchants();
        } else {
            enchantments = item.getEnchantments();
        }


        double xp = 0;

        Map<VanillaEnchantmentsBook, Integer> prices = EnchantmentInventory.getExpPrices();

        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {

            for (Map.Entry<VanillaEnchantmentsBook, Integer> en : prices.entrySet()) {
                if (en.getKey().contains(entry.getKey(), entry.getValue())) {
                    xp += (en.getValue() * 0.25);
                    break;
                }
            }
        }

        Map<CustomEnchantment, Integer> customEnchantments = CustomEnchantmentUtil.getEnchantments(item);

        xp += 500 * customEnchantments.size();

        return (int) xp;
    }

    public int findFirstFreeSlot() {
        for (int slot : slots) {
            if (getItem(slot) == null || getItem(slot).getType() == Material.AIR) {
                return slot;
            }
        }

        return -1;
    }

    public void updateResultSlot() {
        int exp = getTotalExpReward();

        if (exp > 1) {
            ExpBottle bottle = new ExpBottle(exp);
            setItem(4, bottle.asItemStack());
        } else {
            setItem(4, new ItemBuilder(Material.GLASS_BOTTLE).name("&cVazio.").make());
        }
    }

    @Override
    public void onClose(InventoryCloseEvent event) {

        Player player = (Player) event.getPlayer();

        for (int slot : slots) {
            ItemStack stack;
            if ((stack = getItem(slot)) != null && stack.getType() != Material.AIR) {

                if (InventoryUtils.fits(player.getInventory(), stack)) {
                    player.getInventory().addItem(stack);
                } else {
                    player.getLocation().getWorld().dropItemNaturally(player.getLocation(), stack);
                }
            }
        }
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        super.onClick(event);
        Player player = (Player) event.getWhoClicked();

        event.setCancelled(true);

        if (event.getClick().isCreativeAction() || event.getClick().isKeyboardClick()
                || event.getClick().isShiftClick() || event.getClickedInventory() == null) {
            return;
        }

        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        if (event.getClickedInventory().getType().equals(InventoryType.PLAYER)) {

            if (clickedItem.getType().equals(Material.ENCHANTED_BOOK)) {

                CustomItem custom = CustomItemRegistry.getByItemStack(clickedItem);

                if (custom != null) {
                    Message.ERROR.send(player, "Você não pode reciclar esse livro.");
                    return;
                } else {
                    if (CustomEnchantmentUtil.getEnchantments(clickedItem).isEmpty()) {
                        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) clickedItem.getItemMeta();

                        if (meta.getStoredEnchants().isEmpty()) {
                            Message.ERROR.send(player, "Você não pode reciclar esse livro.");
                            return;
                        }
                    }
                }
            } else {

                if (clickedItem.getType().name().startsWith("IRON_")) {
                    Message.ERROR.send(player, "Você não pode reciclar itens de ferro!");
                    return;
                }

                ItemBuilder item = ItemBuilder.of(clickedItem);

                if (!item.hasNbt("exp_bottle_value")) {
                    if (clickedItem.getEnchantments().isEmpty() && CustomEnchantmentUtil.getEnchantments(clickedItem).isEmpty()) {
                        Message.ERROR.send(player, "Você só pode reciclar itens com encantamentos!");
                        return;
                    }
                }
            }

            int slot = findFirstFreeSlot();

            if (slot >= 0) {
                event.getView().getTopInventory().setItem(slot, clickedItem);
                event.getView().getBottomInventory().setItem(event.getSlot(), null);
                event.setCurrentItem(new ItemStack(Material.AIR));

                updateResultSlot();
            }
        } else {

            for (int slot : slots) {

                if (slot == event.getSlot()) {

                    if (!InventoryUtils.fits(event.getInventory())) {
                        Message.ERROR.send(player, "Seu inventário está cheio!");
                        return;
                    }

                    event.getView().getTopInventory().setItem(slot, null);
                    player.getInventory().addItem(clickedItem);
                    updateResultSlot();
                    return;
                }
            }
        }
    }
}