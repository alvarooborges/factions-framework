package net.hyze.factions.framework.setups;

import dev.utils.shared.setup.Setup;
import dev.utils.shared.setup.SetupException;
import net.hyze.factions.framework.FactionsCustomPlugin;

public abstract class FactionsSetup<T extends FactionsCustomPlugin> implements Setup<T> {

    @Override
    public boolean test(T plugin) {
        return true;
    }

    @Override
    public void load(T plugin) throws SetupException {

    }

    @Override
    public void enable(T plugin) throws SetupException {

    }

    @Override
    public void disable(T plugin) throws SetupException {

    }
}
