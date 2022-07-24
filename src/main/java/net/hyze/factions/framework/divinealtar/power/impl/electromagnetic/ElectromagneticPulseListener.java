package net.hyze.factions.framework.divinealtar.power.impl.electromagnetic;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class ElectromagneticPulseListener implements Listener {

    @EventHandler
    public void on(EntityExplodeEvent event) {

//        if (!(event.getEntity() instanceof TNTPrimed)) {
//            return;
//        }
//
//        TNTPrimed tnt = (TNTPrimed) event.getEntity();
//
//        Location location = tnt.getLocation();
//
//        Claim claim = LandUtils.getClaim(location);
//
//        if (claim == null) {
//            return;
//        }
//
//        ElectromagneticPulseManager.log(claim.getFactionId(), tnt.getSourceLoc());

    }

    @EventHandler
    public void on(BlockRedstoneEvent event) {

//        Location location = event.getBlock().getLocation();
//
//        if (ElectromagneticPulseManager.contains(location)) {
//            event.setNewCurrent(0);
//        }

    }

}
