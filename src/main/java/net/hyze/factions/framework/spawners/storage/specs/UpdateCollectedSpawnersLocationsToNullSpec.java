package net.hyze.factions.framework.spawners.storage.specs;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.storage.repositories.specs.UpdateSqlSpec;
import net.hyze.core.shared.world.location.SerializedLocation;
import net.hyze.factions.framework.FactionsConstants;
import net.hyze.factions.framework.spawners.SpawnerState;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

@RequiredArgsConstructor
public class UpdateCollectedSpawnersLocationsToNullSpec extends UpdateSqlSpec<Void> {

    private final List<SerializedLocation> locations;

    @Override
    public Void parser(int affectedRows) {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return (Connection connection) -> {
            StringBuilder builder = new StringBuilder();

            builder.append(String.format(
                    "UPDATE `%s` SET `app_id` = NULL, `world_name` = NULL, `x` = NULL, `y` = NULL, `z` = NULL, `transacted_at` = now() WHERE ",
                    FactionsConstants.Databases.Mysql.Tables.SPAWNERS_TABLE_NAME
            ));

            locations.forEach(location -> {
                builder.append(String.format(
                        "(`app_id`='%s' AND `world_name`='%s' AND `x`='%s' AND `y`='%s' AND `z`='%s' AND `state`='%s') OR",
                        location.getAppId(),
                        location.getWorldName(),
                        location.getX(),
                        location.getY(),
                        location.getZ(),
                        SpawnerState.COLLECTED.name()
                ));
            });

            builder.setLength(builder.length() - 2);

            builder.append(String.format(
                    "LIMIT %s;",
                    locations.size()
            ));

            return connection.prepareStatement(builder.toString(), Statement.RETURN_GENERATED_KEYS);
        };
    }
}
