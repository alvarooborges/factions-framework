package net.hyze.factions.framework.divinealtar.power.impl;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.FactionsPlugin;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.divinealtar.echo.packets.ThunderstormPacket;
import net.hyze.factions.framework.divinealtar.power.Power;
import net.hyze.factions.framework.divinealtar.power.PowerCurrency;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.atomic.AtomicInteger;

public class ThunderstormPower implements Power {

    @Override
    public String getId() {
        return "thunderstorm";
    }

    @Override
    public String getName() {
        return "Tempestade de Raios";
    }

    @Override
    public String getGemName() {
        return "Gema de Tempestade de Raios";
    }

    @Override
    public ItemBuilder getIcon() {
        return ItemBuilder.of(Material.BLAZE_ROD);
    }

    @Override
    public String[] getDescription() {
        return new String[]{
            "Inicie uma tempestade de",
            "raios que atinge todos que",
            "estiverem em combate com",
            "a sua facção."
        };
    }

    @Override
    public Long rechargeTime() {
        /**
         * 30 minutos.
         */
        return 30L * 60000L;
    }

    @Override
    public Long activeTime() {
        /**
         * 2 minutos.
         */
        return 2L * 60000L;
    }

    @Override
    public PowerCurrency getCurrency() {
        return PowerCurrency.GEM;
    }

    @Override
    public Integer getPrice() {
        return 1;
    }

    @Override
    public void onActivate(User user) {

        FactionUser factionUser = FactionsProvider.Cache.Local.USERS.provide().get(user.getId());
        FactionUserRelation relation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getByUserId(factionUser.getId());

        AtomicInteger seconds = new AtomicInteger((int) (activeTime() / 1000));

        new BukkitRunnable() {
            @Override
            public void run() {
                CoreProvider.Redis.ECHO.provide().publish(
                        new ThunderstormPacket(relation.getFaction().getId())
                );

                if (seconds.getAndDecrement() <= 0) {
                    this.cancel();
                }
            }
        }.runTaskTimer(FactionsPlugin.getInstance(), 20L, 20L);

    }
}
