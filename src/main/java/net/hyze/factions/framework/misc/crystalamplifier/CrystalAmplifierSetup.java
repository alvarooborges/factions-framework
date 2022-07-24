package net.hyze.factions.framework.misc.crystalamplifier;

import net.hyze.core.shared.apps.AppType;
import net.hyze.core.spigot.misc.customitem.CustomItemRegistry;
import net.hyze.factions.framework.FactionsPlugin;
import net.hyze.factions.framework.FactionsProvider;
import org.bukkit.Bukkit;

public class CrystalAmplifierSetup {

    public static void setup() {

        CustomItemRegistry.registerCustomItem(new CrystalAmplifierItem());

        if (!(AppType.FactionsAppType.isCurrentAllowClaim() || AppType.FACTIONS_TESTS.isCurrent())) {
            return;
        }

        FactionsProvider.Repositories.CRYSTAL_AMPLIFIER.provide().fetch();

        Bukkit.getPluginManager().registerEvents(new CrystalAmplifierListeners(), FactionsPlugin.getInstance());

    }

}
