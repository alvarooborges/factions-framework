package net.hyze.factions.framework.menu.inventories;

import com.google.common.collect.Maps;
import net.hyze.core.spigot.inventory.CustomInventory;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.menu.MenuIcon;
import net.hyze.factions.framework.menu.icons.*;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.inventory.Inventory;

import java.util.Map;
import java.util.function.Supplier;

public class LeaderIndexInventory extends CustomInventory {

    public LeaderIndexInventory(FactionUser user) {
        super(6 * 9, user.getRelation().getFaction().getStrippedDisplayName());

        Map<Integer, MenuIcon> icons = Maps.newHashMap();

        Supplier<Inventory> supplier = () -> new LeaderIndexInventory(user);

        icons.put(10, new FactionInformationIcon(user, supplier));
        icons.put(11, new PersonalInformationIcon(user, supplier));

        icons.put(13, new MapIcon(user, supplier));
        icons.put(14, new ShowLandsIcon(user, supplier));
        icons.put(15, new FactionsRankIcon(user, supplier));
        icons.put(16, new HelpIcon(user, supplier));

        //  icons.put(13, new TpaAcceptLogIcon(user, supplier));

        icons.put(29, new FactionClaimsIcon(user, supplier));
        icons.put(30, new FactionHomeIcon(user, supplier));
        icons.put(31, new FactionSpawnersIcon(user, supplier));
        icons.put(32, new PermissionsIcon(user, supplier));
        icons.put(33, new FactionMembersIcon(user, supplier));

        if (FactionsProvider.getSettings().isAllowAlly()) {
            icons.put(39, new AlliesIcon(user, supplier));
        }

        icons.put(40, new LabIcon(user, supplier));

        //customizations

        icons.put(41, new DisbandIcon(user, supplier));

        if (FactionsProvider.getSettings().isBankEnabled()) {
            icons.put(53, new FactionBankIcon(user, supplier));
        }

        for (Map.Entry<Integer, MenuIcon> entry : icons.entrySet()) {
            Integer slot = entry.getKey();
            MenuIcon icon = entry.getValue();

            if (icon.getEvent() != null) {
                setItem(slot, icon.getIcon(), icon.getEvent());
            } else {
                setItem(slot, icon.getIcon(), icon.getRunnable());
            }

        }
    }

}
