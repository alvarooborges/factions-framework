package net.hyze.factions.framework.spawners.storage.specs;

import lombok.NonNull;
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
import java.sql.Statement;
import java.util.List;

@RequiredArgsConstructor
public class UpdateCollectedSpawnersToPlacedByLocationSpec extends UpdateSqlSpec<Integer> {

    @NonNull
    private final Faction faction;

    @NonNull
    private final SpawnerType type;

    @NonNull
    private final List<SerializedLocation> locations;

    @Override
    public Integer parser(int affectedRows) {
        return affectedRows;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return (Connection connection) -> {
            StringBuilder builder = new StringBuilder(String.format(
                    "UPDATE `%s` SET `state` = ?, `transacted_at` = now() WHERE `faction_id` = ? AND `type` = ? AND `state` = ? AND (",
                    FactionsConstants.Databases.Mysql.Tables.SPAWNERS_TABLE_NAME
            ));

            locations.forEach(location -> {
                builder.append(String.format(
                        "(`app_id`='%s' AND `world_name`='%s' AND `x`='%s' AND `y`='%s' AND `z`='%s') OR",
                        location.getAppId(),
                        location.getWorldName(),
                        location.getX(),
                        location.getY(),
                        location.getZ()
                ));
            });

            builder.setLength(builder.length() - 2);

            builder.append(String.format(
                    ") LIMIT %s",
                    locations.size()
            ));

            PreparedStatement statement = connection.prepareStatement(builder.toString(), Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, SpawnerState.PLACED.name());
            statement.setInt(2, faction.getId());
            statement.setString(3, type.name());
            statement.setString(4, SpawnerState.COLLECTED.name());

            return statement;
        };
    }

}
