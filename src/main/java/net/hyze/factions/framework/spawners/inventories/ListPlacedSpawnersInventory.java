package net.hyze.factions.framework.spawners.inventories;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import net.hyze.core.shared.cache.local.utils.CaffeineScheduler;
import net.hyze.core.shared.misc.cooldowns.UserCooldowns;
import net.hyze.core.spigot.inventory.PaginateInventory;
import net.hyze.core.spigot.misc.utils.BlockedWhenRestarting;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.FactionsSettings;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.permission.FactionPermission;
import net.hyze.factions.framework.spawners.Spawner;
import net.hyze.factions.framework.spawners.SpawnerType;
import net.hyze.factions.framework.spawners.SpawnerUtils;
import net.hyze.factions.framework.user.FactionUser;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ListPlacedSpawnersInventory extends PaginateInventory implements BlockedWhenRestarting {

    private static LoadingCache<Faction, List<Spawner>> CACHE = Caffeine.newBuilder()
            .scheduler(CaffeineScheduler.getInstance())
            .expireAfterWrite(5, TimeUnit.SECONDS)
            .build(faction -> {
                Multimap<SpawnerType, Spawner> spawners = FactionsProvider.Repositories.SPAWNERS.provide().fetchPlaced(faction);
                return Lists.newArrayList(spawners.values());
            });

    public ListPlacedSpawnersInventory(FactionUser user, Faction faction) {
        super(String.format("Geradores Colocados [%s]", faction.getTag()));

        List<Spawner> spawners = CACHE.get(faction);

        spawners.stream().collect(Collectors.groupingBy(Spawner::getType))
                .forEach((type, spawnersOfType) -> {
                    int unlockedCount = 0;
                    int lockedCount = 0;
                    long totalLocketTime = 0;

                    for (Spawner spawner : spawnersOfType) {
                        if (SpawnerUtils.hasEndedBreakCooldown(faction, spawner)) {
                            unlockedCount++;
                        } else {
                            lockedCount++;
                            totalLocketTime += SpawnerUtils.getBreakCooldownLeft(faction, spawner);
                        }
                    }

                    ItemBuilder icon = ItemBuilder.of(type.getIcon().getHead());

                    if (FactionsProvider.getSettings().getSpawnerMode() == FactionsSettings.SpawnerMode.UNDER_ATTACK) {
                        icon.name(String.format(
                                "&e%sx %s",
                                spawnersOfType.size(),
                                type.getDisplayName()
                        ));
                    } else {
                        icon.name("&eGeradores de " + type.getRawDisplayName())
                                .lore("&fGeradores liberados: &7" + unlockedCount)
                                .lore("&fGeradores bloqueados: &7" + lockedCount);

                        if (lockedCount > 0) {
                            icon.lore("&fTempo médio para liberação: &7" + UserCooldowns.getFormattedTimeLeft(totalLocketTime / lockedCount));
                        }

                        icon.lore("", "&eClique para ver mais detalhes.");
                    }

                    PaginateInventory.PaginateInventoryBuilder paginateInventory = PaginateInventory.builder();

                    spawnersOfType.stream()
                            .sorted((o1, o2) -> {
                                return o2.getTransactedAt().compareTo(o1.getTransactedAt());
                            })
                            .forEach(spawner -> {
                                ItemBuilder builder = ItemBuilder.of(spawner.getType().getIcon().getHead());

                                builder.clearLores()
                                        .name(spawner.getType().getDisplayName());

                                if (FactionPermission.COMMAND_BASE.allows(faction, user)) {
                                    builder.lore("&fx: &7" + spawner.getLocation().getX());
                                    builder.lore("&fy: &7" + spawner.getLocation().getY());
                                    builder.lore("&fz: &7" + spawner.getLocation().getZ());
                                }

                                if (SpawnerUtils.hasEndedBreakCooldown(faction, spawner)) {
                                    builder.lore("", "&aLiberado.");
                                } else {
                                    builder.lore("", "&fTempo para liberação: &7" + UserCooldowns.getFormattedTimeLeft(
                                            SpawnerUtils.getBreakCooldownLeft(faction, spawner)
                                    ));
                                }

                                paginateInventory.item(builder.make(), null);
                            });

                    ListPlacedSpawnersInventory.this.addItem(icon.make(), () -> {
                        if (FactionsProvider.getSettings().getSpawnerMode() == FactionsSettings.SpawnerMode.BREAK_COOLDOWN) {
                            user.getPlayer().openInventory(paginateInventory.build(
                                    String.format("Geradores Colocados [%s]", faction.getTag())
                            ));
                        }
                    });
                });
    }
}
