package net.hyze.factions.framework.setups;

import lombok.RequiredArgsConstructor;
import net.hyze.factions.framework.FactionsCustomPlugin;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.safe.SafeProvider;
import net.hyze.safe.SafeSettings;

@RequiredArgsConstructor
public class SafeSetup<T extends FactionsCustomPlugin> extends FactionsSetup<T> {

    private final SafeSettings settings;

    @Override
    public void load(FactionsCustomPlugin plugin) {
        SafeProvider.prepare(
                FactionsProvider.Database.MYSQL_FACTIONS,
                FactionsProvider.Redis.REDIS_FACTIONS,
                settings
        );
    }
}
