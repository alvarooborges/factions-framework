package net.hyze.factions.framework.user.stats.storage.specs;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.storage.repositories.specs.UpdateSqlSpec;
import net.hyze.core.shared.user.User;
import net.hyze.factions.framework.FactionsConstants;
import net.hyze.factions.framework.user.stats.UserStats;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

@RequiredArgsConstructor
public class InsertUserStatsSpec extends UpdateSqlSpec<UserStats> {

    private final User user;

    @Override
    public UserStats parser(int affectedRows) {
        if (affectedRows != 1) {
            return null;
        }

        return new UserStats(user.getId());
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return (Connection con) -> {
            String query = String.format(
                    "INSERT INTO `%s` (user_id) VALUES (?);",
                    FactionsConstants.Databases.Mysql.Tables.USER_STATS_TABLE_NAME
            );
            PreparedStatement statement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, user.getId());
         
            return statement;
        };
    }
}
