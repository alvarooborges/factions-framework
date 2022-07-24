package net.hyze.factions.framework.beacon.listeners;

import net.hyze.beacon.BeaconProperties;
import net.hyze.beacon.BeaconProvider;
import net.hyze.factions.framework.beacon.FactionBeaconAttribute;
import net.hyze.factions.framework.beacon.FactionBeaconConstants;
import net.hyze.factions.framework.beacon.attributes.durability.DurabilityAttribute;
import net.hyze.factions.framework.beacon.attributes.durability.DurabilityAttributeLevel;
import net.hyze.obsidiandestroyer.enumerations.DamageResult;
import net.hyze.obsidiandestroyer.events.GetMaxDurabilityEvent;
import net.hyze.obsidiandestroyer.events.PostDurabilityDamageEvent;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class BeaconObsidianDestroyerListeners implements Listener {

    @EventHandler
    public void on(GetMaxDurabilityEvent event) {
        Location location = event.getLocation();

        BeaconProperties properties = BeaconProvider.Cache.Local.BEACON.provide().get(location);

        if (properties != null) {

            DurabilityAttribute durability = (DurabilityAttribute) FactionBeaconAttribute.DURABILITY.getAttribute();

            int level = properties.getAttributes().get(durability.getId());

            DurabilityAttributeLevel durabilityLevel = (DurabilityAttributeLevel) durability.getLevels().get(level - 1);

            event.setMaxDurability(durabilityLevel.getDurability());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(PostDurabilityDamageEvent event) {

        Block block = event.getBlock();
        Location location = block.getLocation();

        BeaconProperties properties = BeaconProvider.Cache.Local.BEACON.provide().get(location);

        if (properties == null) {
            return;
        }

        if (!properties.getId().equalsIgnoreCase(FactionBeaconConstants.BEACON_SUPREME)) {
            return;
        }

        if (event.getDamageResult().equals(DamageResult.DESTROY)) {
            event.setCancelled(true);

            properties.getMetadata().put(FactionBeaconConstants.BEACON_BREAKED, System.currentTimeMillis() + "");
            BeaconProvider.Repositories.BEACON.provide().update(block.getLocation(), properties);

            System.out.println("> Beacon quebrou!");
        }

    }
}
