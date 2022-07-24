package net.hyze.factions.framework.misc.lostfortress.storage.spec;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.misc.utils.Pair;
import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import net.hyze.factions.framework.misc.lostfortress.LostFortressConstants;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@RequiredArgsConstructor
public class SelectLostFortressSpec extends SelectSqlSpec<Pair<Integer, String>> {

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        String query = String.format(
                "SELECT * FROM `%s` WHERE `active` = ?;",
                LostFortressConstants.Databases.Mysql.Tables.LOG_TABLE_NAME
        );

        return (Connection con) -> {
            PreparedStatement statement = con.prepareStatement(query);

            statement.setBoolean(1, true);

            return statement;
        };
    }

    @Override
    public ResultSetExtractor<Pair<Integer, String>> getResultSetExtractor() {
        return (ResultSet result) -> {

            if (result.next()) {
                return new Pair(result.getInt("id"), result.getString("log"));
            }

            return null;
        };
    }

}
