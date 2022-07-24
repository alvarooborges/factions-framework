package net.hyze.factions.framework.user.options.cache.redis;

import com.google.common.collect.Maps;
import net.hyze.core.shared.cache.redis.RedisCache;
import net.hyze.core.shared.user.User;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.user.options.UserOptions;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.Map;
import java.util.function.Function;

public class UserOptionsRedisCache implements RedisCache {

    public static final Function<String, String> KEY_CONVERTER_BY_NICK = nick -> "user_options:" + nick.toLowerCase();

    public UserOptions fetch(User user) {
        try (Jedis jedis = FactionsProvider.Redis.REDIS_FACTIONS.provide().getResource()) {
            Map<String, String> map = jedis.hgetAll(KEY_CONVERTER_BY_NICK.apply(user.getNick()));

            if (map == null || map.isEmpty()) {
                return null;
            }

            UserOptions options = new UserOptions(user);

            options.setSeeChunksEnabled(Boolean.parseBoolean(map.getOrDefault("see_chunks_enabled", "false")));
            options.setAdminModeEnabled(Boolean.parseBoolean(map.getOrDefault("admin_mode_enabled", "false")));
            options.setAutoMapEnabled(Boolean.parseBoolean(map.getOrDefault("auto_map_enabled", "false")));
            options.setLightEnabled(Boolean.parseBoolean(map.getOrDefault("light_enabled", "false")));
            options.setPvpEnabled(Boolean.parseBoolean(map.getOrDefault("pvp_enabled", "true")));
            options.setGlobalTabEnabled(Boolean.parseBoolean(map.getOrDefault("global_tab_enabled", "true")));
            options.setAACKickCount(Integer.parseInt(map.getOrDefault("aac_kick_count", "0")));
            options.setAACBanAt(Long.parseLong(map.getOrDefault("aac_ban_at", "0")));

            return options;
        }
    }

    public void update(User user, UserOptions options) {
        try (Jedis jedis = FactionsProvider.Redis.REDIS_FACTIONS.provide().getResource()) {

            Pipeline pipeline = jedis.pipelined();
            Map<String, String> map = Maps.newHashMap();

            map.put("see_chunks_enabled", String.valueOf(options.isSeeChunksEnabled()));
            map.put("admin_mode_enabled", String.valueOf(options.isAdminModeEnabled()));
            map.put("auto_map_enabled", String.valueOf(options.isAutoMapEnabled()));
            map.put("light_enabled", String.valueOf(options.isLightEnabled()));
            map.put("pvp_enabled", String.valueOf(options.isPvpEnabled()));
            map.put("global_tab_enabled", String.valueOf(options.isGlobalTabEnabled()));
            map.put("aac_kick_count", String.valueOf(options.getAACKickCount()));
            map.put("aac_ban_at", String.valueOf(options.getAACBanAt()));

            pipeline.hset(KEY_CONVERTER_BY_NICK.apply(user.getNick()), map);

            pipeline.expire(KEY_CONVERTER_BY_NICK.apply(user.getNick()), 60 * 60);

            pipeline.sync();
        }
    }
}
