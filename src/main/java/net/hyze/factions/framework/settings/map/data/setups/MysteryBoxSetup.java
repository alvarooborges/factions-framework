package net.hyze.factions.framework.settings.map.data.setups;

import lombok.RequiredArgsConstructor;
import net.hyze.core.spigot.misc.hologram.Hologram;
import net.hyze.core.spigot.misc.hologram.HologramPosition;
import net.hyze.core.spigot.misc.utils.LocationUtils;
import net.hyze.factions.framework.FactionsCustomPlugin;
import net.hyze.factions.framework.settings.map.IMapSetup;
import net.hyze.mysterybox.MysteryBoxConstants;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

@RequiredArgsConstructor
public class MysteryBoxSetup implements IMapSetup, Listener {

    private final Location location;

    @Override
    public void enable(FactionsCustomPlugin plugin) {

        new Hologram(HologramPosition.DOWN)
                .line("&e&lCaixa Misteriosa")
                .line("&fClique para abrir!")
                .spawn(LocationUtils.center(this.location.clone().add(0, 1.3, 0)));

        Bukkit.getPluginManager().registerEvents(this, plugin);

    }

    @EventHandler
    public void on(PlayerInteractEvent event) {

        if (!event.hasBlock()) {
            return;
        }

        if (!event.getClickedBlock().getType().equals(Material.ENDER_PORTAL_FRAME)) {
            return;
        }

        if (event.getClickedBlock().getLocation().equals(this.location)) {
            event.setCancelled(true);
            event.getPlayer().openInventory(MysteryBoxConstants.BOX_LIST_INVENTORY);
        }

    }


}
