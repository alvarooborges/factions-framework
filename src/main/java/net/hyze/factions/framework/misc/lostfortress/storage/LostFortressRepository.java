package net.hyze.factions.framework.misc.lostfortress.storage;

import net.hyze.core.shared.misc.utils.Pair;
import net.hyze.core.shared.providers.MysqlDatabaseProvider;
import net.hyze.core.shared.storage.repositories.MysqlRepository;
import net.hyze.factions.framework.misc.lostfortress.storage.spec.InsertLostFortressLogSpec;
import net.hyze.factions.framework.misc.lostfortress.storage.spec.SelectLostFortressSpec;
import net.hyze.factions.framework.misc.lostfortress.storage.spec.UpdateLostFortressLogSpec;

public class LostFortressRepository extends MysqlRepository {

    public LostFortressRepository(MysqlDatabaseProvider databaseProvider) {
        super(databaseProvider);
    }

    public void insert(String log) {
        query(new InsertLostFortressLogSpec(log));
    }

    public Pair<Integer, String> fetch() {
        return query(new SelectLostFortressSpec());
    }

    public void update(int id, String log) {
        query(new UpdateLostFortressLogSpec(id, log));
    }

}
