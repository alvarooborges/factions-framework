package net.hyze.factions.framework.misc.npc.impl.ranking;

import net.hyze.core.shared.misc.utils.NumberUtils;
import net.hyze.factions.framework.ranking.FactionRankIcon;
import net.hyze.factions.framework.ranking.RankingFactory;
import net.hyze.factions.framework.ranking.factions.ValuationRanking;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ValuationRankingNPC extends RankingNPC {

    private static final String TITLE = "&e&lFACÇÕES MAIS RICAS";

    private final Vector first;
    private final Vector second;
    private final Vector third;

    public ValuationRankingNPC(Vector hologramLocation, Vector lookAt, Vector first, Vector second, Vector third) {
        super(TITLE, hologramLocation, lookAt);

        this.first = first;
        this.second = second;
        this.third = third;
    }

    public ValuationRankingNPC(Vector first, Vector second, Vector third) {
        super(TITLE, first.clone().add(new Vector(0, 3.2, 0)), new Vector(0, 120, 0));

        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Override
    public void populate() {

        ValuationRanking ranking = (ValuationRanking) RankingFactory.FACTIONS_VALUATION_RANKING.getRanking();
        List<FactionRankIcon<ValuationRanking.RankValue>> items = ranking.getItems();

        List<FactionRankIcon<ValuationRanking.RankValue>> top = items.stream()
                .limit(3)
                .collect(Collectors.toCollection(LinkedList::new));

        // FIRST
        {
            if (top.size() > 0) {
                FactionRankIcon<ValuationRanking.RankValue> icon = top.get(0);

                ValuationRanking.RankValue rankingItem = icon.getElement();
                position(this.first, icon.getFaction(), NumberUtils.toK(rankingItem.sum()), 1);
            }
        }

        // SECOND
        {
            if (top.size() > 1) {
                FactionRankIcon<ValuationRanking.RankValue> icon = top.get(1);

                ValuationRanking.RankValue rankingItem = icon.getElement();
                position(this.second, icon.getFaction(), NumberUtils.toK(rankingItem.sum()), 2);
            }
        }

        // THIRD
        {
            if (top.size() > 2) {
                FactionRankIcon<ValuationRanking.RankValue> icon = top.get(2);

                ValuationRanking.RankValue rankingItem = icon.getElement();
                position(this.third, icon.getFaction(), NumberUtils.toK(rankingItem.sum()), 3);
            }
        }
    }

    public Location getPosition(Location relLoc, BlockFace blockFace) {

        Location location = null;
        World world = relLoc.getWorld();
        double x = relLoc.getX();
        double y = relLoc.getY();
        double z = relLoc.getZ();

        switch (blockFace) {
            case SOUTH:
                location = new Location(world, x + relLoc.getX(), y - relLoc.getY(), z + 1);
                break;
            case NORTH:
                location = new Location(world, x - relLoc.getX(), y - relLoc.getY(), z - 1);
                break;
            case WEST:
                location = new Location(world, x - 1, y - relLoc.getY(), z + relLoc.getX());
                break;
            case EAST:
                location = new Location(world, x + 1, y - relLoc.getY(), z - relLoc.getX());
                break;
            default:
                throw new IllegalArgumentException("BlockFace argument error. Use NORTH, SOUTH, EAST or WEST.");
        }

        return location;

    }

}
