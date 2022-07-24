package net.hyze.factions.framework.faction.permission.storage.specs;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.storage.repositories.specs.DeleteSqlSpec;
import net.hyze.factions.framework.FactionsConstants;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.relation.user.FactionRole;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

@RequiredArgsConstructor
public class DeleteFactionPermissionByRoleSpec extends DeleteSqlSpec<Void> {

    private final Faction faction;
    private final FactionRole role;

    @Override
    public Void parser(int affectedRows) {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return (Connection con) -> {
            String query = String.format(
                    "DELETE FROM `%s` WHERE `faction_id` = ? AND `permissible_type` = 'ROLE' AND `permissible_id` = ? LIMIT 1;",
                    FactionsConstants.Databases.Mysql.Tables.FACTION_PERMISSIONS_TABLE_NAME
            );

            PreparedStatement statement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            statement.setInt(1, faction.getId());
            statement.setString(2, role.name());

            return statement;
        };
    }
}
