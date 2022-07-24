package net.hyze.factions.framework.divinealtar;

import com.google.common.collect.Lists;
import dev.utils.echo.Echo;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.spigot.commands.CommandRegistry;
import net.hyze.core.spigot.misc.blockdrops.BlockDropsManager;
import net.hyze.core.spigot.misc.customitem.CustomItemRegistry;
import net.hyze.factions.framework.FactionsPlugin;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.divinealtar.command.AltarCommand;
import net.hyze.factions.framework.divinealtar.echo.listeners.AltarPacketListeners;
import net.hyze.factions.framework.divinealtar.inventory.AltarInventory;
import net.hyze.factions.framework.divinealtar.listener.BlockBreakListener;
import net.hyze.factions.framework.divinealtar.listener.BlockPlaceListener;
import net.hyze.factions.framework.divinealtar.listener.InteractListener;
import net.hyze.factions.framework.divinealtar.listener.ObsidianDestroyerListener;
import net.hyze.factions.framework.divinealtar.manager.AltarManager;
import net.hyze.factions.framework.divinealtar.misc.customitems.GemItem;
import net.hyze.factions.framework.divinealtar.misc.customitems.MeteorRainItem;
import net.hyze.factions.framework.divinealtar.power.PowerInstance;
import net.hyze.factions.framework.divinealtar.power.impl.ProsperityPower;
import net.hyze.factions.framework.divinealtar.power.impl.electromagnetic.ElectromagneticPulseListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

import java.util.HashMap;
import java.util.stream.Stream;

public class AltarSetup {

    public static void setup(FactionsPlugin plugin) {

        /**
         * Registra comandos.
         */
        CommandRegistry.registerCommand(new AltarCommand());

        /**
         * Registra todas as moedas de poderes.
         */
        Stream.of(PowerInstance.values())
                .forEach(
                        power -> CustomItemRegistry.registerCustomItem(
                                new GemItem(power)
                        )
                );

        CustomItemRegistry.registerCustomItem(new MeteorRainItem());

        /**
         * Atualiza todos inventários de 1 em 1 minuto.
         */
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            FactionsProvider.Cache.Local.ALTAR.provide()
                    .getCache()
                    .values()
                    .forEach(inventory -> inventory.update());
        }, 20L, 20L);

        /**
         * Registra todos os altares.
         */
        HashMap<Integer, AltarInventory> cache = FactionsProvider.Repositories.ALTAR.provide().fetch();
        FactionsProvider.Cache.Local.ALTAR.provide().getCache().putAll(cache);

        /**
         * Registrando eventos do Echo.
         */
        Echo echo = CoreProvider.Redis.ECHO.provide();
        echo.registerListener(new AltarPacketListeners());

        /**
         * Registra todos os eventos.
         */
        PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new BlockBreakListener(), plugin);
        pluginManager.registerEvents(new BlockPlaceListener(), plugin);
        pluginManager.registerEvents(new InteractListener(), plugin);
        pluginManager.registerEvents(new ObsidianDestroyerListener(), plugin);
        pluginManager.registerEvents(new MeteorRainItem(), plugin);
        pluginManager.registerEvents(new ElectromagneticPulseListener(), plugin);
        pluginManager.registerEvents((ProsperityPower) PowerInstance.PROSPERITY.getPower(), plugin);

        BlockDropsManager.registerHandler(Material.ENDER_PORTAL_FRAME, (Block block, Player player, ItemStack tool) -> {
            return Lists.newArrayList(AltarManager.buildAltarItem());
        });

    }

}
