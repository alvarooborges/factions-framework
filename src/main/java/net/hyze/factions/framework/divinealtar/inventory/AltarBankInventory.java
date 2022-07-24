package net.hyze.factions.framework.divinealtar.inventory;

import lombok.Getter;
import net.hyze.core.spigot.inventory.CustomInventory;
import net.hyze.core.spigot.misc.customitem.CustomItemRegistry;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.divinealtar.misc.customitems.GemItem;
import net.hyze.factions.framework.divinealtar.power.Power;
import net.hyze.factions.framework.divinealtar.power.PowerInstance;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;

public class AltarBankInventory extends CustomInventory {

    @Getter
    private final AltarInventory backInventory;

    public AltarBankInventory(AltarInventory backInventory) {
        super(9 * 4, "Depósito de Gemas");
        this.backInventory = backInventory;

        update();

        backItem(backInventory);
    }

    public void update() {

        BiConsumer<InventoryClickEvent, PowerInstance> consumer = (event, power) -> {
            
            int amount = this.backInventory.getAltarProperties().getBalance(power);

            if (amount < 1) {
                return;
            }

            if (event.getAction().equals(InventoryAction.PICKUP_ALL)) {
                this.backInventory.getAltarProperties().subtractBalance(power, 1);
                this.backInventory.update();
                this.update();
                FactionsProvider.Repositories.ALTAR.provide().update(this.backInventory);

                Player player = (Player) event.getWhoClicked();
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                player.getInventory().addItem(CustomItemRegistry.getItem(GemItem.BUILD_KEY.apply(power)).asItemStack());

                return;
            }

            if (event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                this.backInventory.getAltarProperties().subtractBalance(power, amount);
                this.backInventory.update();
                this.update();
                FactionsProvider.Repositories.ALTAR.provide().update(this.backInventory);

                Player player = (Player) event.getWhoClicked();
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                player.getInventory().addItem(CustomItemRegistry.getItem(GemItem.BUILD_KEY.apply(power)).asItemStack(amount));
            }

        };

        setItem(
                10,
                buildIcon(PowerInstance.DIVINE_PROTECTION),
                event -> consumer.accept(event, PowerInstance.DIVINE_PROTECTION)
        );

        setItem(
                12,
                buildIcon(PowerInstance.ELECTROMAGNETIC_POWER),
                event -> consumer.accept(event, PowerInstance.ELECTROMAGNETIC_POWER)
        );
        
        setItem(
                14,
                buildIcon(PowerInstance.METEOR_RAIN),
                event -> consumer.accept(event, PowerInstance.METEOR_RAIN)
        );
        
        setItem(
                16,
                buildIcon(PowerInstance.THUNDERSTORM),
                event -> consumer.accept(event, PowerInstance.THUNDERSTORM)
        );

    }

    private ItemStack buildIcon(PowerInstance instance) {
        Power power = instance.getPower();

        int amount = this.backInventory.getAltarProperties().getBalance(instance);

        ItemBuilder item = ItemBuilder.of(Material.EYE_OF_ENDER)
                .clone()
                .name(ChatColor.AQUA + "" + amount + "x " + power.getName());

        item.lore(
                "&eClique para retirar uma gema.",
                "&eClique segurando shift para retirar tudo."
        );

        item.amount(amount > 0 ? 1 : 0);

        return item.make();
    }

}
