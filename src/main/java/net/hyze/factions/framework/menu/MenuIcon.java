package net.hyze.factions.framework.menu;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Getter
@RequiredArgsConstructor
public abstract class MenuIcon {

    public final FactionUser user;
    public final Supplier<Inventory> back;
    
    public abstract ItemStack getIcon();
    public abstract Runnable getRunnable();

    public abstract Consumer<InventoryClickEvent> getEvent();
}
