package net.hyze.factions.framework.faction.storage.specs;

import net.hyze.factions.framework.FactionsConstants;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.Connection;

public class SelectAllFactionsSpec extends SelectFactionsSpec {

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return (Connection con) -> {
            String query = String.format(
                    "SELECT * FROM `%s`;",
                    FactionsConstants.Databases.Mysql.Tables.FACTIONS_TABLE_NAME
            );

            return con.prepareStatement(query);
        };
    }
}
