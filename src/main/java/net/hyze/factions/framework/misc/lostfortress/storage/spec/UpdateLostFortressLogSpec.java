package net.hyze.factions.framework.misc.lostfortress.storage.spec;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.storage.repositories.specs.UpdateSqlSpec;
import net.hyze.factions.framework.misc.lostfortress.LostFortressConstants;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;

@RequiredArgsConstructor
public class UpdateLostFortressLogSpec extends UpdateSqlSpec<Void> {

    private final Integer id;
    private final String log;

    @Override
    public Void parser(int affectedRows) {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {

        String query = String.format(
                "UPDATE `%s` SET `log` = ? WHERE `id` = ?;",
                LostFortressConstants.Databases.Mysql.Tables.LOG_TABLE_NAME
        );

        return (Connection con) -> {
            PreparedStatement statement = con.prepareStatement(query);

            statement.setString(1, this.log);
            statement.setInt(2, this.id);

            return statement;
        };
    }

}
