package net.hyze.factions.framework.misc.power;

import net.hyze.core.shared.CoreProvider;
import net.hyze.factions.framework.FactionsPlugin;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.echo.packets.PowerSchedulerUpdatedPacket;
import net.hyze.factions.framework.echo.packets.UserPowerUpdatedPacket;
import net.hyze.factions.framework.user.stats.UserStats;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.stream.Collectors;

public class PowerScheduler {

    public static void start() {
        Bukkit.getScheduler().runTaskTimer(FactionsPlugin.getInstance(), () -> {

            Set<String> onlineNicks = Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName).
                    collect(Collectors.toSet());

            FactionsProvider.Cache.Local.USERS.provide()
                    .getAllPresentByNicks(onlineNicks)
                    .values()
                    .forEach(user -> {
                        long time = PowerManager.get(user.getId());

                        if (user.getStats().getPower() >= user.getStats().getAdditionalMaxPower() + FactionsProvider.getSettings().getMaxPower()) {
                            if (time != 0) {
                                CoreProvider.Redis.ECHO.provide().publish(new PowerSchedulerUpdatedPacket(
                                        user.getId(), 0
                                ));
                            }

                            return;
                        }

                        if (time == 0L) {
                            CoreProvider.Redis.ECHO.provide().publish(new PowerSchedulerUpdatedPacket(
                                    user.getId(), System.currentTimeMillis()
                            ));
                            return;
                        }

                        if (time > System.currentTimeMillis() - FactionsProvider.getSettings().getPowerUpdateDelay()) {
                            return;
                        }

                        int oldPower = user.getStats().getPower();
                        int newPower = user.getStats().increment(UserStats.Field.POWER);

                        FactionsProvider.Repositories.USER_STATS.provide().update(user.getStats(), UserStats.Field.POWER);

                        CoreProvider.Redis.ECHO.provide().publish(new UserPowerUpdatedPacket(user.getId(), oldPower, newPower));
                        CoreProvider.Redis.ECHO.provide().publish(new PowerSchedulerUpdatedPacket(
                                user.getId(), System.currentTimeMillis()
                        ));
                    });

        }, 0L, 20L);
    }
}
