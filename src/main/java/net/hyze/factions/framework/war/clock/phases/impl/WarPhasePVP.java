package net.hyze.factions.framework.war.clock.phases.impl;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.misc.utils.Title;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.war.WarUtils;
import net.hyze.factions.framework.war.clock.phases.AbstractWarPhase;
import org.bukkit.Bukkit;
import org.bukkit.Sound;

public class WarPhasePVP extends AbstractWarPhase {

    public WarPhasePVP() {
        super("PVP", 2700, true);
    }

    @Override
    public void onStart() {

        FactionsProvider.getSettings().setAllyFire(true);

        Title.builder().fadeIn(20).fadeOut(20).stay(20).title("&c&lFIGHT!").subTitle("&7PVP liberado!").build().send();

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.playSound(player.getLocation(), Sound.HORSE_DEATH, 5, 1);
        });

        /**
         * Colocando todo mundo em combate.
         */
        Bukkit.getOnlinePlayers().forEach(
                player -> {
                    User user = CoreProvider.Cache.Local.USERS.provide().get(player.getName());

                    if (user.hasGroup(Group.MODERATOR)) {
                        return;
                    }

                    //CombatManager.tag(user);
                }
        );

        WarUtils.check();

    }

    @Override
    public void onMeantime(Integer second) {
        WarUtils.check();
    }

    @Override
    public void onEnd() {
        WarUtils.check();
    }

}
