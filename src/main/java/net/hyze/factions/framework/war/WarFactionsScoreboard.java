package net.hyze.factions.framework.war;

import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.misc.utils.SequencePrefix;
import net.hyze.core.spigot.misc.scoreboard.IBoardable;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.misc.scoreboard.FactionsScoreboard;
import net.hyze.factions.framework.user.FactionUser;

public class WarFactionsScoreboard extends FactionsScoreboard {

    public WarFactionsScoreboard(FactionUser user) {
        super(user);
    }

    protected String getName(Group group) {
        return getName(group, null);
    }

    protected String getName(Group group, Faction faction) {
        int index = -group.getPriority() + Group.GAME_MASTER.getPriority() + 1;

        String prefix = "zzz";

        SequencePrefix sequence = new SequencePrefix();

        for (int i = 0; i < index; i++) {
            prefix = sequence.next();
        }

        return String.format(
                "%s-%s",
                prefix,
                faction != null ? faction.getId() : 0
        );
    }
}
