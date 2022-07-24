package net.hyze.factions.framework.war.clock.phases;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.hyze.factions.framework.war.clock.phases.impl.*;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum EnumWarPhase {

    ANNOUNCE(new WarPhaseAnnounce()),
    PRESTART(new WarPhasePreStart()),
    PVP(new WarPhasePVP()),
    DEATHMATCH(new WarPhaseDeathMatch()),
    ENDING(new WarPhaseEnding()),
    TELEPORT(new WarPhaseTeleport());

    private final AbstractWarPhase warPhase;

    public static EnumWarPhase get(int id) {
        return Stream.of(EnumWarPhase.values())
                .filter(enumTarget -> enumTarget.ordinal() == id)
                .findFirst()
                .orElse(null);
    }

}
