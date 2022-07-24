package net.hyze.factions.framework.faction.permission.storage.specs;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.storage.repositories.specs.InsertSqlSpec;
import net.hyze.factions.framework.FactionsConstants;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.relation.user.FactionRole;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

@RequiredArgsConstructor
public class InsertOrUpdateFactionPermissionByRoleSpec extends InsertSqlSpec<Void> {

    private final Faction faction;
    private final FactionRole role;
    private final int value;

    @Override
    public Void parser(int affectedRows, KeyHolder keyHolder) {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return (Connection con) -> {
            String query = String.format(
                    "INSERT INTO `%s` (`faction_id`, `permissible_type`, `permissible_id`, `value`) VALUES(?, ?, ?, ?) "
                    + "ON DUPLICATE KEY UPDATE `value` = VALUES(`value`);",
                    FactionsConstants.Databases.Mysql.Tables.FACTION_PERMISSIONS_TABLE_NAME
            );

            PreparedStatement statement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            statement.setInt(1, faction.getId());
            statement.setString(2, "ROLE");
            statement.setString(3, role.name());
            statement.setInt(4, value);

            return statement;
        };
    }
}