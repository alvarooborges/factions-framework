package net.hyze.factions.framework.faction.relation.faction.storage.specs;

import com.google.common.base.Enums;
import com.google.common.collect.Sets;
import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import net.hyze.factions.framework.FactionsConstants;
import net.hyze.factions.framework.faction.relation.faction.FactionRelation;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Set;

public class SelectFactionRelationsSpec extends SelectSqlSpec<Set<FactionRelation>> {

    @Override
    public ResultSetExtractor<Set<FactionRelation>> getResultSetExtractor() {
        return (ResultSet result) -> {
            Set<FactionRelation> out = Sets.newHashSet();

            while (result.next()) {
                FactionRelation.Type type = Enums.getIfPresent(FactionRelation.Type.class, result.getString("type")).orNull();

                out.add(new FactionRelation(
                        result.getInt("faction_id_min"),
                        result.getInt("faction_id_max"),
                        type
                ));
            }

            return out;
        };
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return (Connection con) -> {
            String query = String.format(
                    "SELECT * FROM `%s`;",
                    FactionsConstants.Databases.Mysql.Tables.FACTIONS_RELATIONS_TABLE_NAME
            );

            return con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        };
    }
}
