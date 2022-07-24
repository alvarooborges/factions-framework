package net.hyze.factions.framework.lands.claim.storage.specs;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.storage.repositories.specs.UpdateSqlSpec;
import net.hyze.factions.framework.FactionsConstants;
import net.hyze.factions.framework.lands.Claim;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;

@RequiredArgsConstructor
public class UpdateClaimSpec extends UpdateSqlSpec<Void> {

    private final Claim claim;

    @Override
    public Void parser(int affectedRows) {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return (Connection con) -> {
            String query = String.format(
                    "UPDATE `%s` SET `contested_at` = ?, `contestant_id` = ? WHERE `app_id` = ? AND `chunk_x` = ? AND `chunk_z` = ? LIMIT 1;",
                    FactionsConstants.Databases.Mysql.Tables.CLAIMS_TABLE_NAME
            );

            PreparedStatement statement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            if (claim.getContestedAt() != null) {
                statement.setTimestamp(1, new Timestamp(claim.getContestedAt().getTime()));
            } else {
                statement.setNull(1, java.sql.Types.DATE);
            }

            if (claim.getContestantId() != null) {
                statement.setInt(2, claim.getContestantId());
            } else {
                statement.setNull(2, java.sql.Types.INTEGER);
            }
            statement.setString(3, claim.getAppId());
            statement.setInt(4, claim.getChunkX());
            statement.setInt(5, claim.getChunkZ());

            return statement;
        };
    }
}
