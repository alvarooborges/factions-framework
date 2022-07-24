package net.hyze.factions.framework.user.stats.storage;

import lombok.NonNull;
import net.hyze.core.shared.providers.MysqlDatabaseProvider;
import net.hyze.core.shared.storage.repositories.MysqlRepository;
import net.hyze.core.shared.user.User;
import net.hyze.factions.framework.user.stats.UserStats;
import net.hyze.factions.framework.user.stats.storage.specs.InsertUserStatsSpec;
import net.hyze.factions.framework.user.stats.storage.specs.SelectStatsByUserSpec;
import net.hyze.factions.framework.user.stats.storage.specs.UpdateBackLocationSpec;
import net.hyze.factions.framework.user.stats.storage.specs.UpdateUserStatsFieldSpec;

import java.util.EnumSet;

public class UserStatsRepository extends MysqlRepository {

    public UserStatsRepository(MysqlDatabaseProvider databaseProvider) {
        super(databaseProvider);
    }

    public UserStats create(@NonNull User user) {
        return query(new InsertUserStatsSpec(user));
    }

    public UserStats fetch(@NonNull User user) {
        return query(new SelectStatsByUserSpec(user));
    }

    public void update(@NonNull UserStats stats, UserStats.Field field, UserStats.Field... fields) {
        fields = EnumSet.of(field, fields).stream().toArray(UserStats.Field[]::new);

        if (fields.length == 0) {
            return;
        }

        query(new UpdateUserStatsFieldSpec(stats, fields));
    }

    public void updateBackLocation(@NonNull UserStats stats) {
        query(new UpdateBackLocationSpec(stats));
    }
}
