package net.hyze.factions.framework.misc.crystalamplifier.storage.spec;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.storage.repositories.specs.InsertSqlSpec;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.misc.crystalamplifier.CrystalAmplifierConstants;
import org.bukkit.Location;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;

@RequiredArgsConstructor
public class InsertCrystalSpec extends InsertSqlSpec<Void> {

    private final Faction faction;
    private final Location location;

    @Override
    public Void parser(int affectedRows, KeyHolder keyHolder) {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {

        String query = String.format("INSERT INTO `%s` (`faction_id`, `faction_tag`, `location_x`, `location_y`, `location_z`, `location_world`, `app_id`, `created_at`) VALUES (?, ?, ?, ?, ?, ?, ?, ?);",
                CrystalAmplifierConstants.Databases.Mysql.Tables.TABLE_NAME
        );

        return (Connection con) -> {
            PreparedStatement statement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            statement.setInt(1, this.faction.getId());
            statement.setString(2, this.faction.getTag());
            statement.setDouble(3, this.location.getX());
            statement.setDouble(4, this.location.getY());
            statement.setDouble(5, this.location.getZ());
            statement.setString(6, this.location.getWorld().getName());
            statement.setString(7, CoreProvider.getApp().getId());
            statement.setTimestamp(8, new Timestamp(System.currentTimeMillis()));

            return statement;
        };
    }
}
