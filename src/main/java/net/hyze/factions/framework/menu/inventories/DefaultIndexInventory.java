package net.hyze.factions.framework.menu.inventories;

import com.google.common.collect.Maps;
import net.hyze.core.spigot.inventory.CustomInventory;
import net.hyze.factions.framework.menu.MenuIcon;
import net.hyze.factions.framework.menu.icons.*;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.inventory.Inventory;

import java.util.Map;
import java.util.function.Supplier;

public class DefaultIndexInventory extends CustomInventory {

    public DefaultIndexInventory(FactionUser user) {
        super(5 * 9, user.getNick());

        Map<Integer, MenuIcon> icons = Maps.newHashMap();

        Supplier<Inventory> supplier = () -> new DefaultIndexInventory(user);

        icons.put(10, new PersonalInformationIcon(user, supplier));

        icons.put(14, new HelpIcon(user, supplier));
        icons.put(15, new FactionsRankIcon(user, supplier));

        icons.put(30, new MapIcon(user, supplier));
        icons.put(31, new ShowLandsIcon(user, supplier));
        icons.put(32, new FactionInvitationsIcon(user, supplier));

        icons.forEach((slot, icon) -> {
            if (icon.getEvent() != null) {
                setItem(slot, icon.getIcon(), icon.getEvent());
            } else {
                setItem(slot, icon.getIcon(), icon.getRunnable());
            }
        });
    }

}
