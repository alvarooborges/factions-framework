package net.hyze.factions.framework.spawners.log.storage;

import net.hyze.core.shared.providers.MysqlDatabaseProvider;
import net.hyze.core.shared.storage.repositories.MysqlRepository;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.spawners.log.SpawnerLog;
import net.hyze.factions.framework.spawners.log.storage.specs.InsertSpawnerLogSpec;
import net.hyze.factions.framework.spawners.log.storage.specs.SelectSpawnerLogByFactionSpec;

import java.util.List;

public class SpawnerLogRepository extends MysqlRepository {

    public SpawnerLogRepository(MysqlDatabaseProvider databaseProvider) {
        super(databaseProvider);
    }

    public void insert(SpawnerLog log) {
        query(new InsertSpawnerLogSpec(log));
    }

    public List<SpawnerLog> fetch(Faction faction) {
        return query(new SelectSpawnerLogByFactionSpec(faction));
    }
}
