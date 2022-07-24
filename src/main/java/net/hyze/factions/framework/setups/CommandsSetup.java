package net.hyze.factions.framework.setups;

import dev.utils.shared.setup.Setup;
import dev.utils.shared.setup.SetupException;
import net.hyze.core.spigot.commands.CommandRegistry;
import net.hyze.core.spigot.commands.impl.CustomEnchantmentCommand;
import net.hyze.core.spigot.commands.impl.CustomItemCommand;
import net.hyze.core.spigot.misc.tpa.commands.TPACommand;
import net.hyze.core.spigot.misc.tpa.commands.TPAcceptCommand;
import net.hyze.core.spigot.misc.tpa.commands.TPCancelCommand;
import net.hyze.core.spigot.misc.tpa.commands.TPDenyCommand;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.commands.*;
import net.hyze.factions.framework.commands.factioncommand.FactionCommand;
import net.hyze.factions.framework.misc.targetsystem.TargetCommand;
import net.hyze.factions.framework.war.commands.WarCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandsSetup implements Setup<JavaPlugin> {

    @Override
    public void enable(JavaPlugin instance) throws SetupException {
        /*
         * Registrando comandos
         */
        FactionCommand factionCommand = new FactionCommand();
        CommandRegistry.registerCommand(factionCommand);
        CommandRegistry.registerCommand(new SpawnCommand());
        CommandRegistry.registerCommand(new GlobalChatCommand());
        CommandRegistry.registerCommand(new AllyChatCommand());
        CommandRegistry.registerCommand(new FactionChatCommand());

//        CommandRegistry.registerCommand(new FurnaceCommand());
        CommandRegistry.registerCommand(new SpawnerCommand());
        CommandRegistry.registerCommand(new CustomItemCommand());
        CommandRegistry.registerCommand(new CustomEnchantmentCommand());

        CommandRegistry.registerCommand(new TPACommand());
        CommandRegistry.registerCommand(new TPAcceptCommand());
        CommandRegistry.registerCommand(new TPCancelCommand());
        CommandRegistry.registerCommand(new TPDenyCommand());
        CommandRegistry.registerCommand(new VIPCommand());
        CommandRegistry.registerCommand(new ShopCommand());

        if (FactionsProvider.getSettings().isOfferSystemEnabled()) {
            CommandRegistry.registerCommand(new OfferCommand());
        }

        CommandRegistry.registerCommand(new TagsCommand());
        CommandRegistry.registerCommand(new UpgradeCommand());
        CommandRegistry.registerCommand(new WarCommand());
        CommandRegistry.registerCommand(new AnnounceCommand());
        CommandRegistry.registerCommand(new SkinCommand());

        /*
        fora dos padrões por ?
         */
        TargetCommand targetCommand = new TargetCommand();
        CommandRegistry.registerCommand(targetCommand);
        instance.getServer().getPluginManager().registerEvents(targetCommand, instance);
    }
}
