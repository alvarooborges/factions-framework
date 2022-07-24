package net.hyze.factions.framework.divinealtar.storage;

import net.hyze.core.shared.providers.MysqlDatabaseProvider;
import net.hyze.core.shared.storage.repositories.MysqlRepository;
import net.hyze.factions.framework.divinealtar.inventory.AltarInventory;
import net.hyze.factions.framework.divinealtar.storage.spec.InsertAltarSpec;
import net.hyze.factions.framework.divinealtar.storage.spec.SelectAltarSpec;
import net.hyze.factions.framework.divinealtar.storage.spec.UpdateAltarSpec;

import java.util.HashMap;

public class DivineAltarRepository extends MysqlRepository {

    public DivineAltarRepository(MysqlDatabaseProvider databaseProvider) {
        super(databaseProvider);
    }

    public void insert(String properties, int factionId) {
        query(new InsertAltarSpec(properties, factionId));
    }

    public HashMap<Integer, AltarInventory> fetch() {
        return query(new SelectAltarSpec());
    }

    public void update(AltarInventory inventory) {
        query(new UpdateAltarSpec(inventory.getFactionId(), inventory.getAltarProperties()));
    }

}
