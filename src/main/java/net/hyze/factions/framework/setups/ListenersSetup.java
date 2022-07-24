package net.hyze.factions.framework.setups;

import dev.utils.shared.setup.Setup;
import dev.utils.shared.setup.SetupException;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.spigot.misc.antidupe.AntiDupeListener;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.beacon.listeners.BeaconListener;
import net.hyze.factions.framework.beacon.listeners.BeaconObsidianDestroyerListeners;
import net.hyze.factions.framework.furypoints.FuryListener;
import net.hyze.factions.framework.listeners.*;
import net.hyze.factions.framework.listeners.player.*;
import net.hyze.factions.framework.misc.offers.impl.spawners.OfferSpawnerListener;
import net.hyze.factions.framework.misc.playerheads.PlayerHeadsListeners;
import net.hyze.factions.framework.misc.scoreboard.ScoreboardManager;
import net.hyze.factions.framework.misc.suspect.SuspectListener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ListenersSetup implements Setup<JavaPlugin> {

    @Override
    public void enable(JavaPlugin instance) throws SetupException {
        PluginManager pluginManager = instance.getServer().getPluginManager();

        ScoreboardManager scoreboardManager = new ScoreboardManager();
        /*
         * Registrando eventos
         */
        pluginManager.registerEvents(new PlayerConnectionListener(), instance);
        pluginManager.registerEvents(new PlayerListener(), instance);
        pluginManager.registerEvents(new GeneralListener(), instance);
        pluginManager.registerEvents(new HumanDataListener(), instance);
        pluginManager.registerEvents(new ServerListener(), instance);
        pluginManager.registerEvents(new EntityListener(), instance);
        pluginManager.registerEvents(new BlockListener(), instance);
        pluginManager.registerEvents(new PlayerHomeListener(), instance);
        pluginManager.registerEvents(new TPAListener(), instance);
        pluginManager.registerEvents(scoreboardManager, instance);
        pluginManager.registerEvents(new FuryListener(), instance);
        pluginManager.registerEvents(new PlayerTeleportListener(), instance);
        pluginManager.registerEvents(new PlayerInteractListener(), instance);
        pluginManager.registerEvents(new PlayerDeathListener(), instance);

        //AutoPickUP - plantações
        pluginManager.registerEvents(new AutoPickUpListeners(), instance);

        if (AppType.FactionsAppType.isCurrentAllowClaim() || AppType.FACTIONS_LOSTFORTRESS.isCurrent()) {
            pluginManager.registerEvents(new PlayerMoveListener(), instance);
        }

        pluginManager.registerEvents(new SuspectListener(), instance);
        pluginManager.registerEvents(new AntiDupeListener(), instance);

        pluginManager.registerEvents(new PlayerChatListener(), instance);
        pluginManager.registerEvents(new PlayerSignShopListener(), instance);

        if (FactionsProvider.getSettings().isOfferSystemEnabled()) {
            pluginManager.registerEvents(new OfferSpawnerListener(), instance);
        }

        pluginManager.registerEvents(new CustomBlockExplodeListener(), instance);
        pluginManager.registerEvents(new ObsidianDestroyerListener(), instance);
        pluginManager.registerEvents(new PlayerHeadsListeners(instance), instance);
        pluginManager.registerEvents(new FactionsCitizensListeners(), instance);

        // Beacon
        pluginManager.registerEvents(new BeaconListener(), instance);
        pluginManager.registerEvents(new BeaconObsidianDestroyerListeners(), instance);
    }
}
