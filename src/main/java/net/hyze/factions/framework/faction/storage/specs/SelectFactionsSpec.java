package net.hyze.factions.framework.faction.storage.specs;

import com.google.common.collect.Lists;
import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import net.hyze.core.shared.world.location.SerializedLocation;
import net.hyze.factions.framework.faction.Faction;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.util.Date;
import java.util.List;

public abstract class SelectFactionsSpec extends SelectSqlSpec<List<Faction>> {

    @Override
    public ResultSetExtractor<List<Faction>> getResultSetExtractor() {
        return (ResultSet result) -> {
            List<Faction> out = Lists.newArrayList();

            while (result.next()) {
                int id = result.getInt("id");
                String tag = result.getString("tag").toUpperCase();
                String name = result.getString("name");
                String home = result.getString("home");
                Date createdAt = result.getTimestamp("created_at");
                int maxMembers = result.getInt("max_members");
                Date underAttackAt = result.getTimestamp("under_attack_at");

                int points = result.getInt("points");

                out.add(new Faction(
                        id,
                        tag,
                        name,
                        maxMembers,
                        createdAt,
                        SerializedLocation.of(home),
                        underAttackAt,
                        points
                ));
            }

            return out;
        };
    }
}