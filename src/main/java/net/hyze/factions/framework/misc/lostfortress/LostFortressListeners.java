package net.hyze.factions.framework.misc.lostfortress;

import net.hyze.core.shared.group.Group;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.core.spigot.misc.utils.LocationUtils;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class LostFortressListeners implements Listener {

    @EventHandler
    public void on(PlayerPickupItemEvent event) {
        ItemStack itemStack = event.getItem().getItemStack();
        ItemBuilder item = ItemBuilder.of(itemStack, true);

        if (item.hasNbt(LostFortressConstants.ITEM_NBT)) {
            ItemBuilder.of(itemStack, true).removeNbt(LostFortressConstants.ITEM_NBT);

            FactionUser factionUser = FactionsProvider.Cache.Local.USERS.provide().get(event.getPlayer());
            LostFortress lostFortress = LostFortressConstants.CURRENT;

            lostFortress.logItemStack(factionUser, itemStack);
        }
    }

    @EventHandler
    public void on(EntityDamageEvent event) {

        if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)
                && !LostFortressConstants.FALL_DAMAGE) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void on(PlayerMoveEvent event) {

        Location to = event.getTo();
        Location from = event.getFrom();

        if (LocationUtils.compareLocation(to, from)) {
            return;
        }

        Player player = event.getPlayer();

        if (LostFortressConstants.LOST_FORSTRESS_INSIDE_CUBOID.contains(player.getLocation(), true)) {

            if (LostFortressConstants.CURRENT.getFirstPlayer() == null) {
                FactionUser factionUser = FactionsProvider.Cache.Local.USERS.provide().get(player);

                if (!factionUser.getHandle().hasGroup(Group.MODERATOR)) {
                    LostFortressConstants.CURRENT.setFirstPlayer(factionUser);

                    if (LostFortressConstants.CURRENT.getFirstFaction() == null) {
                        if (factionUser.getRelation() != null) {
                            LostFortressConstants.CURRENT.setFirstFaction(factionUser.getRelation().getFaction());
                            return;
                        }
                    }
                }
            }

            if (LostFortressConstants.CURRENT.getFirstFaction() == null) {
                FactionUser factionUser = FactionsProvider.Cache.Local.USERS.provide().get(player);

                if (!factionUser.getHandle().hasGroup(Group.MODERATOR)) {
                    if (factionUser.getRelation() != null) {
                        LostFortressConstants.CURRENT.setFirstFaction(factionUser.getRelation().getFaction());
                    }
                }

            }

        }

    }

}
