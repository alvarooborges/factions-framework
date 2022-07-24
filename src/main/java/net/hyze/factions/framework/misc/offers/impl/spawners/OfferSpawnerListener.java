package net.hyze.factions.framework.misc.offers.impl.spawners;

import com.google.common.base.Enums;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.lands.Claim;
import net.hyze.factions.framework.misc.utils.LandUtils;
import net.hyze.factions.framework.spawners.SpawnersSetup;
import net.hyze.factions.framework.spawners.SpawnerType;
import net.hyze.obsidiandestroyer.events.CustomBlockExplodeEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class OfferSpawnerListener implements Listener {

    @EventHandler
    public void on(CustomBlockExplodeEvent event) {

        if (!event.getCustomBlock().getMaterial().equals(Material.MOB_SPAWNER)) {
            return;
        }

        if (event.isCanDrop()) {
            return;
        }

        SpawnerType type = Enums.getIfPresent(SpawnerType.class, event.getAt().getBlock().getMetadata(SpawnersSetup.METADATA_TYPE_TAG).get(0).asString())
                .orNull();

        if (type == null) {
            return;
        }

        Claim claim = LandUtils.getClaim(event.getAt());

        if (claim == null || claim.getFactionId() == null) {
            return;
        }

        FactionsProvider.Cache.Local.OFFER_SPAWNER.provide().putSpawner(claim.getFactionId(), type);

        System.out.println("\n* SPAWNER EXPLODIDO: 1x " + type.name() + "\n ");

    }
}
