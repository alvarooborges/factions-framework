package net.hyze.factions.framework.faction.ranking.storage.specs;

import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import lombok.AllArgsConstructor;
import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.ranking.DailyTycoonItem;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class SelectSellRankingSpec extends SelectSqlSpec<List<DailyTycoonItem>> {

    private final int limit;

    private final Date start;
    private final Date end;

    public SelectSellRankingSpec(int limit) {
        this.limit = limit;

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, -25);

        this.start = calendar.getTime(); // now - 25 hours
        this.end = new Date(); // now
    }

    @Override
    public ResultSetExtractor<List<DailyTycoonItem>> getResultSetExtractor() {
        return result -> {
            List<DailyTycoonItem> out = Lists.newArrayList();

            while (result.next()) {
                int factionId = result.getInt("faction_id");
                double value = result.getDouble("total");

                Faction faction = FactionsProvider.Cache.Local.FACTIONS.provide().get(factionId);

                if (faction != null) {
                    out.add(new DailyTycoonItem(faction, value));
                }
            }

            return out.stream()
                    .sorted((o1, o2) -> Doubles.compare(o2.getValue(), o1.getValue()))
                    .collect(Collectors.toCollection(LinkedList::new));
        };
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {

            String query = "SELECT `faction_id`, sum(`value`) as 'total' " +
                    "FROM `ranking_sell` " +
                    "WHERE `date` BETWEEN ? AND ? " +
                    "GROUP BY `faction_id` " +
                    "ORDER BY total DESC LIMIT ?;";

            PreparedStatement statement = connection.prepareStatement(query);

            statement.setTimestamp(1, new Timestamp(start.getTime()));
            statement.setTimestamp(2, new Timestamp(end.getTime()));
            statement.setInt(3, limit);

            return statement;
        };
    }

}
