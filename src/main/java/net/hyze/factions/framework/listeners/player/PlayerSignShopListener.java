package net.hyze.factions.framework.listeners.player;

import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.group.Group;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.misc.utils.FactionUserUtils;
import net.hyze.factions.framework.user.FactionUser;
import net.hyze.signshop.events.PlayerSignShopBuyEvent;
import net.hyze.signshop.events.PlayerSignShopSellEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class PlayerSignShopListener implements Listener {

//    private final Set<Material> SELL_VALID_MATERIALS = Sets.newHashSet(
//            Material.IRON_INGOT,
//            Material.IRON_BLOCK,
//            Material.DIAMOND,
//            Material.DIAMOND_BLOCK,
//            Material.EMERALD,
//            Material.EMERALD_BLOCK,
//            Material.GOLD_INGOT,
//            Material.GOLD_BLOCK,
//            Material.BLAZE_ROD,
//            Material.BONE,
//            Material.NETHER_STAR,
//            Material.SUGAR_CANE,
//            Material.NETHER_WARTS,
//            Material.BROWN_MUSHROOM,
//            Material.RED_MUSHROOM,
//            Material.WOOL,
//            Material.GOLD_RECORD,
//            Material.GREEN_RECORD,
//            Material.RECORD_6,
//            Material.RECORD_11
//    );

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onSellMonitor(PlayerSignShopSellEvent event) {

        try {
//            if (!SELL_VALID_MATERIALS.contains(event.getShop().getItem().getType())) {
//                return;
//            }

            FactionUser factionUser = FactionUserUtils.getUser(event.getPlayer());

            FactionUserRelation relation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getByUser(factionUser);

            if (relation == null) {
                return;
            }

            Faction faction = relation.getFaction();

            FactionsProvider.Repositories.FACTIONS_RANKING.provide().insertSellValue(faction, event.getValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void on(PlayerSignShopSellEvent event) {

        if (AppType.FACTIONS_VIP.isCurrent()) {
            if (!event.getUser().hasGroup(Group.ARCANE)) {
                event.setCancelled(true);
                Message.ERROR.send(event.getPlayer(), "Você não pode usar a loja VIP.");
            }
        }

    }

    @EventHandler
    public void on(PlayerSignShopBuyEvent event) {

        if (AppType.FACTIONS_VIP.isCurrent()) {
            if (!event.getUser().hasGroup(Group.ARCANE)) {
                event.setCancelled(true);
                Message.ERROR.send(event.getPlayer(), "Você não pode usar a loja VIP.");
            }
        }

    }
}
