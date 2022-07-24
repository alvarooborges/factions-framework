package net.hyze.factions.framework.faction.permission.storage.specs;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.storage.repositories.specs.DeleteSqlSpec;
import net.hyze.factions.framework.FactionsConstants;
import net.hyze.factions.framework.faction.Faction;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

@RequiredArgsConstructor
public class DeleteFactionPermissionByAllySpec extends DeleteSqlSpec<Void> {

    private final Faction faction;

    @NonNull
    private final Faction ally;

    @Override
    public Void parser(int affectedRows) {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return (Connection con) -> {
            if (faction != null) {
                String query = String.format(
                        "DELETE FROM `%s` WHERE `faction_id` = ? AND `permissible_type` = 'ALLY' AND `permissible_id` = ? LIMIT 1;",
                        FactionsConstants.Databases.Mysql.Tables.FACTION_PERMISSIONS_TABLE_NAME
                );

                PreparedStatement statement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

                statement.setInt(1, faction.getId());
                statement.setInt(2, ally.getId());

                return statement;
            } else {
                String query = String.format(
                        "DELETE FROM `%s` WHERE `permissible_type` = 'ALLY' AND `permissible_id` = ? LIMIT 1;",
                        FactionsConstants.Databases.Mysql.Tables.FACTION_PERMISSIONS_TABLE_NAME
                );

                PreparedStatement statement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

                statement.setInt(1, ally.getId());

                return statement;
            }
        };
    }
}
