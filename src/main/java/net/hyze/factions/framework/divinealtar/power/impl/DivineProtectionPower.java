package net.hyze.factions.framework.divinealtar.power.impl;

import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.divinealtar.power.Power;
import net.hyze.factions.framework.divinealtar.power.PowerCurrency;
import org.bukkit.Material;

public class DivineProtectionPower implements Power {

    @Override
    public String getId() {
        return "devine_protection";
    }

    @Override
    public String getName() {
        return "Proteção Divina";
    }

    @Override
    public String getGemName() {
        return "Gema da Proteção Divina";
    }

    @Override
    public ItemBuilder getIcon() {
        return ItemBuilder.of(Material.CHAINMAIL_CHESTPLATE);
    }

    @Override
    public String[] getDescription() {
        return new String[]{
            "Dobra a proteção dos Blocos",
            "de Proteção colocados em",
            "seus terrenos."
        };
    }

    @Override
    public Long rechargeTime() {
        /**
         * 1 horas.
         */
        return 1L * (60L * 60000L);
    }

    @Override
    public Long activeTime() {
        /**
         * 10 minutos.
         */
        return 10L * 60000L;
    }

    @Override
    public PowerCurrency getCurrency() {
        return PowerCurrency.GEM;
    }

    @Override
    public Integer getPrice() {
        return 1;
    }

}
