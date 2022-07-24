package net.hyze.factions.framework.spawners.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hyze.factions.framework.spawners.SpawnerType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Getter
@AllArgsConstructor
public class EntitySpawnerTypeDeathEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private SpawnerType type;
    private Player killer;
    private List<ItemStack> drops;
    private int exp;

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
