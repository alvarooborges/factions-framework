package net.hyze.factions.framework.misc.crystalamplifier;

import net.hyze.core.shared.apps.AppType;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.lands.Claim;
import net.hyze.factions.framework.misc.utils.LandUtils;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.Chunk;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class CrystalAmplifierListeners implements Listener {

//    @EventHandler
//    public void on(InventoryClickEvent event){
//
//        if(event.getInventory().getName().equalsIgnoreCase("Cristal Amplificador")){
//            System.out.println("> " + event.getWhoClicked().getName() + " clicou em " + event.getCurrentItem().getType().name());
//            event.getWhoClicked().sendMessage("Debug: " +  event.getCurrentItem().getType().name());
//        }
//
//    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        CrystalAmplifier crystal = FactionsProvider.Cache.Local.CRYSTAL_AMPLIFIER.provide().get(chunk);

        if (crystal == null) {
            return;
        }

        if (System.currentTimeMillis() > crystal.getEndTime()) {
            CrystalAmplifierUtils.remove(crystal.getFactionId());
            return;
        }

        crystal.spawn();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(ChunkUnloadEvent event) {
        Chunk chunk = event.getChunk();
        CrystalAmplifier crystal = FactionsProvider.Cache.Local.CRYSTAL_AMPLIFIER.provide().get(chunk);

        if (crystal == null) {
            return;
        }

        crystal.destroy();
    }

    @EventHandler
    public void on(PlayerInteractEntityEvent event) {

        if (!event.getRightClicked().getType().equals(EntityType.ENDER_CRYSTAL)) {
            return;
        }

        if (AppType.FACTIONS_END.isCurrent()) {
            return;
        }

        event.setCancelled(true);

        Player player = event.getPlayer();
        FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(player.getName());
        FactionUserRelation relation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getIfPresentByUserId(user.getId());

        if (relation == null) {
            return;
        }

        Claim claim = LandUtils.getClaim(event.getRightClicked().getLocation());

        if (claim == null
                || claim.getFaction() == null
                || !claim.getFactionId().equals(relation.getFaction().getId())) {
            return;
        }

        if (!FactionsProvider.Cache.Local.CRYSTAL_AMPLIFIER.provide().contains(claim.getFactionId())) {
            return;
        }

        player.openInventory(new CrystalAmplifierInventory(user.getRelation().getFaction()));

    }

    @EventHandler
    public void on(ExplosionPrimeEvent event) {
        if (event.getEntity().getType().equals(EntityType.ENDER_CRYSTAL)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void on(EntityExplodeEvent event) {
        if (event.getEntity().getType().equals(EntityType.ENDER_CRYSTAL)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void on(EntityDamageEvent event) {
        if (event.getEntity().getType().equals(EntityType.ENDER_CRYSTAL)) {
            event.setCancelled(true);
        }
    }

}
