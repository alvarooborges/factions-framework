package net.hyze.factions.framework.misc.arena.storage.specs;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.storage.repositories.specs.InsertSqlSpec;
import net.hyze.factions.framework.FactionsConstants;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;

@RequiredArgsConstructor
public class InsertArenaKillSpec extends InsertSqlSpec<Void> {

    private final int userId;
    private final int deadUserId;

    @Override
    public Void parser(int affectedRows, KeyHolder keyHolder) {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {

        String query = String.format(
                "INSERT INTO `%s` (`user_id`, `dead_user_id`, `created_at`) VALUES (?, ?, ?);",
                FactionsConstants.Databases.Mysql.Tables.ARENA_KILLS_TABLE_NAME
        );

        return (Connection con) -> {
            PreparedStatement statement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            statement.setInt(1, this.userId);
            statement.setInt(2, this.deadUserId);
            statement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));

            return statement;
        };
    }
}
