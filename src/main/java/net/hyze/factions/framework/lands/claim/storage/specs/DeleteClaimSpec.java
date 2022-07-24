package net.hyze.factions.framework.lands.claim.storage.specs;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.storage.repositories.specs.UpdateSqlSpec;
import net.hyze.factions.framework.FactionsConstants;
import net.hyze.factions.framework.lands.Claim;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

@RequiredArgsConstructor
public class DeleteClaimSpec extends UpdateSqlSpec<Void> {

    private final Claim claim;

    @Override
    public Void parser(int affectedRows) {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return (Connection con) -> {
            String query = String.format(
                    "DELETE FROM `%s` WHERE `app_id` = ? AND `chunk_x` = ? AND `chunk_z` = ? LIMIT 1;",
                    FactionsConstants.Databases.Mysql.Tables.CLAIMS_TABLE_NAME
            );

            PreparedStatement statement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, claim.getAppId());
            statement.setInt(2, claim.getChunkX());
            statement.setInt(3, claim.getChunkZ());

            return statement;
        };
    }
}
