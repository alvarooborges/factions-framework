package net.hyze.factions.framework.divinealtar.inventory;

import lombok.Getter;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.misc.utils.TimeCode;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.inventory.CustomInventory;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.divinealtar.altar.AltarProperties;
import net.hyze.factions.framework.divinealtar.power.Power;
import net.hyze.factions.framework.divinealtar.power.PowerInstance;
import net.hyze.factions.framework.divinealtar.power.PowerState;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;

public class AltarInventory extends CustomInventory {

    @Getter
    private final AltarProperties altarProperties;

    @Getter
    private final AltarBankInventory altarBankInventory;
    
    @Getter
    private final int factionId;

    public AltarInventory(int factionId, AltarProperties properties) {
        super(9 * 6, "Altar Divino");
        this.altarProperties = properties;
        this.altarBankInventory = new AltarBankInventory(this);
        this.factionId = factionId;

        update();
    }

    public void update() {

        BiConsumer<InventoryClickEvent, PowerInstance> consumer = (event, instance) -> {

            Power power = instance.getPower();

            if (power.getPowerState(this).equals(PowerState.AVAILABLE)) {

                if (power.getId().equals(PowerInstance.ELECTROMAGNETIC_POWER.getPower().getId())) {
                    Message.ERROR.send((Player) event.getWhoClicked(), "Ops, este poder está desativado neste momento.");
                    return;
                }

                User user = CoreProvider.Cache.Local.USERS.provide().get(event.getWhoClicked().getName());

                switch (power.getCurrency()) {

                    case CASH:
                        if (user.getRealCash() < power.getPrice()) {
                            Message.ERROR.send(Bukkit.getPlayerExact(user.getNick()), "Ops, você não tem Cash suficiente para ativar este poder.");
                            return;
                        }

                        user.decrementCash(power.getPrice());
                        break;

                    case COINS:
                        /**
                         *
                         */

                        break;

                    case GEM:
                        this.altarProperties.subtractBalance(instance, 1);
                        break;

                }

                power.onActivate(user);

                this.altarProperties.getActivePowers().put(
                        instance,
                        System.currentTimeMillis() + instance.getPower().activeTime()
                );

                FactionsProvider.Repositories.ALTAR.provide().update(this);

                update();
                this.altarBankInventory.update();
            }

        };

        setItem(12, buildIcon(PowerInstance.THUNDERSTORM), event -> consumer.accept(event, PowerInstance.THUNDERSTORM));
        setItem(14, buildIcon(PowerInstance.PROSPERITY), event -> consumer.accept(event, PowerInstance.PROSPERITY));
        setItem(20, buildIcon(PowerInstance.DIVINE_PROTECTION), event -> consumer.accept(event, PowerInstance.DIVINE_PROTECTION));
        setItem(22, buildIcon(PowerInstance.METEOR_RAIN), event -> consumer.accept(event, PowerInstance.METEOR_RAIN));
        setItem(24, buildIcon(PowerInstance.ELECTROMAGNETIC_POWER), event -> consumer.accept(event, PowerInstance.ELECTROMAGNETIC_POWER));

        /**
         * Depósito.
         */
        ItemBuilder bank = ItemBuilder.of(Material.CHEST)
                .name("&5Depósitos de Gemas")
                .lore(
                        "&7Deposite as suas Gemas para",
                        "&7conseguir invocar os poderes",
                        "&7divinos."
                );

        if (!this.altarProperties.getGems().isEmpty()) {

            bank.lore("", "&fGemas depositadas:");

            this.altarProperties.getGems().forEach((power, bald) -> {
                if (bald > 0) {
                    bank.lore(String.format("&8 ▪&7 %sx %s", bald, power.getPower().getGemName()));
                }
            });

        }

        bank.lore("", "&aClique para abrir.");

        setItem(
                40,
                bank.make(),
                event -> {
                    event.getWhoClicked().openInventory(this.altarBankInventory);
                }
        );

    }

    private ItemStack buildIcon(PowerInstance instance) {
        Power power = instance.getPower();

        ItemBuilder item = power.getIcon().clone()
                .name(ChatColor.AQUA + power.getName())
                .lore(power.getDescription());

        item.lore(
                "",
                String.format("&fTempo de recarga: &7%s", TimeCode.toText(power.rechargeTime(), 5)),
                String.format("&fDuração: &7%s", TimeCode.toText(power.activeTime(), 5)),
                String.format("&fCusto: &6%s", power.getCurrency().getColor() + power.getCurrency().format(power.getPrice())),
                ""
        );

        switch (power.getPowerState(this)) {

            case ACTIVE:
                item.lore(
                        "&aEste efeito está ativado pelos",
                        String.format(
                                "&apróximos %s.",
                                TimeCode.toText(
                                        this.altarProperties.getActivePowers().get(instance) - System.currentTimeMillis(),
                                        5
                                )
                        )
                );
                break;

            case IN_COOLDOWN:
                item.lore(
                        "&cEste poder estará disponível",
                        String.format(
                                "&cem %s.",
                                TimeCode.toText(
                                        this.altarProperties.getActiveCooldowns().get(instance) - System.currentTimeMillis(),
                                        5
                                )
                        )
                );
                break;

            case AVAILABLE:
                item.lore("&aClique para ativar.");
                break;

            case WITHOUT_BALANCE:
                item.lore("&cEste altar não possui gemas", "&csuficientes para ativar este poder.");
                break;

        }

        return item.make();
    }

}
