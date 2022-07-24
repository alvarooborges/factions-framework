package net.hyze.factions.framework.ranking.factions.coins;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.economy.Currency;
import net.hyze.economy.EconomyProvider;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.misc.utils.FactionUtils;
import net.hyze.factions.framework.ranking.FactionRankIcon;
import net.hyze.factions.framework.ranking.Ranking;
import net.hyze.factions.framework.ranking.RankingFactory;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CoinsRanking implements Ranking<Double> {

    private List<FactionRankIcon<Double>> icons = Collections.synchronizedList(new LinkedList<>());

    @Override
    public String getName() {
        return "Moedas";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(Material.DIAMOND)
                .lore("&7Veja as facções mais ricas")
                .lore("&7do servidor!")
                .make();
    }

    @Override
    public List<FactionRankIcon<Double>> getItems() {
        return ImmutableList.copyOf(icons);
    }

    @Override
    public void initialize() {

       //Printer.INFO.coloredPrint("&cInitialize " + this.getClass().getSimpleName());

        RankingFactory.RANKING_EXECUTOR_SERVICE.scheduleWithFixedDelay(() -> {
            try {
                //   Debug debug = new Debug(this.getClass().getSimpleName(), 1000);

                List<FactionRankIcon<Double>> icons = new LinkedList<>();

                Map<Integer, Double> rankMap = FactionsProvider.Repositories.FACTIONS_RELATIONS.provide().query(new FetchCurrencyRankSpec(Currency.COINS));

                Map<Faction, Double> rank = Maps.newLinkedHashMap();

                for (Map.Entry<Integer, Double> entry : rankMap.entrySet()) {
                    Faction faction = FactionsProvider.Cache.Local.FACTIONS.provide().getIfPresent(entry.getKey());

                    if (faction == null) {
                        continue;
                    }

                    rank.put(faction, entry.getValue());
                }

                SetMultimap<Faction, FactionUserRelation> relationsMap = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getByFactions(rank.keySet());

                Set<Integer> users = Sets.newHashSet();

                for (Map.Entry<Faction, Collection<FactionUserRelation>> entry : relationsMap.asMap().entrySet()) {
                    users.addAll(entry.getValue().stream().map(FactionUserRelation::getUserId).collect(Collectors.toList()));
                }

                Map<Integer, Double> currencyMap = EconomyProvider.Repositories.ECONOMY.provide().get(Currency.COINS, users);

                AtomicInteger index = new AtomicInteger();

                for (Map.Entry<Faction, Double> entry : rank.entrySet()) {
                    int currentIndex = index.getAndIncrement();

                    final Faction faction = entry.getKey();
                    final Double value = entry.getValue();

                    ItemBuilder builder = FactionUtils.getBanner(faction)
                            .name(String.format("&a%sº: &a%s", currentIndex + 1, faction.getStrippedDisplayName()))
                            .lore("");

                    Set<FactionUserRelation> relations = relationsMap.get(faction);

                    Map<FactionUser, Double> usersCurrency = Maps.newHashMap();

                    for (FactionUserRelation relation : relations) {
                        usersCurrency.put(relation.getUser(), currencyMap.getOrDefault(relation.getUserId(), 0d));
                    }

                    Map<FactionUser, Double> sorted = usersCurrency
                            .entrySet()
                            .stream()
                            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));

                    AtomicInteger targetPos = new AtomicInteger(1);

                    sorted.forEach((target, currencyValue) -> {
                        builder.lore(String.format(
                                "&f%s. &7%s: &7%s",
                                targetPos.getAndIncrement(),
                                target.getHandle().getHighestGroup().getDisplayTag(target.getNick()),
                                Currency.COINS.format(currencyValue)
                        ));
                    });

                    builder.lore("").lore(String.format(
                            "&fTotal de Moedas: &e%s",
                            Currency.COINS.format(value)
                    ));

                    FactionRankIcon<Double> icon = new FactionRankIcon<>(faction, value, builder.make());

                    icons.add(icon);
                }

                CoinsRanking.this.icons.clear();
                CoinsRanking.this.icons.addAll(icons);

                //  debug.done();
            } catch (Exception | Error e) {
                e.printStackTrace();
            }
        }, 5, 30, TimeUnit.SECONDS);
    }
}
