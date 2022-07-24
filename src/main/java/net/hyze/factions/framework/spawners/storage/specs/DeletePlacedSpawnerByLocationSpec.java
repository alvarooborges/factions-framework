package net.hyze.factions.framework.spawners.storage.specs;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.storage.repositories.specs.DeleteSqlSpec;
import net.hyze.core.shared.world.location.SerializedLocation;
import net.hyze.factions.framework.FactionsConstants;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

@RequiredArgsConstructor
public class DeletePlacedSpawnerByLocationSpec extends DeleteSqlSpec<Boolean> {

    @NonNull
    private final SerializedLocation location;

    @Override
    public Boolean parser(int affectedRows) {
        return affectedRows > 0;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return (Connection connection) -> {
            String query = String.format(
                    "DELETE FROM `%s` WHERE `app_id` = ? AND `world_name` = ? AND `x` = ? AND `y` = ? AND `z` = ? AND `state` = 'PLACED' LIMIT 1;",
                    FactionsConstants.Databases.Mysql.Tables.SPAWNERS_TABLE_NAME
            );

            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, location.getAppId());
            statement.setString(2, location.getWorldName());
            statement.setDouble(3, location.getX());
            statement.setDouble(4, location.getY());
            statement.setDouble(5, location.getZ());

            return statement;
        };
    }

}
