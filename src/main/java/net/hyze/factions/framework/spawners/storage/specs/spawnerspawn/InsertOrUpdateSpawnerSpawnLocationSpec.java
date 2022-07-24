package net.hyze.factions.framework.spawners.storage.specs.spawnerspawn;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.storage.repositories.specs.UpdateSqlSpec;
import net.hyze.core.shared.world.location.SerializedLocation;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.spawners.SpawnerType;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.PreparedStatement;

@RequiredArgsConstructor
public class InsertOrUpdateSpawnerSpawnLocationSpec extends UpdateSqlSpec<Void> {

    private final Faction faction;
    private final SpawnerType type;
    private final SerializedLocation location;

    @Override
    public Void parser(int affectedRows) {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {

            String query = "INSERT INTO `factions_spawners_spawns` (`faction_id`, `type`, `location`) "
                    + "VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE "
                    + "`location` = VALUES(`location`);";

            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, faction.getId());
            statement.setString(2, type.name());
            statement.setString(3, location.toString());

            return statement;
        };
    }

}
