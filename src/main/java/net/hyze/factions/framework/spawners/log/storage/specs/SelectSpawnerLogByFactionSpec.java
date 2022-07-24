package net.hyze.factions.framework.spawners.log.storage.specs;

import com.google.common.base.Enums;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.spawners.SpawnerType;
import net.hyze.factions.framework.spawners.log.LogAction;
import net.hyze.factions.framework.spawners.log.LogSourceType;
import net.hyze.factions.framework.spawners.log.SpawnerLog;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.PreparedStatement;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
public class SelectSpawnerLogByFactionSpec extends SelectSqlSpec<List<SpawnerLog>> {

    private final Faction faction;

    @Override
    public ResultSetExtractor<List<SpawnerLog>> getResultSetExtractor() {
        return result -> {
            List<SpawnerLog> out = Lists.newLinkedList();

            while (result.next()) {

                try {
                    LogSourceType type = Enums.getIfPresent(LogSourceType.class, result.getString("type")).orNull();
                    String typeValue = result.getString("type_value");
                    LogAction action = Enums.getIfPresent(LogAction.class, result.getString("action")).orNull();
                    SpawnerType spawnerType = Enums.getIfPresent(SpawnerType.class, result.getString("spawner_type")).orNull();
                    Integer amount = result.getInt("amount");
                    Date date = new Date(result.getTimestamp("date").getTime());

                    if (type != null && action != null && spawnerType != null) {
                        out.add(new SpawnerLog(result.getInt("id"), faction, type, typeValue, action, spawnerType, amount, date));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            return out;
        };
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {
            String query = "SELECT * FROM `spawners_log` WHERE `faction_id` = ? AND `date` > DATE_SUB(now(), INTERVAL 3 DAY) LIMIT 1000;";

            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, faction.getId());

            return statement;
        };
    }

}
