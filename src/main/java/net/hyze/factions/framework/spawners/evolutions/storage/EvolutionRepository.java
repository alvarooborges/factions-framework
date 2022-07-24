package net.hyze.factions.framework.spawners.evolutions.storage;

import net.hyze.core.shared.providers.MysqlDatabaseProvider;
import net.hyze.core.shared.storage.repositories.MysqlRepository;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.spawners.SpawnerType;
import net.hyze.factions.framework.spawners.evolutions.Evolution;
import net.hyze.factions.framework.spawners.evolutions.storage.specs.SelectLevelIndexSpec;
import net.hyze.factions.framework.spawners.evolutions.storage.specs.UpdateLevelIndexSpec;

public class EvolutionRepository extends MysqlRepository {

    public EvolutionRepository(MysqlDatabaseProvider databaseProvider) {
        super(databaseProvider);
    }

    public void updateLevelIndex(Evolution evolution, Faction faction, SpawnerType type, int index) {
        query(new UpdateLevelIndexSpec(evolution, faction, type, index));
    }

    public int selectLevelIndex(String evolutionId, int factionId, String typeId) {
        return query(new SelectLevelIndexSpec(evolutionId, factionId, typeId));
    }
}
