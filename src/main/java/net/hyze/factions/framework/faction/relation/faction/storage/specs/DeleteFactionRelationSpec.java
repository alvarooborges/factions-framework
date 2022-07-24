package net.hyze.factions.framework.faction.relation.faction.storage.specs;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.storage.repositories.specs.UpdateSqlSpec;
import net.hyze.factions.framework.FactionsConstants;
import net.hyze.factions.framework.faction.relation.faction.FactionRelation;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

@RequiredArgsConstructor
public class DeleteFactionRelationSpec extends UpdateSqlSpec<Void> {

    private final FactionRelation relation;

    @Override
    public Void parser(int affectedRows) {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return (Connection con) -> {
            String query = String.format(
                    "DELETE FROM `%s` WHERE `faction_id_min` = ? AND `faction_id_max` = ? LIMIT 1;",
                    FactionsConstants.Databases.Mysql.Tables.FACTIONS_RELATIONS_TABLE_NAME
            );

            PreparedStatement statement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            statement.setInt(1, relation.getFactionIdMin());
            statement.setInt(2, relation.getFactionIdMax());

            return statement;
        };
    }

}
