package net.hyze.factions.framework.ranking.factions;

import com.google.common.collect.ImmutableList;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.economy.Currency;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.ranking.DailyTycoonItem;
import net.hyze.factions.framework.misc.utils.FactionUtils;
import net.hyze.factions.framework.ranking.FactionRankIcon;
import net.hyze.factions.framework.ranking.Ranking;
import net.hyze.factions.framework.ranking.RankingFactory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class DailyTycoonRanking implements Ranking<Double> {

    private List<FactionRankIcon<Double>> icons = Collections.synchronizedList(new LinkedList<>());

    @Override
    public String getName() {
        return "Magnata Diário";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(Material.GOLD_BLOCK)
                .lore("&7Veja as facções que mais")
                .lore("&7ganharam moedas nas últimas")
                .lore("&724 horas!")
                .make();
    }

    @Override
    public List<FactionRankIcon<Double>> getItems() {
        return ImmutableList.copyOf(icons);
    }

    @Override
    public void initialize() {
        //   Printer.INFO.coloredPrint("&cInitialize " + this.getClass().getSimpleName());

        RankingFactory.RANKING_EXECUTOR_SERVICE.scheduleWithFixedDelay(() -> {
            try {
                //  Debug debug = new Debug(this.getClass().getSimpleName(), 1000);

                List<DailyTycoonItem> items = FactionsProvider.Repositories.FACTIONS_RANKING.provide().fetchSellRanking(50);

                AtomicInteger index = new AtomicInteger();

                List<FactionRankIcon<Double>> icons = items.stream()
                        .map(item -> {
                            int currentIndex = index.getAndIncrement();
                            ItemBuilder builder = FactionUtils.getBanner(item.getFaction())
                                    .name(String.format("&a%sº: &a%s", currentIndex + 1, item.getFaction().getStrippedDisplayName()))
                                    .lore(String.format(
                                            "&8 \u25AA &fMoedas nas últimas 24 horas: &e%s",
                                            Currency.COINS.format(item.getValue())
                                    ));

                            return new FactionRankIcon<>(item.getFaction(), item.getValue(), builder.make());
                        })
                        .collect(Collectors.toCollection(LinkedList::new));


                DailyTycoonRanking.this.icons.clear();
                DailyTycoonRanking.this.icons.addAll(icons);

                //   debug.done();
            } catch (Exception | Error e) {
                e.printStackTrace();
            }
        }, 5, 30, TimeUnit.SECONDS);
    }

}
