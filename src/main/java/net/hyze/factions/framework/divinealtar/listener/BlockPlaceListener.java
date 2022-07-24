package net.hyze.factions.framework.divinealtar.listener;

import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.lands.Claim;
import net.hyze.factions.framework.misc.utils.LandUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void on(BlockPlaceEvent event) {
        Block block = event.getBlock();

        if (block.getType() != Material.ENDER_PORTAL_FRAME) {
            return;
        }

        Claim claim = LandUtils.getClaim(block.getLocation());

        boolean canBuild = LandUtils.canBuildAt(
                FactionsProvider.Cache.Local.USERS.provide().get(event.getPlayer().getName()),
                block.getLocation().getChunk().getX(),
                block.getLocation().getChunk().getZ()
        );

        if (claim == null || !canBuild) {
            Message.ERROR.send(event.getPlayer(), "Você só pode colocar o Altar Divino em terrenos de sua facção.");
            event.setCancelled(true);
            return;
        }

//        Long cooldown = FactionsProvider.Repositories.ALTAR.provide().getCooldown(claim.getFactionId());
//
//        if (cooldown != null && (System.currentTimeMillis() - cooldown) < AltarConstants.COOLDOWN) {
//            Message.ERROR.send(
//                    event.getPlayer(),
//                    String.format(
//                            "Ops, você precisa aguardar para colocar o Altar Divino novamente. (%s)",
//                            TimeCode.toText((cooldown + AltarConstants.COOLDOWN) - System.currentTimeMillis(), 5)
//                    )
//            );
//            event.setCancelled(true);
//            return;
//        }

//        FactionsProvider.Cache.Local.ALTAR.provide().put(block.getLocation(),
//                new AltarInventory(properties, block.getLocation())
//        );


        Message.SUCCESS.send(event.getPlayer(), "Yay! Altar colocado com sucesso!");
    }

}
