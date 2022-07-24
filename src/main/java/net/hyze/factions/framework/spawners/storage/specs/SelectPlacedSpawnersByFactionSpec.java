package net.hyze.factions.framework.spawners.storage.specs;

import lombok.RequiredArgsConstructor;
import net.hyze.factions.framework.FactionsConstants;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.spawners.SpawnerState;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;

@RequiredArgsConstructor
public class SelectPlacedSpawnersByFactionSpec extends SelectSpawnersSpec {

    private final Faction faction;

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return (Connection connection) -> {
            String query = String.format(
                    "SELECT * FROM `%s` WHERE `faction_id` = ? AND `state` = ?;",
                    FactionsConstants.Databases.Mysql.Tables.SPAWNERS_TABLE_NAME
            );

            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, faction.getId());
            statement.setString(2, SpawnerState.PLACED.name());

            return statement;
        };
    }
}
