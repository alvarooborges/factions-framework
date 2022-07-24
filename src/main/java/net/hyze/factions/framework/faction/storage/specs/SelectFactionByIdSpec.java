package net.hyze.factions.framework.faction.storage.specs;

import lombok.RequiredArgsConstructor;
import net.hyze.factions.framework.FactionsConstants;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.PreparedStatement;

@RequiredArgsConstructor
public class SelectFactionByIdSpec extends SelectFactionSpec {

    private final Integer id;

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {

        return connection -> {
            PreparedStatement statement = connection.prepareStatement(String.format(
                    "SELECT * FROM `%s` WHERE `id` = ? LIMIT 1;",
                    FactionsConstants.Databases.Mysql.Tables.FACTIONS_TABLE_NAME
            ));

            statement.setInt(1, this.id);

            return statement;
        };

    }
}
