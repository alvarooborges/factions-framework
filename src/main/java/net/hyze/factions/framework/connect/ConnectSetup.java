package net.hyze.factions.framework.connect;

import dev.utils.shared.setup.Setup;
import dev.utils.shared.setup.SetupException;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.spigot.connect.CoreConnectManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ConnectSetup implements Setup<JavaPlugin> {

    @Override
    public void enable(JavaPlugin instance) throws SetupException {
        FactionsConnectManager manager = new FactionsConnectManager();

        instance.getServer().getPluginManager().registerEvents(manager, instance);

//        CoreProvider.Redis.ECHO.provide().registerListener(manager);
        CoreProvider.Redis.ECHO.provide().registerListener(new CoreConnectManager());
    }
}
