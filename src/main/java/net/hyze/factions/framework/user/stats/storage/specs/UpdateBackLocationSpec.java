package net.hyze.factions.framework.user.stats.storage.specs;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.storage.repositories.specs.UpdateSqlSpec;
import net.hyze.factions.framework.FactionsConstants;
import net.hyze.factions.framework.user.stats.UserStats;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;

@RequiredArgsConstructor
public class UpdateBackLocationSpec extends UpdateSqlSpec<Void> {

    private final UserStats stats;

    @Override
    public Void parser(int affectedRows) {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return (Connection con) -> {

            String query = String.format(
                    "UPDATE `%s` SET `back_location` = ? WHERE `user_id` = ?;",
                    FactionsConstants.Databases.Mysql.Tables.USER_STATS_TABLE_NAME
            );

            PreparedStatement statement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            if (stats.getBackLocation() != null) {
                statement.setString(1, stats.getBackLocation().toString());
            } else {
                statement.setNull(1, Types.VARCHAR);
            }

            statement.setInt(2, stats.getUserId());

            return statement;
        };
    }

}
