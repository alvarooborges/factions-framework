package net.hyze.factions.framework.ranking.factions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
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

public class KDRRanking implements Ranking<KDRRanking.RankValue> {

    private List<FactionRankIcon<RankValue>> icons = Collections.synchronizedList(new LinkedList<>());

    @Override
    public String getName() {
        return "KDR";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(Material.DIAMOND_SWORD)
                .lore("&7Veja as facções com o KDR")
                .lore("&7mais alto do servidor!")
                .make();
    }

    @Override
    public List<FactionRankIcon<RankValue>> getItems() {
        return ImmutableList.copyOf(icons);
    }

    @Override
    public void initialize() {
        //  Printer.INFO.coloredPrint("&cInitialize " + this.getClass().getSimpleName());

        RankingFactory.RANKING_EXECUTOR_SERVICE.scheduleWithFixedDelay(() -> {
            try {
                //     Debug debug = new Debug(this.getClass().getSimpleName(), 1000);

                ImmutableMultimap<Faction, FactionUserRelation> all = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().all();

                AtomicInteger index = new AtomicInteger(1);

                List<FactionRankIcon<RankValue>> icons = Lists.newArrayList();

                all.asMap()
                        .entrySet()
                        .stream()
                        .map(entry -> {
                            Collection<FactionUserRelation> relations = entry.getValue();

                            int civilKills = 0;
                            int neutralKills = 0;

                            int civilDeaths = 0;
                            int neutralDeaths = 0;

                            for (FactionUserRelation relation : relations) {
                                FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(relation.getUserId());

                                if (user != null) {
                                    civilKills += user.getStats().getCivilKills();
                                    neutralKills += user.getStats().getNeutralKills();

                                    civilDeaths += user.getStats().getCivilDeaths();
                                    neutralDeaths += user.getStats().getNeutralDeaths();
                                }
                            }

                            RankValue value = new RankValue(
                                    civilKills, neutralKills,
                                    civilDeaths, neutralDeaths
                            );

                            if (value.calculateKDR() == 0) {
                                return null;
                            }

                            return new AbstractMap.SimpleEntry<>(entry.getKey(), value);

                        })
                        .filter(Objects::nonNull)
                        .sorted((o1, o2) -> {
                            int i = Double.compare(o2.getValue().calculateKDR(), o1.getValue().calculateKDR());

                            if (i == 0) {
                                return o2.getKey().getCreatedAt().compareTo(o1.getKey().getCreatedAt());
                            }

                            return i;
                        })
                        .forEach(entry -> {

                            ItemBuilder builder = FactionUtils.getBanner(entry.getKey())
                                    .name(String.format("&a%sº: %s", index.getAndIncrement(), entry.getKey().getStrippedDisplayName()))
                                    .lore("&fKDR: &7" + FactionUtils.formatKDR(entry.getValue().calculateKDR()))
                                    .lore("")
                                    .lore(
                                            "&a▲ Abates:",
                                            "  &fCivil: &7" + entry.getValue().civilKills,
                                            "  &fNeutro: &7" + entry.getValue().neutralKills,
                                            "  &fTotal: &7" + entry.getValue().totalKills()
                                    )
                                    .lore("")
                                    .lore(
                                            "&c▼ Mortes:",
                                            "  &fCivil: &7" + entry.getValue().civilDeaths,
                                            "  &fNeutro: &7" + entry.getValue().neutralDeaths,
                                            "  &fTotal: &7" + entry.getValue().totalDeaths()
                                    );

                            icons.add(new FactionRankIcon<>(entry.getKey(), entry.getValue(), builder.make()));
                        });

                KDRRanking.this.icons.clear();
                KDRRanking.this.icons.addAll(icons);

                //     debug.done();
            } catch (Exception | Error e) {
                e.printStackTrace();
            }
        }, 5, 30, TimeUnit.SECONDS);
    }


    @Getter
    @RequiredArgsConstructor
    public static class RankValue {

        private final int civilKills;
        private final int neutralKills;

        private final int civilDeaths;
        private final int neutralDeaths;

        public int totalKills() {
            return civilKills + neutralKills;
        }

        public int totalDeaths() {
            return civilDeaths + neutralDeaths;
        }

        public double calculateKDR() {
            if (totalKills() == 0 && totalDeaths() == 0) {
                return 0;
            }

            if (totalKills() == totalDeaths()) {
                return 1;
            }

            if (totalDeaths() == 0) {
                return totalKills();
            }

            return totalKills() / totalDeaths();
        }
    }

}
