package net.hyze.factions.framework.ranking;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public class AsyncRankUpdateEvent extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final Ranking ranking;

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
