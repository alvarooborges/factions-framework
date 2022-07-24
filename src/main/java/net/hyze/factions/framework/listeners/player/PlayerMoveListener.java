package net.hyze.factions.framework.listeners.player;

import net.hyze.core.shared.group.Group;
import net.hyze.core.spigot.misc.combat.CombatManager;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.LocationUtils;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.lands.Claim;
import net.hyze.factions.framework.misc.utils.LandUtils;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Objects;

public class PlayerMoveListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(PlayerMoveEvent event) {

        Location to = event.getTo();
        Location from = event.getFrom();

        if (LocationUtils.compareLocation(to, from)) {
            return;
        }

        Player player = event.getPlayer();

        if (player.isFlying() || player.getAllowFlight()) {
            FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(player);

            if (player.getGameMode() == GameMode.SURVIVAL && !user.getHandle().hasGroup(Group.MODERATOR)) {

                Claim claim = LandUtils.getClaim(to);

                FactionUserRelation relation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getByUserId(user.getId());

                if (CombatManager.isTagged(user.getHandle())) {
                    player.setAllowFlight(false);
                    player.setFlying(false);
                    Message.ERROR.send(player, "Você não pode voar em combate.");

                    user.stopFlyingTask();
                    return;
                }

                if (relation == null || claim == null || !Objects.equals(claim.getFactionId(), relation.getFaction().getId())) {
                    player.setAllowFlight(false);
                    player.setFlying(false);
                    Message.ERROR.send(player, "Você só pode voar em terras de sua facção.");

                    user.stopFlyingTask();
                    return;
                }

                if (relation.getFaction().isUnderAttack()) {
                    player.setAllowFlight(false);
                    player.setFlying(false);
                    Message.ERROR.send(player, "Você não pode voar enquanto sua facção estiver sob ataque.");

                    user.stopFlyingTask();
                    return;
                }
            }

        }
    }
}
