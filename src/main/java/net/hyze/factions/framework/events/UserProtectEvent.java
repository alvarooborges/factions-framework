package net.hyze.factions.framework.events;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public class UserProtectEvent extends Event implements Cancellable {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    @Setter
    private boolean cancelled;

    @NonNull
    private final FactionUser user;

    @NonNull
    private final Location location;

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
