package net.hyze.factions.framework.furypoints;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FuryType {

    FURY_PLAYERS_KEY(75), FURY_MOBS_KEY(130);

    private final int points;

}
