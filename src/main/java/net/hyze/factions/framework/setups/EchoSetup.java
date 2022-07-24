package net.hyze.factions.framework.setups;

import dev.utils.echo.Echo;
import dev.utils.shared.setup.Setup;
import dev.utils.shared.setup.SetupException;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.spigot.misc.tpa.TPAManager;
import net.hyze.factions.framework.echo.listeners.FactionEchoListener;
import net.hyze.factions.framework.echo.listeners.FactionPermissionEchoListener;
import net.hyze.factions.framework.echo.listeners.GeneralEchoListener;
import net.hyze.factions.framework.echo.listeners.UserEchoListener;
import org.bukkit.plugin.java.JavaPlugin;

public class EchoSetup implements Setup<JavaPlugin> {

    @Override
    public void enable(JavaPlugin instance) throws SetupException {
        /*
         * Registrando eventos do Echo
         */
        Echo echo = CoreProvider.Redis.ECHO.provide();

        echo.registerListener(new FactionEchoListener());
        echo.registerListener(new UserEchoListener());
        echo.registerListener(new GeneralEchoListener());
        echo.registerListener(new FactionPermissionEchoListener());
        echo.registerListener(new TPAManager());
    }
}
