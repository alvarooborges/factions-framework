package net.hyze.factions.framework.misc.tpa.storage;

import net.hyze.core.shared.providers.MysqlDatabaseProvider;
import net.hyze.core.shared.storage.repositories.MysqlRepository;
import net.hyze.core.shared.user.User;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.lands.Claim;
import net.hyze.factions.framework.misc.tpa.TpaAcceptLog;
import net.hyze.factions.framework.misc.tpa.storage.specs.InsertTpaAcceptLog;
import net.hyze.factions.framework.misc.tpa.storage.specs.SelectTpaAcceptLogByFaction;
import org.bukkit.Location;

import java.util.List;

public class TpaLogRepository extends MysqlRepository {

    public TpaLogRepository(MysqlDatabaseProvider databaseProvider) {
        super(databaseProvider);
    }

    public void insertAcceptLog(User target, User requester, Claim claim, Location location) {
        query(new InsertTpaAcceptLog(target, requester, claim, location));
    }

    public List<TpaAcceptLog> fetch(Faction faction) {
        return query(new SelectTpaAcceptLogByFaction(faction));
    }
}
