package net.hyze.factions.framework.misc.arena.storage;

import net.hyze.core.shared.providers.MysqlDatabaseProvider;
import net.hyze.core.shared.storage.repositories.MysqlRepository;
import net.hyze.factions.framework.misc.arena.storage.specs.InsertArenaKillSpec;
import net.hyze.factions.framework.misc.arena.storage.specs.SelectArenaRankSpec;

import java.util.HashMap;

public class ArenaRepository extends MysqlRepository {

    public ArenaRepository(MysqlDatabaseProvider databaseProvider) {
        super(databaseProvider);
    }

    public void insertKill(int userId, int deadUserId) {
        query(new InsertArenaKillSpec(userId, deadUserId));
    }

    public HashMap<Integer, Integer> fetchRank() {
        return query(new SelectArenaRankSpec());
    }

}
