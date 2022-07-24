package net.hyze.factions.framework.faction.relation.user.storage;

import lombok.NonNull;
import net.hyze.core.shared.providers.MysqlDatabaseProvider;
import net.hyze.core.shared.storage.repositories.MysqlRepository;
import net.hyze.core.shared.user.User;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.faction.relation.user.storage.specs.DeleteFactionUserRelationByUserIdSpec;
import net.hyze.factions.framework.faction.relation.user.storage.specs.InsertOrUpdateFactionUserRelationSpec;
import net.hyze.factions.framework.faction.relation.user.storage.specs.SelectFactionUserRelationByUserIdSpec;
import net.hyze.factions.framework.faction.relation.user.storage.specs.SelectFactionUsersRelationsByFactionSpec;

import javax.annotation.Nonnull;
import java.util.Set;

public class FactionUserRelationRepository extends MysqlRepository {

    public FactionUserRelationRepository(MysqlDatabaseProvider databaseProvider) {
        super(databaseProvider);
    }

    public void update(@NonNull FactionUserRelation relation) {
        query(new InsertOrUpdateFactionUserRelationSpec(relation));
    }

    public void remove(@NonNull Integer userId) {
        query(new DeleteFactionUserRelationByUserIdSpec(userId));
    }

    public FactionUserRelation fetchByUser(@Nonnull User user) {
        return fetchByUserId(user.getId());
    }

    public FactionUserRelation fetchByUserId(@Nonnull Integer userId) {
        return query(new SelectFactionUserRelationByUserIdSpec(userId));
    }

    public Set<FactionUserRelation> fetchByFaction(@Nonnull Faction faction) {
        return query(new SelectFactionUsersRelationsByFactionSpec(faction));
    }
}
