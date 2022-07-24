package net.hyze.factions.framework.spawners.storage.specs;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.storage.repositories.specs.UpdateSqlSpec;
import net.hyze.core.shared.world.location.SerializedLocation;
import net.hyze.factions.framework.FactionsConstants;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.spawners.SpawnerState;
import net.hyze.factions.framework.spawners.SpawnerType;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;

@RequiredArgsConstructor
public class InsertPlacedSpawnerSpec extends UpdateSqlSpec<Boolean> {

    private final Faction faction;

    private final SpawnerType type;
    private final SerializedLocation location;

    @Override
    public Boolean parser(int affectedRows) {
        return affectedRows != 0;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return (Connection connection) -> {
            String query = String.format("INSERT INTO `%s` "
                    + "(`faction_id`, `type`, `app_id`, `world_name`, `x`, `y`, `z`, `state`, `transacted_at`) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, now()) "
                    + "ON DUPLICATE KEY UPDATE "
                    + "`faction_id` = VALUES(`faction_id`), `type` = VALUES(`type`), `transacted_at` = VALUES(`transacted_at`);",
                    FactionsConstants.Databases.Mysql.Tables.SPAWNERS_TABLE_NAME
            );

            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, faction.getId());
            statement.setString(2, type.name());
            statement.setString(3, location.getAppId());
            statement.setString(4, location.getWorldName());
            statement.setDouble(5, location.getX());
            statement.setDouble(6, location.getY());
            statement.setDouble(7, location.getZ());
            statement.setString(8, SpawnerState.PLACED.name());

            return statement;
        };
    }
}
