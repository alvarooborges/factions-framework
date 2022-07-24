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
public class InsertClaimSpec extends UpdateSqlSpec<Boolean> {

    private final Claim claim;

    @Override
    public Boolean parser(int affectedRows) {
        return affectedRows != 0;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return (Connection con) -> {

            String query;
            if (claim.isTemporary()) {
                query = String.format(
                        "INSERT INTO `%s` (`faction_id`, `app_id`, `chunk_x`, `chunk_z`, `temporary`) "
                        + "VALUES (?, ?, ?, ?, 1) ON DUPLICATE KEY UPDATE "
                        + "`faction_id` = VALUES(`faction_id`), "
                        + "`app_id` = VALUES(`app_id`), "
                        + "`temporary` = 1;",
                        FactionsConstants.Databases.Mysql.Tables.CLAIMS_TABLE_NAME
                );
            } else {
                query = String.format(
                        "INSERT INTO `claims` (`faction_id`, `app_id`, `chunk_x`, `chunk_z`, `temporary`) "
                        + "SELECT tmp.* FROM (SELECT ? as 'temp_f', ? as 'temp_app', ? as 'temp_x', ? as 'temp_z', 0 as 'temp_t') AS tmp "
                        + "WHERE NOT EXISTS (SELECT `id` FROM `claims` WHERE `faction_id` = ? AND `temporary` = 0 AND `app_id` != ?) LIMIT 1 "
                        + "ON DUPLICATE KEY UPDATE "
                        + "`faction_id` = VALUES(`faction_id`), "
                        + "`app_id` = VALUES(`app_id`), "
                        + "`temporary` = 0;"
                );
            }

            PreparedStatement statement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            statement.setInt(1, claim.getFactionId());
            statement.setString(2, claim.getAppId());
            statement.setInt(3, claim.getChunkX());
            statement.setInt(4, claim.getChunkZ());

            if (!claim.isTemporary()) {
                statement.setInt(5, claim.getFactionId());
                statement.setString(6, claim.getAppId());
            }

            return statement;
        };
    }
}
