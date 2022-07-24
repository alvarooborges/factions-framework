package net.hyze.factions.framework.lands.claim.storage.specs;

import com.google.common.collect.Sets;
import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import net.hyze.factions.framework.FactionsConstants;
import net.hyze.factions.framework.lands.Claim;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Set;

public class SelectClaimsSpec extends SelectSqlSpec<Set<Claim>> {

    @Override
    public ResultSetExtractor<Set<Claim>> getResultSetExtractor() {
        return (ResultSet result) -> {
            Set<Claim> out = Sets.newHashSet();

            while (result.next()) {
                Claim claim = new Claim(
                        result.getInt("faction_id"),
                        new Date(result.getTimestamp("created_at").getTime()),
                        result.getBoolean("temporary"),
                        result.getString("app_id"),
                        result.getInt("chunk_x"),
                        result.getInt("chunk_z")
                );
                
                if (result.getDate("contested_at") != null && result.getInt("contestant_id") != 0) {
                    claim.setContestedAt(new Date(result.getTimestamp("contested_at").getTime()));
                    claim.setContestantId(result.getInt("contestant_id"));
                }

                out.add(claim);
            }

            return out;
        };
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return (Connection con) -> {
            String query = String.format(
                    "SELECT * FROM `%s`;",
                    FactionsConstants.Databases.Mysql.Tables.CLAIMS_TABLE_NAME
            );

            PreparedStatement statement = con.prepareStatement(query);

            return statement;
        };
    }
}
