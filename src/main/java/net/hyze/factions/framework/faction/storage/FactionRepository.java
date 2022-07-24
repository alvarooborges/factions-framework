package net.hyze.factions.framework.faction.storage;

import net.hyze.core.shared.providers.MysqlDatabaseProvider;
import net.hyze.core.shared.storage.repositories.MysqlRepository;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.storage.specs.*;

import java.util.Date;
import java.util.List;

public class FactionRepository extends MysqlRepository {

    public FactionRepository(MysqlDatabaseProvider databaseProvider) {
        super(databaseProvider);
    }

    public Faction create(String tag, String name, int maxMembers) {
        return query(new InsertFactionSpec(tag, name, maxMembers, new Date()));
    }

    public boolean delete(String tag) {
        return query(new DeleteFactionSpec(tag));
    }

    public Faction fetchByTag(String tag) {
        return query(new SelectFactionByTagSpec(tag));
    }

    public Faction fetchById(Integer id) {
        return query(new SelectFactionByIdSpec(id));
    }

    public List<Faction> fetchAll() {
        return query(new SelectAllFactionsSpec());
    }

    public boolean update(Faction faction) {
        return query(new UpdateFactionSpec(faction));
    }
}
