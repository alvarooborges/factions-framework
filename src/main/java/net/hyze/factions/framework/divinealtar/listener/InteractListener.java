package net.hyze.factions.framework.divinealtar.listener;

import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.InventoryUtils;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.divinealtar.altar.AltarProperties;
import net.hyze.factions.framework.divinealtar.inventory.AltarInventory;
import net.hyze.factions.framework.divinealtar.manager.AltarManager;
import net.hyze.factions.framework.lands.Claim;
import net.hyze.factions.framework.misc.utils.LandUtils;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class InteractListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void on(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();

        if (!(event.hasBlock() && block.getType().equals(Material.ENDER_PORTAL_FRAME))) {
            return;
        }

        Claim claim = LandUtils.getClaim(block.getLocation());
        
        if(claim.getFactionId() == null){
            return;
        }

        AltarInventory inventory = FactionsProvider.Cache.Local.ALTAR.provide().get(claim.getFactionId());

        if (inventory == null) {
            inventory = new AltarInventory(claim.getFactionId(), new AltarProperties());
            FactionsProvider.Cache.Local.ALTAR.provide().put(claim.getFactionId(), inventory);
        }

        ItemStack handItem = event.getPlayer().getItemInHand();

        if (handItem != null && handItem.getType().equals(Material.DIAMOND_PICKAXE) && handItem.getEnchantments().containsKey(Enchantment.SILK_TOUCH)) {

            ItemStack item = AltarManager.buildAltarItem();

            Player player = event.getPlayer();

            if (!InventoryUtils.fits(player.getInventory(), item)) {
                Message.ERROR.send(player, "Seu inventário está cheio.");
                return;
            }

            try {
                inventory.getAltarBankInventory().getViewers().forEach(entity -> entity.closeInventory());
                inventory.getViewers().forEach(entity -> entity.closeInventory());
            } catch (Exception e) {
            }

            block.setType(Material.AIR);
            block.getWorld().playSound(block.getLocation(), Sound.ZOMBIE_WOODBREAK, 5, 0);
            block.getWorld().spigot().playEffect(block.getLocation().add(.5, .5, .5), Effect.SMOKE);

            player.getInventory().addItem(item);
            player.updateInventory();
            return;
        }

        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        event.getPlayer().openInventory(inventory);

        event.setCancelled(true);

    }

}
