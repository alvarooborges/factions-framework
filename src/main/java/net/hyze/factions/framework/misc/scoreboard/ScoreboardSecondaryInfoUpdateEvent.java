package net.hyze.factions.framework.misc.scoreboard;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public class ScoreboardSecondaryInfoUpdateEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final FactionUser user;
    private final FactionsScoreboard scoreboard;

    @Setter
    private boolean cancelled;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
