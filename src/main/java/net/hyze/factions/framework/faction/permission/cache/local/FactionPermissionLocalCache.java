package net.hyze.factions.framework.faction.permission.cache.local;

import com.google.common.collect.HashBasedTable;
import net.hyze.core.shared.cache.local.LocalCache;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.relation.user.FactionRole;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;

public class FactionPermissionLocalCache implements LocalCache {

    private final HashBasedTable<Faction, Integer, Integer> CACHE_BY_USERS = HashBasedTable.create();
    private final HashBasedTable<Faction, FactionRole, Integer> CACHE_BY_ROLES = HashBasedTable.create();
    private final HashBasedTable<Faction, Integer, Integer> CACHE_BY_ALLY = HashBasedTable.create();

    public Integer getByUser(FactionUserRelation relation) {
        return CACHE_BY_USERS.get(relation.getFaction(), relation.getUser().getId());
    }

    public Integer getByRole(FactionUserRelation relation) {
        return CACHE_BY_ROLES.get(relation.getFaction(), relation.getRole());
    }

    public Integer getByRole(Faction faction, FactionRole role) {
        return CACHE_BY_ROLES.get(faction, role);
    }

    public Integer getByAlly(Faction faction, Faction ally) {
        return CACHE_BY_ALLY.get(faction, ally.getId());
    }


    public void putByUser(Faction faction, Integer userId, int value) {
        CACHE_BY_USERS.put(faction, userId, value);
    }

    public void putByRole(Faction faction, FactionRole role, int value) {
        CACHE_BY_ROLES.put(faction, role, value);
    }

    public void putByAlly(Faction faction, Integer allyId, int value) {
        CACHE_BY_ALLY.put(faction, allyId, value);
    }

    public void removeByUser(Faction faction, Integer userId) {
        CACHE_BY_USERS.remove(faction, userId);
    }

    public void removeByAlly(Faction faction, Integer allyId) {
        CACHE_BY_ALLY.remove(faction, allyId);
    }

    public void removeByRole(Faction faction, FactionRole role) {
        CACHE_BY_ROLES.remove(faction, role);
    }
}
