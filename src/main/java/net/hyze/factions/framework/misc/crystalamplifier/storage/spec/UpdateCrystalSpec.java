package net.hyze.factions.framework.misc.crystalamplifier.storage.spec;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.storage.repositories.specs.UpdateSqlSpec;
import net.hyze.factions.framework.misc.crystalamplifier.CrystalAmplifierConstants;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

@RequiredArgsConstructor
public class UpdateCrystalSpec extends UpdateSqlSpec<Void> {

    private final Integer factionId;

    @Override
    public Void parser(int affectedRows) {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {

        String query = String.format(
                "UPDATE `%s` SET `end_at` = ? WHERE `faction_id` = ? AND `end_at` is NULL;",
                CrystalAmplifierConstants.Databases.Mysql.Tables.TABLE_NAME
        );

        return (Connection con) -> {
            PreparedStatement statement = con.prepareStatement(query);

            statement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            statement.setInt(2, this.factionId);

            return statement;
        };
    }

}
