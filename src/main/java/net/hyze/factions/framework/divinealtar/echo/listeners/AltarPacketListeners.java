package net.hyze.factions.framework.divinealtar.echo.listeners;

import dev.utils.echo.IEchoListener;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.misc.combat.CombatManager;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.divinealtar.echo.packets.ThunderstormPacket;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.misc.utils.FactionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.greenrobot.eventbus.Subscribe;

public class AltarPacketListeners implements IEchoListener {

    @Subscribe
    public void on(ThunderstormPacket packet) {

        Faction faction = FactionsProvider.Cache.Local.FACTIONS.provide().get(packet.getFactionSenderId());

        FactionUtils.getUsers(faction, true, true).forEach(user -> {

            if (!CombatManager.isTagged(user.getHandle())) {
                return;
            }

            CombatManager.getOpponents(user.getHandle()).forEach(targetId -> {

                User targetUser = CoreProvider.Cache.Local.USERS.provide().get(targetId);

                Player player = Bukkit.getPlayerExact(targetUser.getNick());

                if (player != null) {

                    player.getLocation().getWorld().strikeLightningEffect(player.getLocation());
                    player.damage(3d);
                    
                }

            });

        });

    }

}
