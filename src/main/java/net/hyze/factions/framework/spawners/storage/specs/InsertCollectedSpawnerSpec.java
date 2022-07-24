package net.hyze.factions.framework.spawners.storage.specs;

import com.google.common.collect.Multimap;
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

@RequiredArgsConstructor
public class InsertCollectedSpawnerSpec extends UpdateSqlSpec<Integer> {

    private final Faction faction;

    private final Multimap<SpawnerType, SerializedLocation> spawners;

    @Override
    public Integer parser(int affectedRows) {
        return affectedRows;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return (Connection connection) -> {

            StringBuilder builder = new StringBuilder();

            builder.append(
                    String.format(
                            "INSERT INTO `%s` (`faction_id`, `type`, `app_id`, `world_name`, `x`, `y`, `z`, `state`, `transacted_at`) VALUES ",
                            FactionsConstants.Databases.Mysql.Tables.SPAWNERS_TABLE_NAME
                    )
            );

            spawners.asMap().forEach((type, locations) -> {

                locations.forEach(location -> {
                    builder.append(String.format(
                            " ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', now()),",
                            faction.getId(),
                            type.name(),
                            location.getAppId(),
                            location.getWorldName(),
                            location.getX(),
                            location.getY(),
                            location.getZ(),
                            SpawnerState.COLLECTED.name()
                    ));
                });

            });

            builder.setLength(builder.length() - 1);

            builder.append(
                    " ON DUPLICATE KEY UPDATE "
                    + "`faction_id` = VALUES(`faction_id`), "
                    + "`type` = VALUES(`type`), "
                    + "`state` = VALUES(`state`);"
            );

            PreparedStatement statement = connection.prepareStatement(builder.toString(), Statement.RETURN_GENERATED_KEYS);

            return statement;
        };
    }
}
