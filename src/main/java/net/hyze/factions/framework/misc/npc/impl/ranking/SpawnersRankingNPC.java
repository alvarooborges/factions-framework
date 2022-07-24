package net.hyze.factions.framework.misc.npc.impl.ranking;

import net.hyze.core.shared.misc.utils.NumberUtils;
import net.hyze.factions.framework.ranking.FactionRankIcon;
import net.hyze.factions.framework.ranking.RankingFactory;
import net.hyze.factions.framework.ranking.factions.SpawnersRanking;
import org.bukkit.util.Vector;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class SpawnersRankingNPC extends RankingNPC {

    private static final String TITLE = "&e&lRANKING DE GERADORES";

    private final Vector first;
    private final Vector second;
    private final Vector third;

    public SpawnersRankingNPC(Vector hologramLocation, Vector lookAt, Vector first, Vector second, Vector third) {
        super(TITLE, hologramLocation, lookAt);

        this.first = first;
        this.second = second;
        this.third = third;
    }

    public SpawnersRankingNPC(Vector first, Vector second, Vector third) {
        super(TITLE, first.clone().add(new Vector(0, 3.2, 0)), new Vector(0, 120, 0));

        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Override
    public void populate() {

        SpawnersRanking ranking = (SpawnersRanking) RankingFactory.FACTIONS_SPAWNERS_RANKING.getRanking();
        List<FactionRankIcon<SpawnersRanking.RankValue>> items = ranking.getItems();

        List<FactionRankIcon<SpawnersRanking.RankValue>> top = items.stream()
                .limit(3)
                .collect(Collectors.toCollection(LinkedList::new));

        /*
         * FIRST.
         */
        {
            if (top.size() > 0) {
                FactionRankIcon<SpawnersRanking.RankValue> icon = top.get(0);
                position(this.first, icon.getFaction(), NumberUtils.toK(icon.getElement().calculateValue()), 1);
            }
        }

        /*
         * SECOND.
         */
        {
            if (top.size() > 1) {
                FactionRankIcon<SpawnersRanking.RankValue> icon = top.get(1);
                position(this.second, icon.getFaction(), NumberUtils.toK(icon.getElement().calculateValue()), 2);
            }
        }

        /*
         * THIRD.
         */
        {
            if (top.size() > 2) {
                FactionRankIcon<SpawnersRanking.RankValue> icon = top.get(2);
                position(this.third, icon.getFaction(), NumberUtils.toK(icon.getElement().calculateValue()), 3);
            }
        }

    }

}
