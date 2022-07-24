package net.hyze.factions.framework.divinealtar.storage.spec;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.storage.repositories.specs.InsertSqlSpec;
import net.hyze.factions.framework.divinealtar.AltarConstants;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

@RequiredArgsConstructor
public class InsertAltarSpec extends InsertSqlSpec<Void> {

    private final String altarBank;
    private final Integer factionId;

    @Override
    public Void parser(int affectedRows, KeyHolder keyHolder) {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {

        String query = String.format("INSERT INTO `%s` (`properties`, `faction_id`) VALUES (?, ?);",
                AltarConstants.Databases.Mysql.Tables.ALTAR_TABLE_NAME
        );

        return (Connection con) -> {
            PreparedStatement statement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, this.altarBank);
            statement.setInt(2, this.factionId);
            return statement;
        };
    }
}
