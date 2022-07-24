package net.hyze.factions.framework.setups;

import dev.utils.shared.setup.Setup;
import dev.utils.shared.setup.SetupException;
import net.hyze.auction.AuctionProvider;
import net.hyze.auction.manager.AuctionManager;
import org.bukkit.plugin.java.JavaPlugin;

public class AuctionSetup implements Setup<JavaPlugin> {

    @Override
    public void enable(JavaPlugin plugin) throws SetupException {

        AuctionManager.updateItem(
                AuctionProvider.Repositories.AUCTION.provide().fetchItem()
        );

    }
}
