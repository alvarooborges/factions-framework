package net.hyze.factions.framework.misc.customitem;

import dev.utils.shared.setup.Setup;
import dev.utils.shared.setup.SetupException;
import net.hyze.core.spigot.misc.customitem.CustomItemRegistry;
import net.hyze.core.spigot.misc.customitem.data.compacted.MagmaCreamCompacted;
import net.hyze.factions.framework.beacon.FactionsDustyItem;
import net.hyze.factions.framework.beacon.FactionsMasterDustyItem;
import net.hyze.factions.framework.misc.customitem.data.MasterLightningItem;
import net.hyze.factions.framework.misc.customitem.data.sheeps.ColorfullSheepEggItem;
import net.hyze.factions.framework.misc.customitem.data.sheeps.RainbowSheepEggItem;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomItemSetup implements Setup<JavaPlugin> {

    @Override
    public void enable(JavaPlugin instance) throws SetupException {
        CustomItemRegistry.registerCustomItem(new FactionsDustyItem());
        CustomItemRegistry.registerCustomItem(new FactionsMasterDustyItem());
        CustomItemRegistry.registerCustomItem(FactionsCustomItem.SPY_BOOK.getCustomItem());
        CustomItemRegistry.registerCustomItem(new MagmaCreamCompacted());
        CustomItemRegistry.registerCustomItem(new MasterLightningItem());
        //CustomItemRegistry.registerCustomItem(new SuicideSheepEggItem());
        CustomItemRegistry.registerCustomItem(new ColorfullSheepEggItem());
        CustomItemRegistry.registerCustomItem(new RainbowSheepEggItem());
    }
}
