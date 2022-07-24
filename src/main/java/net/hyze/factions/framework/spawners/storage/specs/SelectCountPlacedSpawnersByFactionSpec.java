package net.hyze.factions.framework.spawners.storage.specs;

import com.google.common.base.Enums;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import net.hyze.factions.framework.FactionsConstants;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.spawners.SpawnerType;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

@AllArgsConstructor
@RequiredArgsConstructor
public class SelectCountPlacedSpawnersByFactionSpec extends SelectSqlSpec<Map<SpawnerType, Integer>> {

    private final Faction faction;
    private Date before;

    @Override
    public ResultSetExtractor<Map<SpawnerType, Integer>> getResultSetExtractor() {
        return (ResultSet result) -> {
            Map<SpawnerType, Integer> out = Maps.newHashMap();

            while (result.next()) {
                String typeRaw = result.getString("type");
                Integer total = result.getInt("total");

                SpawnerType type = Enums.getIfPresent(SpawnerType.class, typeRaw).orNull();

                if (type != null) {
                    out.put(type, total);
                }
            }

            return out;
        };
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return (Connection connection) -> {
            String query;

            if (before != null) {
                query = String.format(
                        "SELECT `type`, COUNT(*) as 'total' FROM `%s` WHERE `faction_id` = ? AND "
                        + "`state` = 'PLACED' AND `transacted_at` <= ? GROUP BY `type`;",
                        FactionsConstants.Databases.Mysql.Tables.SPAWNERS_TABLE_NAME
                );
            } else {
                query = String.format(
                        "SELECT `type`, COUNT(*) as 'total' FROM `%s` WHERE `faction_id` = ? AND `state` = 'PLACED' GROUP BY `type`;",
                        FactionsConstants.Databases.Mysql.Tables.SPAWNERS_TABLE_NAME
                );
            }

            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, faction.getId());

            if (before != null) {
                statement.setTimestamp(2, new Timestamp(before.getTime()));
            }

            return statement;
        };
    }

}
