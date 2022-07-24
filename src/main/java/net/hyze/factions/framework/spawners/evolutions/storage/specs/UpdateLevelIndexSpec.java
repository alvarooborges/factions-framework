package net.hyze.factions.framework.spawners.evolutions.storage.specs;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.storage.repositories.specs.UpdateSqlSpec;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.spawners.SpawnerType;
import net.hyze.factions.framework.spawners.evolutions.Evolution;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.PreparedStatement;

@RequiredArgsConstructor
public class UpdateLevelIndexSpec extends UpdateSqlSpec<Void> {

    private final Evolution evolution;
    private final Faction faction;
    private final SpawnerType type;
    private final int index;

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO `spawners_evolutions` " +
                            "(`evolution_id`, `faction_id`, `type`, `evolution_level_index`) " +
                            "VALUES(?, ?, ?, ?) ON DUPLICATE KEY UPDATE " +
                            "`evolution_level_index` = VALUES(`evolution_level_index`);"
            );

            statement.setString(1, evolution.getId());
            statement.setInt(2, faction.getId());
            statement.setString(3, type.name());
            statement.setInt(4, index);

            return statement;
        };
    }

    @Override
    public Void parser(int affectedRows) {
        return null;
    }
}
