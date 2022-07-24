package net.hyze.factions.framework.ranking.factions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.economy.Currency;
import net.hyze.factions.framework.FactionsPlugin;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.misc.npc.impl.ranking.RankingNPC;
import net.hyze.factions.framework.misc.npc.impl.ranking.ValuationRankingNPC;
import net.hyze.factions.framework.misc.utils.FactionUtils;
import net.hyze.factions.framework.ranking.FactionRankIcon;
import net.hyze.factions.framework.ranking.Ranking;
import net.hyze.factions.framework.ranking.RankingFactory;
import net.hyze.factions.framework.settings.map.MapSettings;
import net.hyze.factions.framework.spawners.SpawnerType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ValuationRanking implements Ranking<ValuationRanking.RankValue> {

    private List<FactionRankIcon<RankValue>> icons = Collections.synchronizedList(new LinkedList<>());
    private List<FactionRankIcon<RankValue>> iconsCache = Collections.synchronizedList(new LinkedList<>());
    private boolean updating = false;

    @Override
    public String getName() {
        return "Valor";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(Material.GOLD_INGOT)
                .lore("&7Veja as facções mais valiosas")
                .lore("&7do servidor!")
                .lore("&7Coins + Geradores")
                .make();
    }

    @Override
    public List<FactionRankIcon<RankValue>> getItems() {
        if (updating) {
            return ImmutableList.copyOf(iconsCache);
        }

        return ImmutableList.copyOf(icons);
    }

    @Override
    public void initialize() {
        //    Printer.INFO.coloredPrint("&cInitialize " + this.getClass().getSimpleName());

        RankingFactory.RANKING_EXECUTOR_SERVICE.scheduleWithFixedDelay(() -> {
            try {
                iconsCache.clear();
                iconsCache.addAll(icons);

                updating = true;

                //     Debug debug = new Debug(this.getClass().getSimpleName(), 1000);

                List<FactionRankIcon<Double>> coins = RankingFactory.FACTIONS_COINS_RANKING.getRanking().getItems();

                Map<Faction, RankValue> map = Maps.newHashMap();

                for (FactionRankIcon<Double> icon : coins) {
                    map.put(icon.getFaction(), new RankValue(icon.getElement(), 0D));
                }

                List<Faction> factions = FactionsProvider.Cache.Local.FACTIONS.provide().get();

                for (Faction faction : factions) {

                    Map<SpawnerType, Integer> collected = FactionsProvider.Repositories.SPAWNERS.provide().countCollected(faction);
                    Map<SpawnerType, Integer> placed = FactionsProvider.Repositories.SPAWNERS.provide().countPlaced(faction);

                    Map<SpawnerType, Integer> total = Maps.newHashMap();

                    total.putAll(collected);

                    for (Map.Entry<SpawnerType, Integer> entry : placed.entrySet()) {
                        total.put(entry.getKey(), entry.getValue() + total.getOrDefault(entry.getKey(), 0));
                    }

                    RankValue value = map.getOrDefault(faction, new RankValue(0D, 0D));

                    SpawnersRanking.RankValue spawnersRankValue = new SpawnersRanking.RankValue(null, null, total);

                    value.setSpawnersValue(spawnersRankValue.calculateValue());

                    map.put(faction, value);
                }

                List<FactionRankIcon<RankValue>> icons = Lists.newArrayList();

                AtomicInteger targetPos = new AtomicInteger(1);

                map.entrySet().stream()
                        .sorted((o1, o2) -> {
                            int i = Double.compare(o2.getValue().sum(), o1.getValue().sum());

                            if (i == 0) {
                                return o2.getKey().getCreatedAt().compareTo(o1.getKey().getCreatedAt());
                            }

                            return i;
                        })
                        .forEach(entry -> {

                            ItemBuilder builder = FactionUtils.getBanner(entry.getKey())
                                    .name(String.format("&a%sº: %s", targetPos.getAndIncrement(), entry.getKey().getStrippedDisplayName()));

                            builder
                                    .lore(String.format(
                                            "&fValor Total: &e%s",
                                            Currency.COINS.format(entry.getValue().sum())
                                    ), "")
                                    .lore(String.format(
                                            "&8 \u25AA &fValor em Moedas: &7%s",
                                            Currency.COINS.format(entry.getValue().getCoinsValue())
                                    ))
                                    .lore(String.format(
                                            "&8 \u25AA &fValor em Geradores: &7%s",
                                            Currency.COINS.format(entry.getValue().getSpawnersValue())
                                    ))
                                    .lore("");

                            icons.add(new FactionRankIcon<>(entry.getKey(), entry.getValue(), builder.make()));
                        });

                ValuationRanking.this.icons.clear();
                ValuationRanking.this.icons.addAll(icons);

                Bukkit.getScheduler().runTask(FactionsPlugin.getInstance(), () -> {
                    Optional.ofNullable(MapSettings.getInstance().getRankings())
                            .map(m -> m.get(ValuationRankingNPC.class))
                            .ifPresent(RankingNPC::updateNPCs);
                });

                //        debug.done();
            } catch (Exception | Error e) {
                e.printStackTrace();
            } finally {
                updating = false;
            }
        }, 5, 30, TimeUnit.SECONDS);
    }

    @Setter
    @Getter
    @AllArgsConstructor
    public static class RankValue {

        private Double coinsValue;
        private Double spawnersValue;

        public double sum() {
            return coinsValue + spawnersValue;
        }
    }
}
