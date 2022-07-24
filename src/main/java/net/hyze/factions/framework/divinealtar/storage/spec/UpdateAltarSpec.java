package net.hyze.factions.framework.divinealtar.storage.spec;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.storage.repositories.specs.UpdateSqlSpec;
import net.hyze.factions.framework.divinealtar.AltarConstants;
import net.hyze.factions.framework.divinealtar.altar.AltarProperties;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;

@RequiredArgsConstructor
public class UpdateAltarSpec extends UpdateSqlSpec<Void> {

    private final Integer factionId;
    private final AltarProperties properties;

    @Override
    public Void parser(int affectedRows) {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {

        String query = String.format(
                "INSERT INTO `%s` (`properties`, `faction_id`) VALUES (?, ?) "
                + "ON DUPLICATE KEY UPDATE "
                + "`properties` = VALUES(`properties`);",
                AltarConstants.Databases.Mysql.Tables.ALTAR_TABLE_NAME
        );

        return (Connection con) -> {
            PreparedStatement statement = con.prepareStatement(query);

            statement.setString(1, CoreConstants.GSON.toJson(this.properties));
            statement.setInt(2, this.factionId);

            return statement;
        };
    }

}
