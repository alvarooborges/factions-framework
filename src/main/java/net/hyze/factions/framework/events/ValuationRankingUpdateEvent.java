package net.hyze.factions.framework.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.hyze.factions.framework.faction.Faction;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public class ValuationRankingUpdateEvent extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();
    
    private final Faction faction;
    private final double sumValue;

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}
