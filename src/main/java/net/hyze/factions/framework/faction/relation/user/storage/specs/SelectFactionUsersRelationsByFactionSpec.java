package net.hyze.factions.framework.faction.relation.user.storage.specs;

import com.google.common.base.Enums;
import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import net.hyze.factions.framework.FactionsConstants;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.relation.user.FactionRole;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Set;

@ToString()
@RequiredArgsConstructor
public class SelectFactionUsersRelationsByFactionSpec extends SelectSqlSpec<Set<FactionUserRelation>> {

    private final Faction faction;

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {

        return (Connection con) -> {
            String query = String.format(
                    "SELECT * FROM `%s` WHERE `faction_id` = ?;",
                    FactionsConstants.Databases.Mysql.Tables.FACTION_USERS_TABLE_NAME
            );

            PreparedStatement statement = con.prepareStatement(query);

            statement.setInt(1, this.faction.getId());

            return statement;
        };

    }

    @Override
    public ResultSetExtractor<Set<FactionUserRelation>> getResultSetExtractor() {
        return (ResultSet result) -> {
            Set<FactionUserRelation> out = Sets.newHashSet();

            while (result.next()) {
                int userId = result.getInt("user_id");
                FactionRole rank = Enums.getIfPresent(FactionRole.class, result.getString("rank")).orNull();
                Date since = result.getTimestamp("since");

                out.add(new FactionUserRelation(userId, faction, rank, since));
            }

            return out;
        };
    }
}
