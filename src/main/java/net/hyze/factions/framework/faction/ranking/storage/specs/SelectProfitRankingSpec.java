package net.hyze.factions.framework.faction.ranking.storage.specs;

import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.ranking.WeeklyProfitItem;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.PreparedStatement;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class SelectProfitRankingSpec extends SelectSqlSpec<List<WeeklyProfitItem>> {

    private final int limit;

    @Override
    public ResultSetExtractor<List<WeeklyProfitItem>> getResultSetExtractor() {
        return result -> {
            List<WeeklyProfitItem> out = Lists.newArrayList();

            while (result.next()) {
                int factionId = result.getInt("faction_id");
                double value = result.getDouble("total");

                Faction faction = FactionsProvider.Cache.Local.FACTIONS.provide().get(factionId);

                if (faction != null) {
                    out.add(new WeeklyProfitItem(faction, value));
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
            String query = "SELECT `faction_id`, sum(`value`) as 'total' FROM `ranking_profit` " +
                    "GROUP BY `faction_id` " +
                    "ORDER BY total DESC LIMIT ?;";

            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, limit);

            return statement;
        };
    }

}
