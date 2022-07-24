package net.hyze.factions.framework.divinealtar.listener;

import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.divinealtar.inventory.AltarInventory;
import net.hyze.factions.framework.divinealtar.power.PowerInstance;
import net.hyze.factions.framework.lands.Claim;
import net.hyze.factions.framework.misc.utils.LandUtils;
import net.hyze.obsidiandestroyer.events.GetMaxDurabilityEvent;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ObsidianDestroyerListener implements Listener {
    
    @EventHandler
    public void on(GetMaxDurabilityEvent event) {

        Location location = event.getLocation();

        Claim claim = LandUtils.getClaim(location);

        if (claim == null) {
            return;
        }

        AltarInventory out = FactionsProvider.Cache.Local.ALTAR.provide().getCache().values().stream()
                .filter(
                        inventory -> inventory.getAltarProperties()
                        .getActivePowers()
                        .containsKey(PowerInstance.DIVINE_PROTECTION)
                )
                .filter(inventory -> {
                    return inventory.getFactionId() == claim.getFactionId();
                })
                .findFirst()
                .orElse(null);

        if (out == null) {
            return;
        }
        
        /**
         * Duplica a durabilidade do bloco.
         */
        event.setMaxDurability(event.getMaxDurability() * 2);

    }

}
