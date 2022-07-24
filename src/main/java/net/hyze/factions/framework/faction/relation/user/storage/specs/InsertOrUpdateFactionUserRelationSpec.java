package net.hyze.factions.framework.faction.relation.user.storage.specs;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.storage.repositories.specs.InsertSqlSpec;
import net.hyze.factions.framework.FactionsConstants;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;

@RequiredArgsConstructor
public class InsertOrUpdateFactionUserRelationSpec extends InsertSqlSpec<Void> {

    private final FactionUserRelation relation;

    @Override
    public Void parser(int affectedRows, KeyHolder keyHolder) {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return (Connection con) -> {
            PreparedStatement statement = con.prepareStatement(String.format(
                    "INSERT INTO `%s` (`user_id`, `faction_id`, `rank`, `since`) VALUE (?, ?, ?, ?) "
                    + "ON DUPLICATE KEY UPDATE "
                    + "`faction_id`= VALUES(`faction_id`), "
                    + "`rank`= VALUES(`rank`), "
                    + "`since`= VALUES(`since`);",
                    FactionsConstants.Databases.Mysql.Tables.FACTION_USERS_TABLE_NAME
            ), Statement.RETURN_GENERATED_KEYS);

            statement.setInt(1, relation.getUserId());
            statement.setInt(2, relation.getFaction().getId());
            statement.setString(3, relation.getRole().name());
            statement.setTimestamp(4, new Timestamp(relation.getSince().getTime()));

            return statement;
        };
    }
}
