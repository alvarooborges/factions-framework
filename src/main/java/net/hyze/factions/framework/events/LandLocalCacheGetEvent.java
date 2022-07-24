package net.hyze.factions.framework.events;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.hyze.factions.framework.lands.Land;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public class LandLocalCacheGetEvent<T extends Land> extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    @NonNull
    private final String appId;
    private final int x;
    private final int z;
    private final Class<T> clazz;
    
    @Setter
    private T result;

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}
