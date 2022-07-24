package net.hyze.factions.framework.misc.economy;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.spigot.misc.economy.CoreEconomy;
import net.hyze.economy.Currency;
import net.hyze.economy.EconomyAPI;

public class FactionsEconomy implements CoreEconomy {

    @Override
    public Double get(int userId) {
        return EconomyAPI.get(CoreProvider.Cache.Local.USERS.provide().get(userId), Currency.COINS);
    }

    @Override
    public void add(int userId, double value) {
        EconomyAPI.add(CoreProvider.Cache.Local.USERS.provide().get(userId), Currency.COINS, value);
    }

    @Override
    public void remove(int userId, double value) {
        EconomyAPI.remove(CoreProvider.Cache.Local.USERS.provide().get(userId), Currency.COINS, value);
    }

    @Override
    public String format(double value) {
        return Currency.COINS.format(value);
    }

}
