package net.hyze.factions.framework.tasks;

import net.hyze.core.spigot.misc.utils.LocationUtils;
import net.hyze.factions.framework.FactionsProvider;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.Player;

public class SeeChunksTask implements Runnable {

    @Override
    public void run() {

        final long now = System.currentTimeMillis();
        final long length = 500;

        final long period = now / length;

        final int steps = 1;
        final int step = (int) (period % steps);

        final float offsetX = 0.0f;
        final float offsetY = 2;
        final float offsetZ = 0.0f;
        final float speed = 0;
        final int amount = 18;

        Bukkit.getOnlinePlayers().stream()
                .map(player -> FactionsProvider.Cache.Local.USERS.provide().getIfPresent(player.getName()))
                .filter(user -> user.getOptions().isSeeChunksEnabled())
                .forEach(user -> {
                    Player player = user.getPlayer();

                    LocationUtils.getChunkBoundingBoxLocations(player.getLocation(), steps, step)
                            .forEach(location -> {
                                player.spigot().playEffect(location, Effect.EXPLOSION, amount, 0, offsetX, offsetY, offsetZ, speed, amount, amount);
                                player.spigot().playEffect(location, Effect.FLAME, amount, 0, offsetX, offsetY, offsetZ, speed, amount, amount);
                            });
                });
    }
}
