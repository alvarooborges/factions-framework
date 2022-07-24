package net.hyze.factions.framework.spawners.storage.specs;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.storage.repositories.specs.DeleteSqlSpec;
import net.hyze.factions.framework.FactionsConstants;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.spawners.SpawnerState;
import net.hyze.factions.framework.spawners.SpawnerType;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

@RequiredArgsConstructor
public class DeleteCollectedSpawnersByTypeSpec extends DeleteSqlSpec<Integer> {

    @NonNull
    private final Faction faction;

    @NonNull
    private final SpawnerType type;

    private final int limit;

    @Override
    public Integer parser(int affectedRows) {
        return affectedRows;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return (Connection connection) -> {
            StringBuilder builder = new StringBuilder(String.format("DELETE FROM `%s` WHERE `faction_id` = ? AND `type` = ? AND `state` = '%s'",
                    FactionsConstants.Databases.Mysql.Tables.SPAWNERS_TABLE_NAME,
                    SpawnerState.COLLECTED.name()
            ));

            if (limit > -1) {
                builder.append(" LIMIT ?");
            }

            PreparedStatement statement = connection.prepareStatement(builder.toString(), Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, faction.getId());
            statement.setString(2, type.name());

            if (limit > -1) {
                statement.setInt(3, limit);
            }

            return statement;
        };
    }

}
