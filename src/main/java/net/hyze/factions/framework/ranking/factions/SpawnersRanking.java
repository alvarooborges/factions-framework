package net.hyze.factions.framework.ranking.factions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.google.common.util.concurrent.AtomicDouble;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.economy.Currency;
import net.hyze.factions.framework.FactionsPlugin;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.misc.npc.impl.ranking.RankingNPC;
import net.hyze.factions.framework.misc.npc.impl.ranking.SpawnersRankingNPC;
import net.hyze.factions.framework.misc.utils.FactionUtils;
import net.hyze.factions.framework.ranking.FactionRankIcon;
import net.hyze.factions.framework.ranking.Ranking;
import net.hyze.factions.framework.ranking.RankingFactory;
import net.hyze.factions.framework.settings.map.MapSettings;
import net.hyze.factions.framework.spawners.SpawnerType;
import net.hyze.signshop.SignShop;
import net.hyze.signshop.SignShopProvider;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SpawnersRanking implements Ranking<SpawnersRanking.RankValue> {

    private List<FactionRankIcon<SpawnersRanking.RankValue>> icons = Collections.synchronizedList(new LinkedList<>());

    @Override
    public String getName() {
        return "Geradores";
    }

    @Override
    public ItemStack getIcon() {
        return ItemBuilder.of(Material.MOB_SPAWNER)
                .lore("&7Veja as facções que mais possuem")
                .lore("&7geradores colocados no momento!")
                .make();
    }

    @Override
    public List<FactionRankIcon<SpawnersRanking.RankValue>> getItems() {
        return ImmutableList.copyOf(icons);
    }

    @Override
    public void initialize() {
        //   Printer.INFO.coloredPrint("&cInitialize " + this.getClass().getSimpleName());

        RankingFactory.RANKING_EXECUTOR_SERVICE.scheduleWithFixedDelay(() -> {
            try {
                //       Debug debug = new Debug(this.getClass().getSimpleName(), 1000);

                Map<Faction, RankValue> map = Maps.newLinkedHashMap();

                List<Faction> factions = FactionsProvider.Cache.Local.FACTIONS.provide().get();

                for (Faction faction : factions) {

                    Map<SpawnerType, Integer> placed = FactionsProvider.Repositories.SPAWNERS.provide().countPlaced(faction);
                    Map<SpawnerType, Integer> stored = FactionsProvider.Repositories.SPAWNERS.provide().countCollected(faction);

                    Map<SpawnerType, Integer> total = Maps.newEnumMap(SpawnerType.class);

                    for (Map.Entry<SpawnerType, Integer> entry : placed.entrySet()) {
                        // total.put(entry.getKey(), entry.getValue() + total.getOrDefault(entry.getKey(), 0));

                        total.merge(entry.getKey(), entry.getValue(), Integer::sum);
                    }

                    for (Map.Entry<SpawnerType, Integer> entry : stored.entrySet()) {
                        total.merge(entry.getKey(), entry.getValue(), Integer::sum);
                    }

                    map.put(faction, new RankValue(placed, stored, total));
                }

                List<FactionRankIcon<SpawnersRanking.RankValue>> icons = Lists.newArrayList();

                AtomicInteger index = new AtomicInteger();

                map.entrySet()
                        .stream()
                        .sorted((o1, o2) -> {
                            int i = Double.compare(o2.getValue().calculateValue(), o1.getValue().calculateValue());

                            if (i == 0) {
                                return o2.getKey().getCreatedAt().compareTo(o1.getKey().getCreatedAt());
                            }

                            return i;
                        })
                        .forEach(entry -> {
                            int currentIndex = index.getAndIncrement();
                            ItemBuilder builder = FactionUtils.getBanner(entry.getKey())
                                    .name(String.format("&a%sº: &a%s", currentIndex + 1, entry.getKey().getStrippedDisplayName()))
                                    .lore("&fGeradores colocados:");

                            RankValue value = entry.getValue();

                            if (value.getPlacedSpawners().isEmpty()) {
                                builder.lore("&8 \u25AA &7Esta facção não possui nenhum", "&7 Gerador colocado em suas terras.");
                            } else {

                                value.getPlacedSpawners()
                                        .entrySet()
                                        .stream()
                                        .sorted((e1, e2) -> Ints.compare(e2.getValue(), e1.getValue()))
                                        .forEach(e -> {
                                            builder.lore(String.format(
                                                    "&8 \u25AA &e%sx &7Geradores de %s",
                                                    e.getValue(),
                                                    e.getKey().getRawDisplayName()
                                            ));
                                        });

                            }

                            builder.lore("", "&fGeradores armazenados:");

                            if (value.getCollectedSpawners().isEmpty()) {
                                builder.lore("&8 \u25AA &7Esta facção não possui nenhum", " &7Gerador em seu armazém.");
                            } else {

                                value.getCollectedSpawners()
                                        .entrySet()
                                        .stream()
                                        .sorted((e1, e2) -> Ints.compare(e2.getValue(), e1.getValue()))
                                        .forEach(e -> {
                                            builder.lore(String.format(
                                                    "&8 \u25AA &e%sx &7Geradores de %s",
                                                    e.getValue(),
                                                    e.getKey().getRawDisplayName()
                                            ));
                                        });

                            }

                            builder.lore("")
                                    .lore(String.format(
                                            "&fValor dos Geradores: &b%s",
                                            Currency.COINS.format(value.calculateValue())
                                    ));

                            icons.add(new FactionRankIcon<>(entry.getKey(), value, builder.make()));
                        });

                SpawnersRanking.this.icons.clear();
                SpawnersRanking.this.icons.addAll(icons);

                Bukkit.getScheduler().runTask(FactionsPlugin.getInstance(), () -> {
                    Optional.ofNullable(MapSettings.getInstance().getRankings())
                            .map(m -> m.get(SpawnersRankingNPC.class))
                            .ifPresent(RankingNPC::updateNPCs);
                });

                //      debug.done();
            } catch (Exception | Error e) {
                e.printStackTrace();
            }
        }, 5, 30, TimeUnit.SECONDS);
    }

    @Getter
    @RequiredArgsConstructor
    public static class RankValue {

        private final Map<SpawnerType, Integer> placedSpawners;
        private final Map<SpawnerType, Integer> collectedSpawners;
        private final Map<SpawnerType, Integer> totalSpawners;

        public double calculateValue() {

            AtomicDouble total = new AtomicDouble();

            totalSpawners.forEach((type, amount) -> {

                Collection<SignShop> shops = SignShopProvider.Cache.Local.SHOPS.provide().get(type.getCustomItem().getKey());

                if (!shops.isEmpty()) {
                    shops.stream()
                            .filter(o1 -> o1.getBuyFromShop() != null)
                            .min((o1, o2) -> Doubles.compare(o1.getBuyFromShop(), o2.getBuyFromShop()))
                            .ifPresent(shop -> total.addAndGet(shop.getBuyFromShop() * amount));

                }
            });

            return total.get();
        }
    }
}
