package net.hyze.factions.framework.ranking.factions.coins;

import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import net.hyze.economy.Currency;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.PreparedStatement;
import java.util.Map;

@RequiredArgsConstructor
public class FetchCurrencyRankSpec extends SelectSqlSpec<Map<Integer, Double>> {

    private final Currency currency;

    @Override
    public ResultSetExtractor<Map<Integer, Double>> getResultSetExtractor() {
        return result -> {
            Map<Integer, Double> out = Maps.newLinkedHashMap();

            while (result.next()) {
                out.put(result.getInt("faction_id"), result.getDouble("total"));
            }

            return out;
        };
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {
            String query = "SELECT `faction_users`.`faction_id`, SUM(`economy`.`value`) as 'total' " +
                    "FROM `faction_users` " +
                    "JOIN `economy` ON `economy`.`user_id` = `faction_users`.`user_id`" +
                    "WHERE `economy`.`currency` = ? " +
                    "GROUP BY `faction_users`.`faction_id` " +
                    "ORDER BY total DESC " +
                    "LIMIT 150";

            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, currency.name());

            return statement;
        };
    }
}
