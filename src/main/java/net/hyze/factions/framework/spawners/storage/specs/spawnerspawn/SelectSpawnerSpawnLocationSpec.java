package net.hyze.factions.framework.spawners.storage.specs.spawnerspawn;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import net.hyze.core.shared.world.location.SerializedLocation;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.spawners.SpawnerType;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.PreparedStatement;

@RequiredArgsConstructor
public class SelectSpawnerSpawnLocationSpec extends SelectSqlSpec<SerializedLocation> {

    private final Faction faction;
    private final SpawnerType type;

    @Override
    public ResultSetExtractor<SerializedLocation> getResultSetExtractor() {
        return result -> {
            if (result.next()) {
                try {
                    String rawLocation = result.getString("location");

                    return SerializedLocation.of(rawLocation);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return null;
        };
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {

            String query = "SELECT `location` FROM `factions_spawners_spawns` "
                    + "WHERE `faction_id` = ? AND `type` = ? LIMIT 1;";

            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, faction.getId());
            statement.setString(2, type.name());

            return statement;
        };
    }

}
