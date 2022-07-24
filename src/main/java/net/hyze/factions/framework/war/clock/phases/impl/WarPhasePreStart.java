package net.hyze.factions.framework.war.clock.phases.impl;

import net.hyze.core.shared.messages.MessageUtils;
import net.hyze.core.spigot.misc.utils.Title;
import net.hyze.factions.framework.war.clock.phases.AbstractWarPhase;
import org.bukkit.Bukkit;
import org.bukkit.Sound;

public class WarPhasePreStart extends AbstractWarPhase {

    public WarPhasePreStart() {
        super("Pré-evento", 10, false);
    }

    @Override
    public void onStart() {

        Bukkit.broadcastMessage(MessageUtils.translateColorCodes("\n&eEvento fechado! Quem não entrou, não entra mais.\n "));

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1, 1);
        });

    }

    @Override
    public void onMeantime(Integer second) {

        Title.builder().fadeIn(20).fadeOut(20).stay(20).subTitle("&a&l" + second).build().send();
        
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 5, 1);
        });
    }

    @Override
    public void onEnd() {

    }

}
