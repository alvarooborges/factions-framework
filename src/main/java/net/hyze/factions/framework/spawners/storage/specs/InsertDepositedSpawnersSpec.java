package net.hyze.factions.framework.spawners.storage.specs;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.storage.repositories.specs.UpdateSqlSpec;
import net.hyze.factions.framework.FactionsConstants;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.spawners.SpawnerType;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Map;

@RequiredArgsConstructor
public class InsertDepositedSpawnersSpec extends UpdateSqlSpec<Boolean> {

    private final Faction faction;
    private final Map<SpawnerType, Integer> spawners;

    @Override
    public Boolean parser(int affectedRows) {
        return affectedRows == spawners.size();
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return (Connection connection) -> {

            String query = String.format(
                    "INSERT INTO `%s` (`faction_id`, `type`, `state`, `transacted_at`) VALUES ",
                    FactionsConstants.Databases.Mysql.Tables.SPAWNERS_TABLE_NAME
            );

            for (Map.Entry<SpawnerType, Integer> entry : spawners.entrySet()) {
                for (int i = 0; i < entry.getValue(); i++) {
                    query += String.format("(%s, '%s', 'COLLECTED', now()),", faction.getId(), entry.getKey().name());
                }
            }

            query = query.substring(0, query.length() - 1);

            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            return statement;
        };
    }

}
