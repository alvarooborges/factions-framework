package net.hyze.factions.framework.faction.relation.faction.storage.specs;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.storage.repositories.specs.UpdateSqlSpec;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.relation.faction.FactionRelation;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.Connection;
import java.sql.Statement;

@RequiredArgsConstructor
public class InsertFactionRelationSpec extends UpdateSqlSpec<Boolean> {

    private final FactionRelation relation;

    @Override
    public Boolean parser(int affectedRows) {
        return affectedRows >= 1;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return (Connection con) -> {

            int allyLimit = FactionsProvider.getSettings().getAllyLimit();

            String query = String.format(
                    "INSERT INTO `factions_relations` (`faction_id_min`, `faction_id_max`, `type`) " +
                            "  SELECT tmp.* FROM (SELECT %s as 'temp_min', %s as 'temp_max', '%s' as 'temp_type') AS tmp " +
                            "    WHERE NOT EXISTS (SELECT count(*) FROM `factions_relations` WHERE `faction_id_min` = `tmp`.`temp_min` OR `faction_id_max` = `tmp`.`temp_min` GROUP BY `type` HAVING COUNT(*) >= %s) " +
                            "      AND NOT EXISTS (SELECT count(*) FROM `factions_relations` WHERE `faction_id_min` = `tmp`.`temp_max` OR `faction_id_max` = `tmp`.`temp_max` GROUP BY `type` HAVING COUNT(*) >= %s) " +
                            "ON DUPLICATE KEY UPDATE `faction_id_min` = `faction_id_min`;",
                    relation.getFactionIdMin(),
                    relation.getFactionIdMax(),
                    relation.getType().name(),
                    allyLimit, allyLimit
            );

            return con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        };
    }
}
