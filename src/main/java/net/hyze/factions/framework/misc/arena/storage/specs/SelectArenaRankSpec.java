package net.hyze.factions.framework.misc.arena.storage.specs;

import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import net.hyze.factions.framework.FactionsConstants;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

@RequiredArgsConstructor
public class SelectArenaRankSpec extends SelectSqlSpec<HashMap<Integer, Integer>> {

    @Override
    public ResultSetExtractor<HashMap<Integer, Integer>> getResultSetExtractor() {
        return (ResultSet result) -> {

            HashMap<Integer, Integer> cache = Maps.newHashMap();

            while (result.next()) {

                cache.put(
                        result.getInt("user_id"),
                        result.getInt("total_kills")
                );

            }

            return cache;

        };
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        String query = "SELECT *, COUNT(id) as total_kills FROM %s WHERE created_at > DATE_SUB(now(), INTERVAL 30 DAY) GROUP BY user_id ORDER BY total_kills DESC LIMIT 10;";

        return (Connection con) -> {
            PreparedStatement statement = con.prepareStatement(
                    String.format(
                            query,
                            FactionsConstants.Databases.Mysql.Tables.ARENA_KILLS_TABLE_NAME
                    )
            );

            return statement;
        };
    }

}
