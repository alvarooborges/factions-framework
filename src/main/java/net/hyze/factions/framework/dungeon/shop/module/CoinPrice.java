package net.hyze.factions.framework.dungeon.shop.module;


import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.misc.shop.module.AbstractModule;
import net.hyze.core.spigot.misc.shop.module.currency.AbstractPrice;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.economy.Currency;
import net.hyze.economy.EconomyAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;

@RequiredArgsConstructor
public class CoinPrice extends AbstractPrice {

    private final int price;

    @Override
    public AbstractModule.State state(User user) {
        if (EconomyAPI.get(user, Currency.COINS) < this.price) {
            return AbstractModule.State.ERROR;
        }

        return AbstractModule.State.SUCCESS;
    }

    @Override
    public String format() {
        return ChatColor.YELLOW.toString() + this.price + " coins";
    }

    @Override
    public ItemBuilder buildIcon(User user) {
        ItemBuilder icon = ItemBuilder.of(Material.IRON_INGOT);

        icon.name((this.state(user) == AbstractModule.State.SUCCESS ? "&a" : "&c") + this.getName());

        if (this.state(user) == AbstractModule.State.SUCCESS) {
            icon.lore("&7Você gastará " + this.format() + "&7.");
        } else {
            icon.lore("Você não possui moedas")
                    .lore("suficientes para efetuar")
                    .lore("esta compra.");
        }

        return icon;
    }

    @Override
    public String getName() {
        return "Moedas";
    }

    @Override
    public boolean transaction(User user) {
        Double coins = EconomyAPI.get(user, Currency.COINS);

        if (coins < this.price) {
            return false;
        }

        EconomyAPI.remove(user, Currency.COINS, (double) this.price);
        return true;
    }

}
