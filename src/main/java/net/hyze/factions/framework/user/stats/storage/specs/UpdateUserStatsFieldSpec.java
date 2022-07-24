package net.hyze.factions.framework.user.stats.storage.specs;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.storage.repositories.specs.UpdateSqlSpec;
import net.hyze.factions.framework.FactionsConstants;
import net.hyze.factions.framework.user.stats.UserStats;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

@RequiredArgsConstructor
public class UpdateUserStatsFieldSpec extends UpdateSqlSpec<Void> {

    private final UserStats stats;
    private final UserStats.Field[] fields;

    @Override
    public Void parser(int affectedRows) {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return (Connection con) -> {

            StringBuilder query = new StringBuilder(String.format(
                    "UPDATE `%s` SET ",
                    FactionsConstants.Databases.Mysql.Tables.USER_STATS_TABLE_NAME
            ));

            for (UserStats.Field field : fields) {
                query.append(String.format("`%s`= ?,", field.getColumnName()));
            }

            query.setLength(query.length() - 1);

            query.append(" WHERE `user_id` = ?;");

            PreparedStatement statement = con.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);

            int i = 1;

            for (UserStats.Field field : fields) {
                statement.setInt(i++, stats.get(field));
            }

            statement.setInt(i, stats.getUserId());

            return statement;
        };
    }

}
