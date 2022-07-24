package net.hyze.factions.framework.ranking;

import dev.utils.shared.concurrent.NamedThreadFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.hyze.factions.framework.ranking.factions.*;
import net.hyze.factions.framework.ranking.factions.coins.CoinsRanking;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Getter
@RequiredArgsConstructor
public enum RankingFactory {

    FACTIONS_SPAWNERS_RANKING(new SpawnersRanking()),
    FACTIONS_KDR_RANKING(new KDRRanking()),
    FACTIONS_VALUATION_RANKING(new ValuationRanking()),
    FACTIONS_DAILY_TYCOON_RANKING(new DailyTycoonRanking()),
    FACTIONS_WEEKLY_TYCOON_RANKING(new WeeklyTycoonRanking()),
    FACTIONS_INVASION_PROFIT_RANKING(new InvasionProfitRanking()),
    FACTIONS_COINS_RANKING(new CoinsRanking()),
    ;

    public static final ScheduledExecutorService RANKING_EXECUTOR_SERVICE = Executors.newScheduledThreadPool(6, new NamedThreadFactory("RankingExecutor", true));

    private final Ranking ranking;
}
