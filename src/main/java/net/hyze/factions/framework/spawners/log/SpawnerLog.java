package net.hyze.factions.framework.spawners.log;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.misc.utils.HeadTexture;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.spawners.SpawnerType;
import org.bukkit.Material;

import java.text.SimpleDateFormat;
import java.util.Date;

@Builder
@Getter
@AllArgsConstructor
public class SpawnerLog {

    private final int id;
    private final Faction faction;
    private final LogSourceType type;
    private final String typeValue;
    private final LogAction action;
    private final SpawnerType spawnerType;
    private final Integer amount;
    private final Date date;

    public ItemBuilder getIcon() {

        ItemBuilder builder;

        try {
            if (this.type == LogSourceType.PLAYER) {
                User user = CoreProvider.Cache.Local.USERS.provide().get(Integer.valueOf(typeValue));
                builder = ItemBuilder.of(HeadTexture.getPlayerHead(user.getNick()))
                        .name(user.getHighestGroup().getDisplayTag(user.getNick()));
            } else {
                builder = ItemBuilder.of(Material.BARRIER);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ItemBuilder.of(Material.BARRIER);
        }

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy HH:mm");
        String dateStr = format.format(this.date);

        builder
                .lore(String.format(
                        "&7%s %x %s",
                        this.action.getActionName(),
                        this.amount,
                        this.spawnerType.getDisplayName()
                ))
                .lore("")
                .lore("&7Em: &f" + dateStr);

        return builder;
    }
}
