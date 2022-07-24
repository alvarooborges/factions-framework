package net.hyze.factions.framework.menu.icons;

import net.hyze.factions.framework.menu.MenuIcon;
import net.hyze.factions.framework.misc.utils.FactionUtils;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class FactionInformationIcon extends MenuIcon {

    public FactionInformationIcon(FactionUser user, Supplier<Inventory> back) {
        super(user, back);
    }

    @Override
    public ItemStack getIcon() {
        return FactionUtils.getInformationIcon(user.getRelation().getFaction(), user).make();
    }

    @Override
    public Runnable getRunnable() {
        return null;
    }

    @Override
    public Consumer<InventoryClickEvent> getEvent() {
        return null;
    }

}
