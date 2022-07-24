package net.hyze.factions.framework.faction.relation.user.storage.specs;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.storage.repositories.specs.DeleteSqlSpec;
import net.hyze.factions.framework.FactionsConstants;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;

@RequiredArgsConstructor
public class DeleteFactionUserRelationByUserIdSpec extends DeleteSqlSpec<Void> {

    private final Integer userId;

    @Override
    public Void parser(int affectedRows) {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return (Connection con) -> {
            String query = String.format(
                    "DELETE FROM %s WHERE `user_id` = ?;",
                    FactionsConstants.Databases.Mysql.Tables.FACTION_USERS_TABLE_NAME
            );

            PreparedStatement statement = con.prepareStatement(query);

            statement.setInt(1, userId);
            return statement;
        };
    }

}
