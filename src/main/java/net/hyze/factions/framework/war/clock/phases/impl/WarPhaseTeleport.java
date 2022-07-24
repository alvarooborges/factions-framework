package net.hyze.factions.framework.war.clock.phases.impl;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.echo.packets.user.connect.ConnectReason;
import net.hyze.core.spigot.misc.utils.TeleportManager;
import net.hyze.factions.framework.war.clock.phases.AbstractWarPhase;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;

public class WarPhaseTeleport extends AbstractWarPhase {

    public WarPhaseTeleport() {
        super("Teleportando", 10, false);
    }

    @Override
    public void onStart() {

        Bukkit.getOnlinePlayers().stream()
                .filter(player -> !player.getGameMode().equals(GameMode.ADVENTURE))
                .forEach(
                        player -> {
                            TeleportManager.teleport(
                                    CoreProvider.Cache.Local.USERS.provide().get(player.getName()),
                                    AppType.FACTIONS_SPAWN,
                                    ConnectReason.RESPAWN,
                                    "&aFim do evento Guerra."
                            );
                        }
                );

    }

    @Override
    public void onMeantime(Integer second) {

    }

    @Override
    public void onEnd() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.kickPlayer("Fim do evento Guerra.");
        });
    }

}
