package net.hyze.factions.framework.user.stats.storage.specs;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import net.hyze.core.shared.user.User;
import net.hyze.core.shared.world.location.SerializedLocation;
import net.hyze.factions.framework.FactionsConstants;
import net.hyze.factions.framework.user.stats.UserStats;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@RequiredArgsConstructor
public class SelectStatsByUserSpec extends SelectSqlSpec<UserStats> {

    @NonNull
    private final User user;

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return (Connection con) -> {
            String query = String.format(
                    "SELECT * FROM `%s` WHERE `user_id` = ?;",
                    FactionsConstants.Databases.Mysql.Tables.USER_STATS_TABLE_NAME
            );

            PreparedStatement statement = con.prepareStatement(query);

            statement.setInt(1, this.user.getId());

            return statement;
        };
    }

    @Override
    public ResultSetExtractor<UserStats> getResultSetExtractor() {
        return (ResultSet result) -> {
            if (result.next()) {
                int power = result.getInt("power");
                int maxPower = result.getInt("additional_max_power");
                int civilDeaths = result.getInt("civil_deaths");
                int neutralDeaths = result.getInt("neutral_deaths");
                int civilKills = result.getInt("civil_kills");
                int neutralKills = result.getInt("neutral_kills");
                String backLocationRaw = result.getString("back_location");

                SerializedLocation backLocation = null;

                if (backLocationRaw != null) {
                    try {
                        backLocation = SerializedLocation.of(backLocationRaw);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                return new UserStats(
                        user.getId(),
                        power,
                        maxPower,
                        civilDeaths,
                        neutralDeaths,
                        civilKills,
                        neutralKills,
                        backLocation
                );
            }

            return null;
        };
    }
}
