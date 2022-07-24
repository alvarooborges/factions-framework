package net.hyze.factions.framework.spawners.log.storage.specs;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.storage.repositories.specs.InsertSqlSpec;
import net.hyze.factions.framework.spawners.log.SpawnerLog;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;

@RequiredArgsConstructor
public class InsertSpawnerLogSpec extends InsertSqlSpec<Void> {

    private final SpawnerLog log;

    @Override
    public Void parser(int affectedRows, KeyHolder keyHolder) {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {
            String query = "INSERT INTO `spawners_log` (`faction_id`, `type`, `type_value`, `action`, `spawner_type`, `amount`, `date`) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE "
            + "`amount` = `amount` + VALUES(`amount`);";

            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            long roundTime = (log.getDate().getTime() / 1000 / 60) * 1000 * 60;

            statement.setInt(1, log.getFaction().getId());
            statement.setString(2, log.getType().name());
            statement.setString(3, log.getTypeValue());
            statement.setString(4, log.getAction().name());
            statement.setString(5, log.getSpawnerType().name());
            statement.setInt(6, log.getAmount());
            statement.setTimestamp(7, new Timestamp(roundTime));

            return statement;
        };
    }

}
