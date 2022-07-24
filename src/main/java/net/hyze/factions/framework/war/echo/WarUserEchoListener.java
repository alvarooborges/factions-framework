package net.hyze.factions.framework.war.echo;

import dev.utils.echo.IEchoListener;
import net.hyze.core.shared.apps.AppType;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.echo.packets.UserLeftFactionPacket;
import net.hyze.factions.framework.user.FactionUser;
import org.greenrobot.eventbus.Subscribe;

public class WarUserEchoListener implements IEchoListener {

    @Subscribe
    public void on(UserLeftFactionPacket packet) {

        if (AppType.FACTIONS_WAR.isCurrent()) {
            FactionUser user = FactionsProvider.Cache.Local.USERS.provide().getIfPresent(packet.getUserId());

            if (user != null) {
                if (user.isOnline()) {
                    user.getPlayer().kickPlayer("Você saiu da facção durante a Guerra.");
                }
            }
        }
    }

}
