package net.hyze.factions.framework.war.clock;

import lombok.Getter;
import net.hyze.factions.framework.war.War;
import net.hyze.factions.framework.war.WarScoreboardManager;
import net.hyze.factions.framework.war.clock.phases.AbstractWarPhase;

import java.util.concurrent.atomic.AtomicInteger;

public class WarClockRunnable implements Runnable {

    private final AbstractWarPhase warPhase;

    @Getter
    private final AtomicInteger count;

    public WarClockRunnable(AbstractWarPhase warPhase) {
        this.warPhase = warPhase;
        this.count = new AtomicInteger(warPhase.getSeconds());
    }

    @Override
    public void run() {

        if (War.PAUSE) {
            WarScoreboardManager.update();
            return;
        }

        if (this.warPhase.getSeconds().equals(this.count.get())) {
            this.count.getAndDecrement();
            this.warPhase.onStart();
            return;
        }

        if (this.count.get() == 0) {
            this.count.getAndDecrement();
            this.warPhase.onEnd();
            return;
        }

        if (this.count.get() < 0) {
            War.CLOCK.next();
            return;
        }

        this.warPhase.onMeantime(this.count.getAndDecrement());
        WarScoreboardManager.update();

    }

}
