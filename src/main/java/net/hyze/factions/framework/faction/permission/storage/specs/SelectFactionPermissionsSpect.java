package net.hyze.factions.framework.faction.permission.storage.specs;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import net.hyze.factions.framework.FactionsConstants;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

public class SelectFactionPermissionsSpect extends SelectSqlSpec<List<SelectFactionPermissionsSpect.Response>> {

    @Override
    public ResultSetExtractor<List<Response>> getResultSetExtractor() {
        return (ResultSet result) -> {
            List<Response> out = Lists.newArrayList();

            while (result.next()) {
                out.add(new Response(
                        result.getInt("faction_id"),
                        result.getString("permissible_type"),
                        result.getString("permissible_id"),
                        result.getInt("value")
                ));
            }

            return out;
        };
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return (Connection con) -> {
            String query = String.format(
                    "SELECT * FROM `%s`;",
                    FactionsConstants.Databases.Mysql.Tables.FACTION_PERMISSIONS_TABLE_NAME
            );

            return con.prepareStatement(query);
        };
    }

    @Getter
    @RequiredArgsConstructor
    public static class Response {

        private final Integer factionId;
        private final String permissableType;
        private final String permissableId;
        private final Integer value;
    }
}
