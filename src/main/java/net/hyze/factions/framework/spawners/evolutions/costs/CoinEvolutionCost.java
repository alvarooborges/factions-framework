package net.hyze.factions.framework.spawners.evolutions.costs;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import net.hyze.economy.Currency;
import net.hyze.economy.EconomyAPI;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.spawners.evolutions.EvolutionCost;
import net.hyze.factions.framework.user.FactionUser;

import java.util.List;

@RequiredArgsConstructor
public class CoinEvolutionCost extends EvolutionCost {

    private final double price;

    @Override
    public boolean has(FactionUser user, Faction faction) {
        return EconomyAPI.get(user.getHandle(), Currency.COINS) >= price;
    }

    @Override
    public boolean transaction(FactionUser user, Faction faction) {
        return EconomyAPI.remove(user.getHandle(), Currency.COINS, price);
    }

    @Override
    public List<String> getDisplay(FactionUser user, Faction faction) {
        return Lists.newArrayList(Currency.COINS.format(price));
    }
}
