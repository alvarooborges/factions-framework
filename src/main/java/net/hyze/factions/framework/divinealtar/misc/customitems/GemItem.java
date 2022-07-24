package net.hyze.factions.framework.divinealtar.misc.customitems;

import net.hyze.core.spigot.misc.customitem.CustomItem;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.InventoryUtils;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.divinealtar.inventory.AltarInventory;
import net.hyze.factions.framework.divinealtar.power.PowerInstance;
import net.hyze.factions.framework.lands.Claim;
import net.hyze.factions.framework.misc.utils.LandUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;
import org.greenrobot.eventbus.Subscribe;

import java.util.function.Function;

public class GemItem extends CustomItem {

    public static Function<PowerInstance, String> BUILD_KEY = powerInstance -> "divine_altar_gem_" + powerInstance.getPower().getId();

    private final PowerInstance powerInstance;

    public GemItem(PowerInstance powerInstance) {
        super(BUILD_KEY.apply(powerInstance));

        this.powerInstance = powerInstance;
    }

    @Override
    public ItemBuilder getItemBuilder() {
        return ItemBuilder.of(Material.EYE_OF_ENDER)
                .name(getDisplayName())
                .lore(
                        "&7Deposite esta gema em um",
                        "&7Altar Divino para invocar o",
                        String.format("&7poder %s.", this.powerInstance.getPower().getName())
                );
    }

    @Override
    public String getDisplayName() {
        return ChatColor.AQUA + this.powerInstance.getPower().getGemName();
    }

    @Subscribe
    public void on(PlayerInteractEvent event) {

        if (event.isCancelled() || !event.hasBlock()) {
            return;
        }

        Block block = event.getClickedBlock();

        if (!(event.getPlayer().isSneaking() && block.getType().equals(Material.ENDER_PORTAL_FRAME))) {
            return;
        }

        Claim claim = LandUtils.getClaim(block.getLocation());
        
        if(claim == null || claim.getFactionId() == null){
            Message.ERROR.send(event.getPlayer(), "Você só pode acessar o seu Altar dentro de terras de sua Facção.");
            return;
        }

        AltarInventory inventory = FactionsProvider.Cache.Local.ALTAR.provide().get(claim.getFactionId());

        if (inventory == null) {
            event.setCancelled(true);
            Message.ERROR.send(event.getPlayer(), "Ops, algo de errado aconteceu!");
            return;
        }

        inventory.getAltarProperties().addBalance(this.powerInstance, 1);
        inventory.update();
        inventory.getAltarBankInventory().update();

        InventoryUtils.subtractOneOnHand(event.getPlayer());

        FactionsProvider.Repositories.ALTAR.provide().update(inventory);

        Message.SUCCESS.send(event.getPlayer(), "Yay, Gema adicionada com sucesso!");

        event.setCancelled(true);

    }

}
