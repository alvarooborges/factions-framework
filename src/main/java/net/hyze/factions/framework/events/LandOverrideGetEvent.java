package net.hyze.factions.framework.events;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.hyze.factions.framework.lands.Zone;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public class LandOverrideGetEvent extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    @NonNull
    private final Location location;

    @Setter
    private Zone result;

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
