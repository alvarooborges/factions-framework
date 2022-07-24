package net.hyze.factions.framework.spawners.storage.specs;

import com.google.common.base.Enums;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.hyze.core.shared.storage.repositories.specs.SelectSqlSpec;
import net.hyze.core.shared.world.location.SerializedLocation;
import net.hyze.factions.framework.spawners.Spawner;
import net.hyze.factions.framework.spawners.SpawnerState;
import net.hyze.factions.framework.spawners.SpawnerType;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;

public abstract class SelectSpawnersSpec extends SelectSqlSpec<Multimap<SpawnerType, Spawner>> {

    @Override
    public ResultSetExtractor<Multimap<SpawnerType, Spawner>> getResultSetExtractor() {
        return (ResultSet result) -> {
            Multimap<SpawnerType, Spawner> out = ArrayListMultimap.create();

            while (result.next()) {
                String typeRaw = result.getString("type");
                String stateRaw = result.getString("state");

                SpawnerState state = Enums.getIfPresent(SpawnerState.class, stateRaw).orNull();
                SpawnerType type = Enums.getIfPresent(SpawnerType.class, typeRaw).orNull();

                if (type != null && state != null) {

                    SerializedLocation location = null;

                    if (result.getString("app_id") != null) {
                        location = new SerializedLocation(
                                result.getString("app_id"),
                                result.getString("world_name"),
                                result.getInt("x"),
                                result.getInt("y"),
                                result.getInt("z")
                        );
                    }

                    out.put(type, new Spawner(location, state, type, result.getDate("transacted_at")));
                }
            }

            return out;
        };
    }
}
