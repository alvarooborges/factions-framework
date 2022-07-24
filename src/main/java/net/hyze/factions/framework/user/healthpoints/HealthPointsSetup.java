package net.hyze.factions.framework.user.healthpoints;

import dev.utils.shared.setup.SetupException;
import net.hyze.factions.framework.FactionsCustomPlugin;
import net.hyze.factions.framework.setups.FactionsSetup;
import net.hyze.factions.framework.user.healthpoints.listeners.HealthPointsListeners;
import org.bukkit.plugin.PluginManager;

public class HealthPointsSetup<T extends FactionsCustomPlugin> extends FactionsSetup<T> {

    @Override
    public void enable(T plugin) throws SetupException {
        PluginManager pluginManager = plugin.getServer().getPluginManager();

        pluginManager.registerEvents(new HealthPointsListeners(), plugin);
    }
}
