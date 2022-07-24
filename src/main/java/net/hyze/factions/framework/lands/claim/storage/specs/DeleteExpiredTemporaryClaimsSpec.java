package net.hyze.factions.framework.lands.claim.storage.specs;

import net.hyze.core.shared.storage.repositories.specs.UpdateSqlSpec;
import net.hyze.factions.framework.FactionsConstants;
import net.hyze.factions.framework.FactionsProvider;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class DeleteExpiredTemporaryClaimsSpec extends UpdateSqlSpec<Void> {

    @Override
    public Void parser(int affectedRows) {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return (Connection con) -> {
            String query = String.format(
                    "DELETE FROM `claims` WHERE `temporary` = 1 AND `created_at` < (NOW() - INTERVAL ? MINUTE);",
                    FactionsConstants.Databases.Mysql.Tables.CLAIMS_TABLE_NAME
            );

            PreparedStatement statement = con.prepareStatement(query);

            statement.setInt(1, FactionsProvider.getSettings().getTemporaryClaimMinutes());

            return statement;
        };
    }

}
