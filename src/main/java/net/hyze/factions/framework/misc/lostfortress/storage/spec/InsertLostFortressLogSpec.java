package net.hyze.factions.framework.misc.lostfortress.storage.spec;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.storage.repositories.specs.InsertSqlSpec;
import net.hyze.factions.framework.misc.lostfortress.LostFortressConstants;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;

@RequiredArgsConstructor
public class InsertLostFortressLogSpec extends InsertSqlSpec<Void> {

    private final String log;

    @Override
    public Void parser(int affectedRows, KeyHolder keyHolder) {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {

        String query = String.format("INSERT INTO `%s` (`log`, `active`, `server_id`, `created_at`) VALUES (?, ?, ?, ?);",
                LostFortressConstants.Databases.Mysql.Tables.LOG_TABLE_NAME
        );

        return (Connection con) -> {
            PreparedStatement statement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, this.log);
            statement.setBoolean(2, true);
            statement.setString(3, CoreProvider.getApp().getServer().getId());
            statement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            return statement;
        };
    }
}
