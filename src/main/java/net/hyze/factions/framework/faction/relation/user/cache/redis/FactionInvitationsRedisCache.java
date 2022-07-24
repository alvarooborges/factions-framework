package net.hyze.factions.framework.faction.relation.user.cache.redis;

import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import lombok.NonNull;
import net.hyze.core.shared.cache.redis.RedisCache;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.user.FactionUser;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FactionInvitationsRedisCache implements RedisCache {

    private static final String KEY_PREFIX = "factions_invitations";
    private static final Function<String, String> NOMINATOR = (nick) -> String.format("%s:%s", KEY_PREFIX, nick.toLowerCase());

    public void putInvitation(@NonNull FactionUser user, @NonNull Faction faction) {
        try (Jedis jedis = FactionsProvider.Redis.REDIS_FACTIONS.provide().getResource()) {
            Pipeline pipeline = jedis.pipelined();

            pipeline.sadd(NOMINATOR.apply(user.getNick()), faction.getId().toString());
            pipeline.expire(NOMINATOR.apply(user.getNick()), 60 * 30);

            pipeline.sync();
        }
    }

    public Set<Faction> getInvitations(FactionUser user) {
        try (Jedis jedis = FactionsProvider.Redis.REDIS_FACTIONS.provide().getResource()) {
            Set<String> members = jedis.smembers(NOMINATOR.apply(user.getNick()));

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

    public boolean hasInvite(@NonNull FactionUser user, @NonNull Faction faction) {
        try (Jedis jedis = FactionsProvider.Redis.REDIS_FACTIONS.provide().getResource()) {
            return jedis.sismember(NOMINATOR.apply(user.getNick()), faction.getId().toString());
        }
    }

    public void clearInvitations(FactionUser user) {
        try (Jedis jedis = FactionsProvider.Redis.REDIS_FACTIONS.provide().getResource()) {
            jedis.del(NOMINATOR.apply(user.getNick()));
        }
    }
}
