package net.hyze.factions.framework.spawners.storage.specs;

import com.google.common.base.Enums;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import net.hyze.factions.framework.FactionsConstants;
import net.hyze.factions.framework.spawners.SpawnerType;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CalculateSpawnerRankSpec extends SelectSqlSpec<Table<Integer, SpawnerType, Integer>> {

    @Override
    public ResultSetExtractor<Table<Integer, SpawnerType, Integer>> getResultSetExtractor() {
        return (ResultSet result) -> {
            Table<Integer, SpawnerType, Integer> out = HashBasedTable.create();

            while (result.next()) {
                Integer factionId = result.getInt("faction_id");
                String typeRaw = result.getString("type");
                Integer total = result.getInt("total");

                SpawnerType type = Enums.getIfPresent(SpawnerType.class, typeRaw).orNull();

                if (type != null) {
                    out.put(factionId, type, total);
                }
            }

            return out;
        };
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {

            String query = String.format(
                    "SELECT `faction_id`, `type`, COUNT(*) as 'total' FROM `%s` GROUP BY `faction_id`, `type`;",
                    FactionsConstants.Databases.Mysql.Tables.SPAWNERS_TABLE_NAME
            );

            PreparedStatement statement = connection.prepareStatement(query);

            return statement;
        };
    }

}
