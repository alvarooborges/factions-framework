package net.hyze.factions.framework.misc.tpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import net.hyze.core.shared.world.location.SerializedLocation;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.faction.Faction;

import java.util.Date;

@Builder
@Getter
@AllArgsConstructor
public class TpaAcceptLog {

    private final int id;
    private final Faction faction;

    private final int targetId;
    private final int requesterId;
    private final String targetTag;
    private final String requesterTag;

    private final SerializedLocation serializedLocation;
    private final Date date;

    public ItemBuilder getIcon() {
        ItemBuilder builder = null;

        return builder;
    }
}
