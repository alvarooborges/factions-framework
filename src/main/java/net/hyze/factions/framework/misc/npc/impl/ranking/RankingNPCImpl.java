package net.hyze.factions.framework.misc.npc.impl.ranking;

import net.hyze.core.shared.CoreConstants;
import net.hyze.core.spigot.misc.npc.PlayerCustomNPC;
import org.bukkit.Location;

public class RankingNPCImpl extends PlayerCustomNPC {

    public RankingNPCImpl(String skinValue, String skinSign, Location npcLocation) {
        super(skinValue, skinSign, npcLocation);

        setId("rank" + CoreConstants.RANDOM.nextDouble());
    }
}
