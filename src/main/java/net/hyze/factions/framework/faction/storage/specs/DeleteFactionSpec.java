package net.hyze.factions.framework.faction.storage.specs;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.storage.repositories.specs.UpdateSqlSpec;
import net.hyze.factions.framework.FactionsConstants;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

@RequiredArgsConstructor
public class DeleteFactionSpec extends UpdateSqlSpec<Boolean> {

    private final String tag;

    @Override
    public Boolean parser(int affectedRows) {
        return affectedRows == 1;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return (Connection con) -> {
            String query = String.format(
                    "DELETE FROM `%s` WHERE `tag` = ? LIMIT 1;",
                    FactionsConstants.Databases.Mysql.Tables.FACTIONS_TABLE_NAME
            );

            PreparedStatement statement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, tag.toUpperCase());

            return statement;
        };
    }
}
