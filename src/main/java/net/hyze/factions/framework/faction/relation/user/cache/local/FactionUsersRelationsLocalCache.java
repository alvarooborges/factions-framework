package net.hyze.factions.framework.faction.relation.user.cache.local;

import com.google.common.collect.*;
import dev.utils.shared.Debug;
import lombok.NonNull;
import net.hyze.core.shared.cache.local.LocalCache;
import net.hyze.core.shared.user.User;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.relation.user.FactionUserRelation;
import net.hyze.factions.framework.user.FactionUser;

import javax.annotation.Nullable;
import java.util.*;

public class FactionUsersRelationsLocalCache implements LocalCache {


    private final SetMultimap<Faction, FactionUserRelation> CACHE_BY_FACTION = Multimaps.synchronizedSetMultimap(HashMultimap.create());
    private final Map<Integer, Optional<FactionUserRelation>> CACHE_BY_USER_ID = Maps.newConcurrentMap();

    public ImmutableMultimap<Faction, FactionUserRelation> all() {
        synchronized (CACHE_BY_FACTION) {
            return ImmutableMultimap.copyOf(CACHE_BY_FACTION);
        }
    }

    public SetMultimap<Faction, FactionUserRelation> getByFactions(Collection<Faction> factions) {
        SetMultimap<Faction, FactionUserRelation> out = HashMultimap.create();

        for (Faction faction : factions) {
            out.putAll(faction, getByFaction(faction));
        }

        return out;
    }

    public FactionUserRelation refreshByUser(@NonNull FactionUser user) {
        return refreshByUser(user.getHandle());
    }

    public FactionUserRelation refreshByUser(@NonNull User user) {
        return refreshByUserId(user.getId());
    }

    public FactionUserRelation refreshByUserId(@NonNull Integer userId) {

        removeByUserId(userId);

        return getByUserId(userId);
    }

    public ImmutableSet<FactionUserRelation> getIfPresentByFaction(@NonNull Faction faction) {
        return ImmutableSet.copyOf(CACHE_BY_FACTION.get(faction));
    }

    public FactionUserRelation getByUser(@NonNull FactionUser user) {
        return getByUserId(user.getId());
    }

    public FactionUserRelation getByUser(@NonNull User user) {
        return getByUserId(user.getId());
    }

    public FactionUserRelation getIfPresentByUserId(@NonNull Integer userId) {
        return Optional.ofNullable(CACHE_BY_USER_ID.get(userId))
                .flatMap(o -> o)
                .orElse(null);
    }

    public FactionUserRelation getIfPresentByUser(@NonNull FactionUser user) {
        return getIfPresentByUserId(user.getId());
    }

    public FactionUserRelation getIfPresentByUser(@NonNull User user) {
        return getIfPresentByUserId(user.getId());
    }

    public void removeByUser(@NonNull FactionUser user) {
        removeByUser(user.getHandle());
    }

    public void removeByUser(@NonNull User user) {
        removeByUserId(user.getId());
    }

    public void removeByUserId(@NonNull Integer userId) {
        Optional<FactionUserRelation> optional = CACHE_BY_USER_ID.remove(userId);

        if (optional != null && optional.isPresent()) {
            Iterator<FactionUserRelation> iterator = CACHE_BY_FACTION.get(optional.get().getFaction()).iterator();

            while (iterator.hasNext()) {
                FactionUserRelation r = iterator.next();

                if (r.getUserId().equals(userId)) {
                    iterator.remove();
                    break;
                }
            }
        }
    }

    public void removeByFaction(@NonNull Faction faction) {
        Set<FactionUserRelation> relations = CACHE_BY_FACTION.removeAll(faction);

        if (relations != null) {
            for (FactionUserRelation relation : relations) {
                CACHE_BY_USER_ID.remove(relation.getUserId());
            }
        }
    }

    public ImmutableSet<FactionUserRelation> getByFaction(@NonNull Faction faction) {

        if (CACHE_BY_FACTION.containsKey(faction)) {
            return ImmutableSet.copyOf(CACHE_BY_FACTION.get(faction));
        }

        Set<FactionUserRelation> relations = FactionsProvider.Repositories.USERS_RELATIONS.provide().fetchByFaction(faction);

        CACHE_BY_FACTION.putAll(faction, relations);

        relations.forEach(relation -> CACHE_BY_USER_ID.put(relation.getUserId(), Optional.of(relation)));

        return ImmutableSet.copyOf(relations);
    }

    public FactionUserRelation getByUserId(@NonNull Integer userId) {

        @Nullable
        Optional<FactionUserRelation> optional = CACHE_BY_USER_ID.get(userId);

        // Não mudar para isPresent
        if (optional != null) {
            return optional.orElse(null);
        }

        FactionUserRelation relation = FactionsProvider.Repositories.USERS_RELATIONS.provide().fetchByUserId(userId);

        CACHE_BY_USER_ID.put(userId, Optional.ofNullable(relation));

        if (relation != null) {
            CACHE_BY_FACTION.put(relation.getFaction(), relation);
        }

        return relation;
    }
}
