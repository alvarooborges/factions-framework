package net.hyze.factions.framework.faction.ranking.storage;

import net.hyze.core.shared.providers.MysqlDatabaseProvider;
import net.hyze.core.shared.storage.repositories.MysqlRepository;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.faction.ranking.DailyTycoonItem;
import net.hyze.factions.framework.faction.ranking.DailyValuationItem;
import net.hyze.factions.framework.faction.ranking.WeeklyProfitItem;
import net.hyze.factions.framework.faction.ranking.storage.specs.*;

import java.util.Date;
import java.util.List;

public class FactionRankingRepository extends MysqlRepository {

    public FactionRankingRepository(MysqlDatabaseProvider databaseProvider) {
        super(databaseProvider);
    }

    public void insertSellValue(Faction faction, double value) {
        query(new InsertSellValueSpec(faction, value));
    }

    public List<DailyTycoonItem> fetchSellRanking(int limit) {
        return query(new SelectSellRankingSpec(limit));
    }

    public List<DailyTycoonItem> fetchSellRanking(int limit, Date start, Date end) {
        return query(new SelectSellRankingSpec(limit, start, end));
    }

    public void insertProfitValue(Faction faction, double value) {
        query(new InsertProfitValueSpec(faction, value));
    }

    public List<WeeklyProfitItem> fetchProfitRanking(int limit) {
        return query(new SelectProfitRankingSpec(limit));
    }

    public void insertValuation(Faction faction, double value) {
        query(new InsertValuationSpec(faction, value));
    }

    public List<DailyValuationItem> fetchValuationRanking(int limit) {
        return query(new SelectValuationRankingSpec(limit));
    }

    public List<DailyValuationItem> fetchValuationRankingByDate(int limit, String date) {
        return query(new SelectValuationRankingSpec(limit, date));
    }
}
