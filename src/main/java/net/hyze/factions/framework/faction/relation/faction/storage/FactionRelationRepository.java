package net.hyze.factions.framework.faction.relation.faction.storage;

import net.hyze.core.shared.providers.MysqlDatabaseProvider;
import net.hyze.core.shared.storage.repositories.MysqlRepository;
import net.hyze.factions.framework.faction.relation.faction.FactionRelation;
import net.hyze.factions.framework.faction.relation.faction.storage.specs.DeleteFactionRelationSpec;
import net.hyze.factions.framework.faction.relation.faction.storage.specs.InsertFactionRelationSpec;
import net.hyze.factions.framework.faction.relation.faction.storage.specs.SelectFactionRelationsSpec;

import java.util.Set;

public class FactionRelationRepository extends MysqlRepository {

    public FactionRelationRepository(MysqlDatabaseProvider databaseProvider) {
        super(databaseProvider);
    }

    public Set<FactionRelation> fetch() {
        return query(new SelectFactionRelationsSpec());
    }

    public void delete(FactionRelation relation) {
        query(new DeleteFactionRelationSpec(relation));
    }

    public boolean insert(FactionRelation relation) {
        return query(new InsertFactionRelationSpec(relation));
    }
}
