package net.hyze.factions.framework.faction.ranking.storage.specs;

import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.ranking.DailyValuationItem;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.PreparedStatement;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class SelectValuationRankingSpec extends SelectSqlSpec<List<DailyValuationItem>> {

    private final int limit;

    private String date;

    public SelectValuationRankingSpec(int limit, String date) {
        this.limit = limit;
        this.date = date;
    }

    @Override
    public ResultSetExtractor<List<DailyValuationItem>> getResultSetExtractor() {
        return result -> {
            List<DailyValuationItem> out = Lists.newArrayList();

            while (result.next()) {
                int factionId = result.getInt("faction_id");
                double value = result.getDouble("value");

                Faction faction = FactionsProvider.Cache.Local.FACTIONS.provide().get(factionId);

                //if (faction != null) {
                    out.add(new DailyValuationItem(faction, value));
                //}
            }

            return out.stream()
                    .sorted((o1, o2) -> Doubles.compare(o2.getValue(), o1.getValue()))
                    .collect(Collectors.toCollection(LinkedList::new));
        };
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {

            if (this.date == null) {
                String query = "SELECT * FROM `ranking_value` WHERE `date` = CURDATE() ORDER BY `ranking_value`.`value` DESC LIMIT ?;";

                PreparedStatement statement = connection.prepareStatement(query);

                statement.setInt(1, this.limit);

                return statement;
            }

            String query = "SELECT * FROM `ranking_value` WHERE `date` = ? ORDER BY `ranking_value`.`value` DESC LIMIT ?;";

            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, this.date);
            statement.setInt(2, this.limit);

            return statement;
        };
    }

}
