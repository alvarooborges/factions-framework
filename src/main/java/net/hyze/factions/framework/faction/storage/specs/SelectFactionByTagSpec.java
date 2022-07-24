package net.hyze.factions.framework.faction.storage.specs;

import lombok.RequiredArgsConstructor;
import net.hyze.factions.framework.FactionsConstants;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.PreparedStatement;

@RequiredArgsConstructor
public class SelectFactionByTagSpec extends SelectFactionSpec {

    private final String tag;

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {
            PreparedStatement statement = connection.prepareStatement(String.format(
                    "SELECT * FROM `%s` WHERE `tag` = ? LIMIT 1;",
                    FactionsConstants.Databases.Mysql.Tables.FACTIONS_TABLE_NAME
            ));

            statement.setString(1, this.tag);

            return statement;
        };
    }
}
