package net.hyze.factions.framework.faction.relation.faction.cache.redis;

import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import lombok.NonNull;
import net.hyze.core.shared.cache.redis.RedisCache;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AllyInvitationsRedisCache implements RedisCache {

    private static final String KEY_PREFIX = "ally_invitations";
    private static final Function<Integer, String> NOMINATOR = (id) -> String.format("%s:%s", KEY_PREFIX, id);

    public void putInvitation(@NonNull Faction sender, @NonNull Faction target) {
        try (Jedis jedis = FactionsProvider.Redis.REDIS_FACTIONS.provide().getResource()) {
            Pipeline pipeline = jedis.pipelined();

            pipeline.sadd(NOMINATOR.apply(target.getId()), sender.getId().toString());
            pipeline.expire(NOMINATOR.apply(target.getId()), 60 * 30);

            pipeline.sync();
        }
    }

    public Set<Faction> getInvitations(@NonNull Faction target) {
        try (Jedis jedis = FactionsProvider.Redis.REDIS_FACTIONS.provide().getResource()) {
            Set<String> members = jedis.smembers(NOMINATOR.apply(target.getId()));

            if (members == null || members.isEmpty()) {
                return Sets.newHashSet();
            }

            return members.stream()
                    .map(Ints::tryParse)
                    .map(FactionsProvider.Cache.Local.FACTIONS.provide()::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        }
    }

    public boolean hasInvite(@NonNull Faction sender, @NonNull Faction target) {
        try (Jedis jedis = FactionsProvider.Redis.REDIS_FACTIONS.provide().getResource()) {
            return jedis.sismember(NOMINATOR.apply(target.getId()), sender.getId().toString());
        }
    }

    public void removeInvitation(@NonNull Faction sender, @NonNull Faction target) {
        try (Jedis jedis = FactionsProvider.Redis.REDIS_FACTIONS.provide().getResource()) {
            jedis.srem(NOMINATOR.apply(target.getId()), sender.getId().toString());
        }
    }
}
