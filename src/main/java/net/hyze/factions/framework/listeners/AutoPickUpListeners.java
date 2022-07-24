package net.hyze.factions.framework.listeners;

import net.hyze.core.shared.misc.utils.RandomUtils;
import net.hyze.hyzeskills.datatypes.skills.XPGainReason;
import net.hyze.hyzeskills.skills.herbalism.HerbalismManager;
import net.hyze.hyzeskills.util.player.UserManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;

public class AutoPickUpListeners implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void on(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Block block = event.getBlock();

        Player player = event.getPlayer();

        HerbalismManager herbalismManager = UserManager.getPlayer(player).getHerbalismManager();

        switch (block.getType()) {
            case NETHER_WARTS:
                event.setCancelled(true);

                handleNetherWarts(player, block, herbalismManager);
                break;

            case SUGAR_CANE_BLOCK:
                event.setCancelled(true);

                handleSugarCane(player, block, herbalismManager);
                break;
            case MELON_BLOCK:
                event.setCancelled(true);

                if (herbalismManager != null) {
                    herbalismManager.applyXpGain(
                            2.0F,
                            XPGainReason.PVE
                    );
                }

                handleWaterMelon(player, block);
                break;
        }
    }

    private void handleSugarCane(Player player, Block block, HerbalismManager herbalismManager) {
        Location location = block.getLocation();

        LinkedList<Block> blocks = new LinkedList<>();

        blocks.add(block);

        int canes = 1;

        for (int y = 0; y < 3; y++) {
            location.setY(block.getY() + y);

            Block relative = location.getBlock();
            if (relative.getType() == Material.SUGAR_CANE_BLOCK) {
                blocks.addFirst(relative);

                canes++;
            }
        }

        int size = blocks.size();

        if (herbalismManager != null && canes >= 3) {
            herbalismManager.applyXpGain(
                    2.0F,
                    XPGainReason.PVE
            );
        }

        ItemStack itemStack = new ItemStack(Material.SUGAR_CANE, size > 1 ? size - 1 : size);

        player.getInventory().addItem(itemStack);

        for (Block relative : blocks) {
            relative.setType(Material.AIR);
        }
    }

    private void handleWaterMelon(Player player, Block block) {
        block.setType(Material.AIR);

        ItemStack itemStack = null;

        ItemStack itemInHand = player.getItemInHand();
        if (itemInHand != null) {

            if (itemInHand.containsEnchantment(Enchantment.SILK_TOUCH)) {
                itemStack = new ItemStack(Material.MELON_BLOCK);
            } else {
                itemStack = new ItemStack(Material.MELON, getWaterMelonAmountByFortune(itemInHand));
            }
        }

        if (itemStack == null) {
            return;
        }

        player.getInventory().addItem(itemStack).forEach(
                (amount, item) -> player.getWorld().dropItem(player.getLocation(), item)
        );
    }

    private void handleNetherWarts(Player player, Block block, HerbalismManager herbalismManager) {
        int amount = 1;

        byte data = block.getData();

        if (data == 3) {

            if (herbalismManager != null) {
                herbalismManager.applyXpGain(
                        2.0F,
                        XPGainReason.PVE
                );
            }

            block.setData((byte) 0);
            block.getState().update();
        } else {
            block.setType(Material.AIR);
        }

        if (player.getItemInHand() != null) {
            if (data == 3) {
                amount = getNetherWartsAmountByFortune(player.getItemInHand());
            }
        }

        ItemStack itemStack = new ItemStack(Material.NETHER_STALK, amount);

        player.getInventory().addItem(itemStack).forEach(
                (amount0, item) -> player.getWorld().dropItem(player.getLocation(), item)
        );
    }

    private int getNetherWartsAmountByFortune(ItemStack itemStack) {
        int enchantmentLevel = itemStack.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);

        switch (enchantmentLevel) {
            case 1:
                return RandomUtils.randomInt(2, 5);
            case 2:
                return RandomUtils.randomInt(2, 6);
            case 3:
                return RandomUtils.randomInt(2, 7);
            default:
                return RandomUtils.randomInt(2, 4);
        }
    }

    private int getWaterMelonAmountByFortune(ItemStack itemStack) {
        int enchantmentLevel = itemStack.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);

        switch (enchantmentLevel) {
            case 1:
                return RandomUtils.randomInt(3, 7);
            case 2:
                return RandomUtils.randomInt(3, 8);
            case 3:
                return RandomUtils.randomInt(3, 9);
            default:
                return RandomUtils.randomInt(3, 6);
        }
    }
}