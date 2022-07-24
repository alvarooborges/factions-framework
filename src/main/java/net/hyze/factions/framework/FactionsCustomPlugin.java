package net.hyze.factions.framework;

import lombok.NonNull;
import net.hyze.core.shared.exceptions.ApplicationAlreadyPreparedException;
import net.hyze.core.shared.exceptions.InvalidApplicationException;
import net.hyze.core.shared.servers.Server;
import net.hyze.core.spigot.CustomPlugin;
import net.hyze.factions.framework.settings.SettingsManager;
import org.bukkit.Bukkit;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class FactionsCustomPlugin extends CustomPlugin {

    protected final Server server;

    protected FactionsSettings factionsSettings;

    public FactionsCustomPlugin(@NonNull Server server) {
        super(true);
        this.server = server;
    }

    @Override
    public void onLoad() {
        super.onLoad();

        if (this.prepareProvider) {
            try {
                FactionsProvider.prepare(server, factionsSettings);
            } catch (InvalidApplicationException | ApplicationAlreadyPreparedException ex) {
                Logger.getGlobal().log(Level.SEVERE, "Failed to prepare factions provider", ex);
                Bukkit.shutdown();
            }
        }

        SettingsManager.onLoad();
    }

    @Override
    public void onEnable() {
        super.onEnable();

        SettingsManager.onEnable();
        
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (this.prepareProvider) {
            try {
                FactionsProvider.shut();
            } catch (Exception ex) {
                Logger.getGlobal().log(Level.SEVERE, "Failed to shut factions provider", ex);
            }
        }
    }
}
