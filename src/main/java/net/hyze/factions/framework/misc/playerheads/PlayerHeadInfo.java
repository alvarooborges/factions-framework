package net.hyze.factions.framework.misc.playerheads;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PlayerHeadInfo {

    private final String owner;
    private final String killer;
    private final long at;
}
