package net.hyze.factions.framework.echo.listeners;

import dev.utils.echo.IEchoListener;
import net.hyze.factions.framework.echo.packets.permission.FactionAllyPermissionUpdatedPacket;
import net.hyze.factions.framework.echo.packets.permission.FactionRolePermissionUpdatedPacket;
import net.hyze.factions.framework.echo.packets.permission.FactionUserPermissionUpdatedPacket;
import net.hyze.factions.framework.misc.utils.FactionUtils;
import org.greenrobot.eventbus.Subscribe;

public class FactionPermissionEchoListener implements IEchoListener {

    @Subscribe
    public void on(FactionAllyPermissionUpdatedPacket packet) {
        FactionUtils.updateAllyPermission(packet);
    }

    @Subscribe
    public void on(FactionRolePermissionUpdatedPacket packet) {
        FactionUtils.updateRolePermission(packet);
    }

    @Subscribe
    public void on(FactionUserPermissionUpdatedPacket packet) {
        FactionUtils.updateUserPermission(packet);
    }
}
