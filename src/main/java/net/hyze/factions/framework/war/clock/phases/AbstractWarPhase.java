package net.hyze.factions.framework.war.clock.phases;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class AbstractWarPhase {

    private final String displayName;
    private final Integer seconds;
    private final boolean pvp;

    /**
     * Roda apenas no inicio da task.
     */
    public void onStart() {

    }

    /**
     * Roda durante a task toda.
     *
     * @param second
     */
    public void onMeantime(Integer second) {

    }

    /**
     * Roda no final da task.
     */
    public void onEnd() {

    }

}
