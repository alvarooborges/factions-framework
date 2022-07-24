package net.hyze.factions.framework.misc.crystalamplifier.storage;

import net.hyze.core.shared.providers.MysqlDatabaseProvider;
import net.hyze.core.shared.storage.repositories.MysqlRepository;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.misc.crystalamplifier.storage.spec.InsertCrystalSpec;
import net.hyze.factions.framework.misc.crystalamplifier.storage.spec.SelectCrystalSpec;
import net.hyze.factions.framework.misc.crystalamplifier.storage.spec.UpdateCrystalSpec;
import org.bukkit.Location;

public class CrystalAmplifierRepository extends MysqlRepository {

    public CrystalAmplifierRepository(MysqlDatabaseProvider databaseProvider) {
        super(databaseProvider);
    }

    public void insert(Faction faction, Location location) {
        query(new InsertCrystalSpec(faction, location));
    }

    public void fetch() {
        query(new SelectCrystalSpec());
    }

    public void update(int factionId) {
        query(new UpdateCrystalSpec(factionId));
    }

}
