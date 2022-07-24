package net.hyze.factions.framework.misc.customitem.data;

import lombok.Getter;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.spigot.inventory.ConfirmInventory;
import net.hyze.core.spigot.misc.customitem.CustomItem;
import net.hyze.core.spigot.misc.customitem.INonStackable;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.InventoryUtils;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.echo.packets.UserAdditionalMaxPowerUpdatedPacket;
import net.hyze.factions.framework.user.FactionUser;
import net.hyze.factions.framework.user.stats.UserStats;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.greenrobot.eventbus.Subscribe;

public class MaxPowerItem extends CustomItem implements INonStackable {

    @Getter
    private final ItemBuilder itemBuilder;

    public MaxPowerItem() {
        super("max-power-item");

        int maxPower = FactionsProvider.getSettings().getMaxAdditionalMaxPower() + FactionsProvider.getSettings().getMaxPower();

        this.itemBuilder = ItemBuilder.of(Material.NETHER_STAR)
                .glowing(true)
                .name("&6+1 Poder Máximo")
                .lore("Ao utilizar este item você", "recebe 1 ponto adicional em", "seu Poder Máximo!")
                .lore("")
                .lore("&fLimite de Poder Máximo: &6" + maxPower);
    }

    @Override
    public String getDisplayName() {
        return "&6+1 Poder Máximo";
    }

    @Subscribe
    public void on(PlayerInteractEvent event) {
        event.setCancelled(true);

        Player player = event.getPlayer();
        ItemStack stack = player.getItemInHand();

        FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(player.getName());

        if (user.getStats().getAdditionalMaxPower() >= FactionsProvider.getSettings().getMaxAdditionalMaxPower()) {
            Message.ERROR.send(player, "Você já antingiu o poder máximo.");
            return;
        }

        ConfirmInventory confirmInventory = ConfirmInventory.of(event0 -> {
            FactionUser user0 = FactionsProvider.Cache.Local.USERS.provide().get(player.getName());

            if (user0.getStats().getAdditionalMaxPower() >= FactionsProvider.getSettings().getMaxAdditionalMaxPower()) {
                Message.ERROR.send(player, "Você já antingiu o poder máximo.");
                player.closeInventory();
                return;
            }

            boolean subtract = InventoryUtils.subtractOne(player, stack);

            if (!subtract) {
                Message.ERROR.send(player, "Algo de errado aconteceu.");
                player.closeInventory();
                return;
            }

            user0.getStats().increment(UserStats.Field.ADDITIONAL_MAX_POWER);

            FactionsProvider.Repositories.USER_STATS.provide().update(user0.getStats(), UserStats.Field.ADDITIONAL_MAX_POWER);

            int oldAdditionalMaxPower = user0.getStats().getAdditionalMaxPower() - 1;
            int newAdditionalMaxPower = user0.getStats().getAdditionalMaxPower();

            CoreProvider.Redis.ECHO.provide().publish(new UserAdditionalMaxPowerUpdatedPacket(
                    user0.getId(), oldAdditionalMaxPower, newAdditionalMaxPower
            ));

            Message.SUCCESS.send(player, String.format("Você aumentou seu poder máximo para %s.", newAdditionalMaxPower + FactionsProvider.getSettings().getMaxPower()));

        }, event1 -> {
        }, getItemBuilder().make());

        player.openInventory(confirmInventory.make());
    }
}
