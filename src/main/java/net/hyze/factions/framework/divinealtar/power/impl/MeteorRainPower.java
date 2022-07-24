package net.hyze.factions.framework.divinealtar.power.impl;

import net.hyze.core.shared.misc.utils.TimeCode;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.misc.customitem.CustomItemRegistry;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.factions.framework.divinealtar.misc.customitems.MeteorRainItem;
import net.hyze.factions.framework.divinealtar.power.Power;
import net.hyze.factions.framework.divinealtar.power.PowerCurrency;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class MeteorRainPower implements Power {

    @Override
    public String getId() {
        return "meteor_rain";
    }

    @Override
    public String getName() {
        return "Chuva de Meteoros";
    }

    @Override
    public String getGemName() {
        return "Gema da Chuva de Meteoros";
    }

    @Override
    public ItemBuilder getIcon() {
        return ItemBuilder.of(Material.FIREBALL);
    }

    @Override
    public String[] getDescription() {
        return new String[]{
            "Inicia uma chuva de meteoros",
            "na localização que for definida",
            "por você."
        };
    }

    @Override
    public Long rechargeTime() {
        /**
         * 90 minutos.
         */
        return 90L * 60000L;
    }

    @Override
    public Long activeTime() {
        /**
         * 30 segundos.
         */
        return 30L * 1000L;
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

        Player player = Bukkit.getPlayerExact(user.getNick());

        player.getInventory().addItem(CustomItemRegistry.getItem("meteor_rain").asItemStack());

        Message.INFO.send(player, String.format("Você tem %s para utilizar este item.", TimeCode.toText(MeteorRainItem.COOLDOWN, 5)));

    }
}
