package net.hyze.factions.framework.spawners.evolutions.storage.specs;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

@RequiredArgsConstructor
public class SelectLevelIndexSpec extends SelectSqlSpec<Integer> {

    private final String evolutionId;
    private final int factionId;
    private final String spawnerTypeId;

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT `evolution_level_index` FROM `spawners_evolutions` " +
                            "WHERE `evolution_id` = ? AND `faction_id` = ? AND `type` = ? " +
                            "LIMIT 1;"
            );

            statement.setString(1, evolutionId);
            statement.setInt(2, factionId);
            statement.setString(3, spawnerTypeId);

            return statement;
        };
    }

    @Override
    public ResultSetExtractor<Integer> getResultSetExtractor() {
        return (ResultSet result) -> {

            if (result.next()) {
                return result.getInt("evolution_level_index");
            }

            return 0;
        };
    }
}
