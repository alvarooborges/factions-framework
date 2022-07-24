package net.hyze.factions.framework.faction.permission.storage;

import net.hyze.core.shared.providers.MysqlDatabaseProvider;
import net.hyze.core.shared.storage.repositories.MysqlRepository;
import net.hyze.core.shared.user.User;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.permission.storage.specs.*;
import net.hyze.factions.framework.faction.relation.user.FactionRole;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;

import java.util.List;

public class FactionPermissionRepository extends MysqlRepository {

    public FactionPermissionRepository(MysqlDatabaseProvider databaseProvider) {
        super(databaseProvider);
    }

    public void fetch(FactionPermissionConsumer<Integer> allyConsumer,
                      FactionPermissionConsumer<FactionRole> roleConsumer,
                      FactionPermissionConsumer<Integer> userConsumer) {

        List<SelectFactionPermissionsSpect.Response> responses = query(new SelectFactionPermissionsSpect());

        responses.forEach(response -> {
            Faction faction = FactionsProvider.Cache.Local.FACTIONS.provide().get(response.getFactionId());

            switch (response.getPermissableType()) {
                case "ALLY":
                    allyConsumer.accept(faction, Integer.valueOf(response.getPermissableId()), response.getValue());
                    break;
                case "ROLE":
                    roleConsumer.accept(faction, FactionRole.valueOf(response.getPermissableId()), response.getValue());
                    break;
                case "USER":
                    userConsumer.accept(faction, Integer.valueOf(response.getPermissableId()), response.getValue());
                    break;
            }
        });
    }

    public void updateByUser(FactionUserRelation relation, Integer value) {
        updateByUser(relation.getFaction(), relation.getUser().getHandle(), value);
    }

    public void updateByUser(Faction faction, User user, Integer value) {
        if (value == null) {
            query(new DeleteFactionPermissionByUserSpec(faction, user));
        } else {
            query(new InsertOrUpdateFactionPermissionByUserSpec(faction, user, value));
        }
    }

    public void updateByRole(FactionUserRelation relation, Integer value) {
        updateByRole(relation.getFaction(), relation.getRole(), value);
    }

    public void updateByRole(Faction faction, FactionRole role, Integer value) {
        if (value == null) {
            query(new DeleteFactionPermissionByRoleSpec(faction, role));
        } else {
            query(new InsertOrUpdateFactionPermissionByRoleSpec(faction, role, value));
        }
    }

    public void updateByAlly(Faction faction, Faction ally, Integer value) {
        if (value == null) {
            query(new DeleteFactionPermissionByAllySpec(faction, ally));
        } else {
            query(new InsertOrUpdateFactionPermissionByAllySpec(faction, ally, value));
        }
    }

    public static interface FactionPermissionConsumer<T> {

        void accept(Faction faction, T permissable, Integer value);
    }
}
