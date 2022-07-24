package net.hyze.factions.framework.misc.tpa.inventories;

import com.google.common.collect.Lists;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.misc.utils.Pair;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.inventory.PaginateInventory;
import net.hyze.core.spigot.misc.utils.HeadTexture;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.permission.FactionPermission;
import net.hyze.factions.framework.misc.tpa.TpaAcceptLog;
import net.hyze.factions.framework.user.FactionUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TpaAcceptLogInventory extends PaginateInventory {

    public TpaAcceptLogInventory(FactionUser user, Faction faction) {
        super("Histórico de TPA: " + faction.getTag());

        Map<Pair<Integer, Date>, List<TpaAcceptLog>> map = FactionsProvider.Cache.Local.TPA_LOG.provide().get(faction).stream()
                .sorted((o1, o2) -> o2.getDate().compareTo(o1.getDate()))
                .collect(Collectors.groupingBy(log -> {
                    long roundTime = (log.getDate().getTime() / 1000 / 60) * 1000 * 60;

                    return new Pair<>(log.getTargetId(), new Date(roundTime));
                }))
                .entrySet()
                .stream()
                .sorted((o1, o2) -> {
                    int compare = o2.getKey().getRight().compareTo(o1.getKey().getRight());

                    if (compare == 0) {
                        int o1MinId = o1.getValue().stream().mapToInt(TpaAcceptLog::getId).min().orElse(0);
                        int o2MinId = o2.getValue().stream().mapToInt(TpaAcceptLog::getId).min().orElse(0);

                        return Integer.compare(o2MinId, o1MinId);
                    }

                    return compare;
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        for (Map.Entry<Pair<Integer, Date>, List<TpaAcceptLog>> entry : map.entrySet()) {
            for (List<TpaAcceptLog> logs : Lists.partition(entry.getValue(), 5)) {
                addItem(buildIcon(user, entry.getKey(), logs).make());
            }
        }

        backOrCloseItem();
    }

    public ItemBuilder buildIcon(FactionUser viewer, Pair<Integer, Date> key, List<TpaAcceptLog> logs) {
        User user = CoreProvider.Cache.Local.USERS.provide().get(key.getLeft());

        ItemBuilder builder = ItemBuilder.of(HeadTexture.getPlayerHead(user.getNick()))
                .name(String.format(
                        "&e%s&f%s aceitou:",
                        logs.get(0).getTargetTag() != null ? logs.get(0).getTargetTag() + " " : "",
                        user.getNick()
                ))
                .lore("");

        logs.forEach(log -> {
            User requester = CoreProvider.Cache.Local.USERS.provide().get(log.getRequesterId());

            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
            String dateStr = format.format(log.getDate());

            builder.lore(
                    String.format(
                            " &e%s&7%s",
                            log.getRequesterTag() != null ? log.getRequesterTag() + " " : "",
                            requester.getNick()
                    ),
                    "  &7Data: &f" + dateStr
            );

            if (FactionPermission.COMMAND_BASE.allows(log.getFaction(), viewer) || viewer.getOptions().isAdminModeEnabled()) {
                builder.lore(String.format(
                        "  &7Local: &fx: %s, y: %s, z: %s",
                        (int) log.getSerializedLocation().getX(),
                        (int) log.getSerializedLocation().getY(),
                        (int) log.getSerializedLocation().getZ()
                ));
            }

            builder.lore("");
        });


        return builder;
    }


}
