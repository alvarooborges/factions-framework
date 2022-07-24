package net.hyze.factions.framework.listeners.player;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.tpa.events.TPAcceptEvent;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.permission.FactionPermission;
import net.hyze.factions.framework.lands.Claim;
import net.hyze.factions.framework.misc.utils.LandUtils;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class TPAListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onTPAcceptHighest(TPAcceptEvent event) {

        if(CoreProvider.getApp().getType().equals(AppType.FACTIONS_SPAWN)){
            if(FactionsProvider.getSettings().getSpawnerProtectedZoneCuboid().contains(event.getPlayer().getLocation(), true)){
                Message.ERROR.send(event.getPlayer(), "Você não pode aceitar pedidos de TPA no spawn.");
                event.setCancelled(true);
                return;
            }
        }

        if (CoreProvider.getApp().getType().equals(AppType.FACTIONS_VIP)
                || CoreProvider.getApp().getType().equals(AppType.FACTIONS_SAFE)
                || CoreProvider.getApp().getType().equals(AppType.FACTIONS_WAR)) {
            Message.ERROR.send(event.getPlayer(), "Você não pode aceitar pedidos de TPA na VIP.");
            event.setCancelled(true);
            return;
        }

        FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(event.getPlayer().getName());

        Location location = event.getPlayer().getLocation();

        Claim claim = LandUtils.getClaim(location);

        if (claim == null) {
            return;
        }

        if (!FactionPermission.TPACCEPT.allows(claim.getFaction(), user)) {
            event.setCancelled(true);

            Message.ERROR.send(event.getPlayer(), String.format(
                    "Você não tem permissão para aceitar pedidos de tpa nas terras da facção %s&c.",
                    claim.getFaction().getDisplayName()
            ));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onTPAcceptMonitor(TPAcceptEvent event) {
        try {
            Location location = event.getPlayer().getLocation();

            Claim claim = LandUtils.getClaim(location);

            User target = event.getTarget();

            if (claim == null) {
                return;
            }

            User requester = event.getRequester();

            FactionsProvider.Repositories.TPA_LOG.provide().insertAcceptLog(target, requester, claim, location);

        } catch (Exception ignore) {
        }
    }
}
