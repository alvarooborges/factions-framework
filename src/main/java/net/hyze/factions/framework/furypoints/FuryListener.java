package net.hyze.factions.framework.furypoints;

import org.bukkit.event.Listener;

public class FuryListener implements Listener {

//    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
//    public void on(EntityDamageByEntityEvent event) {
//
//        if (!(event.getDamager() instanceof Player)) {
//            return;
//        }
//
//        Player damager = (Player) event.getDamager();
//        ItemStack item = damager.getItemInHand();
//
//        if (item == null || !item.getType().equals(Material.DIAMOND_SWORD)) {
//            return;
//        }
//
//        /**
//         * Possuí efeito da espada boladona.
//         */
//        if (ItemBuilder.of(item).hasNbt(FuryConstants.KEY_FURY)) {
//
//            return;
//        }
//
//        Entity damaged = event.getEntity();
//        FuryType type = damaged instanceof Player ? FuryType.FURY_PLAYERS_KEY : FuryType.FURY_MOBS_KEY;
//
//        new FurySword(damager.getItemInHand(), (int) event.getDamage(), type).apply(damager);
//
//    }

}
