package net.hyze.factions.framework.user.cache.local;

import com.github.benmanes.caffeine.cache.CacheLoader;
import lombok.NonNull;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.cache.local.CredentialLocalCache;
import net.hyze.core.shared.user.User;
import net.hyze.factions.framework.FactionsProvider;
import net.hyze.factions.framework.user.FactionUser;
import net.hyze.factions.framework.user.options.UserOptions;
import net.hyze.factions.framework.user.stats.UserStats;
import org.bukkit.entity.Player;

public class FactionUserLocalCache extends CredentialLocalCache<FactionUser> {

    @Override
    public CacheLoader<String, FactionUser> getLoaderByNick() {
        return (String nick) -> {
            User user = CoreProvider.Cache.Local.USERS.provide().get(nick);

            return build0(user);
        };
    }

    @Override
    public CacheLoader<Integer, FactionUser> getLoaderById() {
        return (Integer id) -> {
            User user = CoreProvider.Cache.Local.USERS.provide().get(id);

            return build0(user);
        };
    }

    private FactionUser build0(User user) {
        if (user == null) {
            return null;
        }

        UserStats stats = FactionsProvider.Repositories.USER_STATS.provide().fetch(user);

        if (stats == null) {
            stats = FactionsProvider.Repositories.USER_STATS.provide().create(user);
        }

        UserOptions options = FactionsProvider.Cache.Redis.USERS_OPTIONS.provide().fetch(user);

        if (options == null) {
            options = new UserOptions(user);
        }

        return new FactionUser(user, stats, options);
    }

    public FactionUser get(@NonNull User user) {
        return this.get(user.getNick());
    }

    public FactionUser get(@NonNull Player player) {
        return this.get(player.getName());
    }

    public void remove(User user) {
        if (user != null) {
            this.remove(user.getNick());
        }
    }
}
