package net.hyze.factions.framework.faction.ranking;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.hyze.factions.framework.faction.Faction;

@Getter
@RequiredArgsConstructor
public class DailyTycoonItem {

    private final Faction faction;
    private final double value;
}
