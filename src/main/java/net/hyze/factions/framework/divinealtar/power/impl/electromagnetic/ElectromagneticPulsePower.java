package net.hyze.factions.framework.divinealtar.power.impl.electromagnetic;

import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.divinealtar.power.Power;
import net.hyze.factions.framework.divinealtar.power.PowerCurrency;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import org.bukkit.Material;

public class ElectromagneticPulsePower implements Power {

    @Override
    public String getId() {
        return "electromagnetic_pulse";
    }

    @Override
    public String getName() {
        return "Pulso Eletromagnético";
    }

    @Override
    public String getGemName() {
        return "Gema de Pulso Eletromagnético";
    }

    @Override
    public ItemBuilder getIcon() {
        return ItemBuilder.of(Material.REDSTONE);
    }

    @Override
    public String[] getDescription() {
        return new String[]{
            "Desativa o funcionamento da",
            "redstone em um raio de 30 blocos",
            "do canhão que está atacando a",
            "sua base."
        };
    }

    @Override
    public Long rechargeTime() {
        /**
         * 60 minutos.
         */
        return 60L * 60000L;
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

        FactionUserRelation relation = FactionsProvider.Cache.Local.USERS_RELATIONS.provide().getByUserId(user.getId());

        ElectromagneticPulseManager.active(relation.getFaction().getId());

    }

}
