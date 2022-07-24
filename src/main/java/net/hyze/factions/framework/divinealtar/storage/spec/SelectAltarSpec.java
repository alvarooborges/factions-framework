package net.hyze.factions.framework.divinealtar.storage.spec;

import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import net.hyze.factions.framework.divinealtar.AltarConstants;
import net.hyze.factions.framework.divinealtar.altar.AltarProperties;
import net.hyze.factions.framework.divinealtar.inventory.AltarInventory;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

@RequiredArgsConstructor
public class SelectAltarSpec extends SelectSqlSpec<HashMap<Integer, AltarInventory>> {

    @Override
    public ResultSetExtractor<HashMap<Integer, AltarInventory>> getResultSetExtractor() {
        return (ResultSet result) -> {

            HashMap<Integer, AltarInventory> cache = Maps.newHashMap();

            while (result.next()) {

                cache.put(result.getInt("faction_id"),
                        new AltarInventory(result.getInt("faction_id"), CoreConstants.GSON.fromJson(result.getString("properties"), AltarProperties.class))
                );

            }

            return cache;

        };
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        String query = "SELECT * FROM `%s`;";

        return (Connection con) -> {
            PreparedStatement statement = con.prepareStatement(
                    String.format(query,
                            AltarConstants.Databases.Mysql.Tables.ALTAR_TABLE_NAME
                    )
            );

            return statement;
        };
    }

}
