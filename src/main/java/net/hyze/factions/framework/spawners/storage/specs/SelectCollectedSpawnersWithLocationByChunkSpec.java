package net.hyze.factions.framework.spawners.storage.specs;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.apps.App;
import net.hyze.factions.framework.FactionsConstants;
import net.hyze.factions.framework.spawners.SpawnerState;
import org.bukkit.Chunk;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;

@RequiredArgsConstructor
public class SelectCollectedSpawnersWithLocationByChunkSpec extends SelectSpawnersSpec {

    private final App app;
    private final Chunk chunk;

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return (Connection connection) -> {
            String query = String.format(
                    "SELECT * FROM `%s` WHERE `app_id` = ? AND `world_name` = ? AND `x` >= ? AND `x` <= ? AND `z` >= ? AND `z` <= ? AND `state` = ?;",
                    FactionsConstants.Databases.Mysql.Tables.SPAWNERS_TABLE_NAME
            );

            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, app.getId());
            statement.setString(2, chunk.getWorld().getName());
            statement.setInt(3, chunk.getX() << 4);
            statement.setInt(4, (chunk.getX() << 4) + 15);
            statement.setInt(5, chunk.getZ() << 4);
            statement.setInt(6, (chunk.getZ() << 4) + 15);
            statement.setString(7, SpawnerState.COLLECTED.name());

            return statement;
        };
    }
}
