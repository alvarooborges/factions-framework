package net.hyze.factions.framework.user.stats.storage.specs;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.storage.repositories.specs.UpdateSqlSpec;
import net.hyze.factions.framework.FactionsConstants;
import net.hyze.factions.framework.user.stats.UserStats;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;

@RequiredArgsConstructor
public class UpdateUserStatsSpec extends UpdateSqlSpec<Void> {

    private final UserStats stats;

    @Override
    public Void parser(int affectedRows) {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return (Connection con) -> {
            PreparedStatement statement = con.prepareStatement(String.format(
                    "UPDATE `%s` SET "
                    + "`power`= ?, "
                    + "`additional_max_power`= ?, "
                    + "`civil_deaths`= ?, "
                    + "`neutral_deaths`= ?, "
                    + "`civil_kills`= ?, "
                    + "`neutral_kills`= ?, "
                    + "`back_location`= ? "
                    + "WHERE `user_id` = ?;",
                    FactionsConstants.Databases.Mysql.Tables.USER_STATS_TABLE_NAME
            ), Statement.RETURN_GENERATED_KEYS);

            statement.setInt(1, stats.getPower());
            statement.setInt(2, stats.getAdditionalMaxPower());
            statement.setInt(3, stats.getCivilDeaths());
            statement.setInt(4, stats.getNeutralDeaths());
            statement.setInt(5, stats.getCivilKills());
            statement.setInt(6, stats.getNeutralKills());

            if (stats.getBackLocation() != null) {
                statement.setString(7, stats.getBackLocation().toString());
            } else {
                statement.setNull(7, Types.VARCHAR);
            }

            statement.setInt(8, stats.getUserId());

            return statement;
        };
    }

}
