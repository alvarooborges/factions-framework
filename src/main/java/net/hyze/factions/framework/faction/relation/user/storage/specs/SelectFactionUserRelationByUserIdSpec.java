package net.hyze.factions.framework.faction.relation.user.storage.specs;

import com.google.common.base.Enums;
import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import net.hyze.factions.framework.FactionsConstants;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.relation.user.FactionRole;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

@RequiredArgsConstructor
public class SelectFactionUserRelationByUserIdSpec extends SelectSqlSpec<FactionUserRelation> {

    private final Integer userId;

    @Override
    public ResultSetExtractor<FactionUserRelation> getResultSetExtractor() {
        return (ResultSet result) -> {
            if (result.next()) {
                Faction faction = FactionsProvider.Cache.Local.FACTIONS.provide().get(result.getInt("faction_id"));

                FactionRole rank = Enums.getIfPresent(FactionRole.class, result.getString("rank")).orNull();
                Date since = result.getTimestamp("since");

                return new FactionUserRelation(userId, faction, rank, since);
            }

            return null;
        };
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return (Connection con) -> {
            String query = String.format(
                    "SELECT * FROM `%s` WHERE `user_id` = ? LIMIT 1;",
                    FactionsConstants.Databases.Mysql.Tables.FACTION_USERS_TABLE_NAME
            );

            PreparedStatement statement = con.prepareStatement(query);

            statement.setInt(1, this.userId);

            return statement;
        };
    }
}
