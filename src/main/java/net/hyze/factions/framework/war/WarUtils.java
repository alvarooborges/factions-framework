package net.hyze.factions.framework.war;

import com.google.common.collect.Sets;
import net.hyze.core.shared.group.Group;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.user.FactionUser;
import net.hyze.factions.framework.war.clock.phases.EnumWarPhase;
import net.hyze.factions.framework.war.clock.phases.impl.WarPhaseEnding;
import org.bukkit.Bukkit;

import java.util.Set;

public class WarUtils {

    public static Set<Integer> getFactions() {
        Set<Integer> factions = Sets.newHashSet();

        Bukkit.getOnlinePlayers().stream()
                .filter(player -> !player.isDead())
                .forEach(player -> {
                    FactionUser user = FactionsProvider.Cache.Local.USERS.provide().get(player);

                    if (!user.getHandle().hasGroup(Group.MODERATOR) && user.getRelation() != null) {
                        factions.add(user.getRelation().getFaction().getId());
                    }
                });

        return factions;
    }

    public static void check() {
        Set<Integer> factions = WarUtils.getFactions();

        if (factions.size() <= 1) {
            if (factions.iterator().hasNext()) {
                WarPhaseEnding.FACTION = FactionsProvider.Cache.Local.FACTIONS.provide().get(factions.iterator().next());
            }

            War.CLOCK.stop();
            War.CLOCK.setIndex(EnumWarPhase.ENDING.ordinal());
            War.CLOCK.next();
        }
    }

}
