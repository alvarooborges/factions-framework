package net.hyze.factions.framework.faction.storage.specs;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.storage.repositories.specs.InsertSqlSpec;
import net.hyze.factions.framework.FactionsConstants;
import net.hyze.factions.framework.faction.Faction;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

@RequiredArgsConstructor
public class InsertFactionSpec extends InsertSqlSpec<Faction> {

    private final String tag;
    private final String name;
    private final int maxMembers;
    private final Date createAt;

    @Override
    public Faction parser(int affectedRows, KeyHolder keyHolder) {
        if (affectedRows != 1) {
            return null;
        }

        return new Faction(
                keyHolder.getKey().intValue(),
                tag,
                name,
                maxMembers,
                createAt,
                null,
                0
        );
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return (Connection con) -> {
            String query = String.format(
                    "INSERT INTO `%s` (`tag`, `name`, `max_members`, `created_at`, `points`) VALUES(?, ?, ?, ?, ?);",
                    FactionsConstants.Databases.Mysql.Tables.FACTIONS_TABLE_NAME
            );

            PreparedStatement statement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, tag.toUpperCase());
            statement.setString(2, name);
            statement.setInt(3, maxMembers);
            statement.setTimestamp(4, new Timestamp(createAt.getTime()));
            statement.setInt(5, 0);

            return statement;
        };
    }
}
