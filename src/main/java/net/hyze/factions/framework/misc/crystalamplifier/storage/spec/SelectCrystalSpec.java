package net.hyze.factions.framework.misc.crystalamplifier.storage.spec;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.misc.crystalamplifier.CrystalAmplifier;
import net.hyze.factions.framework.misc.crystalamplifier.CrystalAmplifierConstants;
import net.hyze.factions.framework.misc.crystalamplifier.cache.local.CrystalAmplifierLocalCache;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@RequiredArgsConstructor
public class SelectCrystalSpec extends SelectSqlSpec<Void> {

    @Override
    public ResultSetExtractor<Void> getResultSetExtractor() {
        return (ResultSet result) -> {

            while (result.next()) {

                CrystalAmplifierLocalCache cache = FactionsProvider.Cache.Local.CRYSTAL_AMPLIFIER.provide();

                cache.put(
                        new CrystalAmplifier(
                                result.getInt("faction_id"),
                                result.getTimestamp("created_at").getTime() + CrystalAmplifierConstants.DURATION,
                                new Location(
                                        Bukkit.getWorld(result.getString("location_world")),
                                        result.getDouble("location_x"),
                                        result.getDouble("location_y"),
                                        result.getDouble("location_z")
                                )
                        )
                );

            }

            return null;

        };
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        String query = "SELECT * FROM `%s` WHERE `end_at` is NULL AND `app_id`=?;";

        return (Connection con) -> {
            PreparedStatement statement = con.prepareStatement(
                    String.format(query,
                            CrystalAmplifierConstants.Databases.Mysql.Tables.TABLE_NAME
                    )
            );

            statement.setString(1, CoreProvider.getApp().getId());

            return statement;
        };
    }

}
