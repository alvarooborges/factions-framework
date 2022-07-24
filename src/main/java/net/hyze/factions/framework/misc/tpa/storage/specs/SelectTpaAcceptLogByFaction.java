package net.hyze.factions.framework.misc.tpa.storage.specs;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import net.hyze.core.shared.world.location.SerializedLocation;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.misc.tpa.TpaAcceptLog;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.PreparedStatement;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
public class SelectTpaAcceptLogByFaction extends SelectSqlSpec<List<TpaAcceptLog>> {

    private final Faction faction;

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return con -> {
            String query = "SELECT * FROM `tpaccept_log` WHERE `faction_id` = ?;";

            PreparedStatement statement = con.prepareStatement(query);

            statement.setInt(1, faction.getId());

            return statement;
        };
    }

    @Override
    public ResultSetExtractor<List<TpaAcceptLog>> getResultSetExtractor() {
        return result -> {
            List<TpaAcceptLog> out = Lists.newLinkedList();

            while (result.next()) {
                try {
                    Date date = new Date(result.getTimestamp("date").getTime());

                    out.add(new TpaAcceptLog(
                            result.getInt("id"),
                            faction,
                            result.getInt("target_user_id"),
                            result.getInt("requester_user_id"),
                            result.getString("target_tag"),
                            result.getString("requester_tag"),
                            new SerializedLocation(
                                    result.getString("app_id"),
                                    result.getString("world_name"),
                                    result.getInt("x"),
                                    result.getInt("y"),
                                    result.getInt("z")
                            ),
                            date
                    ));

                } catch (Exception | Error e) {
                    e.printStackTrace();
                }
            }

            return out;
        };
    }
}
