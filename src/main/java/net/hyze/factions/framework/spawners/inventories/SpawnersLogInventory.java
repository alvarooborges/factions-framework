package net.hyze.factions.framework.spawners.inventories;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.misc.cooldowns.UserCooldowns;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.inventory.PaginateInventory;
import net.hyze.core.spigot.misc.utils.BlockedWhenRestarting;
import net.hyze.core.spigot.misc.utils.HeadTexture;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.spawners.log.LogSourceType;
import net.hyze.factions.framework.spawners.log.SpawnerLog;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.Material;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SpawnersLogInventory extends PaginateInventory implements BlockedWhenRestarting {

    public SpawnersLogInventory(FactionUser user, Faction faction) {
        super(String.format("Histórico [%s]", faction.getTag()));

        Map<Key, List<SpawnerLog>> map = FactionsProvider.Cache.Local.SPAWNERS_LOG.provide().get(faction).stream()
                .sorted((o1, o2) -> o2.getDate().compareTo(o1.getDate()))
                .collect(Collectors.groupingBy(log -> new Key(log.getType(), log.getTypeValue(), log.getDate())))
                .entrySet()
                .stream()
                .sorted((o1, o2) -> {
                    int compare = o2.getKey().date.compareTo(o1.getKey().date);

                    if (compare == 0) {
                        int o1MinId = o1.getValue().stream().mapToInt(SpawnerLog::getId).min().orElse(0);
                        int o2MinId = o2.getValue().stream().mapToInt(SpawnerLog::getId).min().orElse(0);

                        return Integer.compare(o2MinId, o1MinId);
                    }

                    return compare;
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        for (Map.Entry<Key, List<SpawnerLog>> entry : map.entrySet()) {
            addItem(buildIcon(entry.getKey(), entry.getValue()).make());
        }

        backOrCloseItem();
    }

    public ItemBuilder buildIcon(Key key, List<SpawnerLog> logs) {
        ItemBuilder builder;

        try {
            switch (key.type) {
                case PLAYER:
                    User user = CoreProvider.Cache.Local.USERS.provide().get(Integer.valueOf(key.typeValue));
                    builder = ItemBuilder.of(HeadTexture.getPlayerHead(user.getNick()))
                            .name(user.getHighestGroup().getDisplayTag(user.getNick()));
                    break;
                case AUTOMATED:
                    builder = ItemBuilder.of(HeadTexture.HOURGLASS.getHead())
                            .name("&eArmazenamento automático")
                            .lore(
                                    "Armazenado automaticamente",
                                    "após " + UserCooldowns.getFormattedTimeLeft(Long.parseLong(key.typeValue)) + " de uso.",
                                    ""
                            );
                    break;
                default:
                    builder = ItemBuilder.of(Material.BARRIER);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ItemBuilder.of(Material.BARRIER);
        }

        logs.forEach(log -> builder.lore(String.format(
                "&7%s &e%sx %s",
                log.getAction().getActionName(),
                log.getAmount(),
                log.getSpawnerType().getDisplayName()
        )));

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy HH:mm");
        String dateStr = format.format(key.date);

        builder.lore("").lore("&7Em: &f" + dateStr);

        return builder;
    }

    @EqualsAndHashCode
    @RequiredArgsConstructor
    private static class Key {

        private final LogSourceType type;
        private final String typeValue;
        private final Date date;
    }
}
