package net.hyze.factions.framework.war.clock;

import lombok.Getter;
import lombok.Setter;
import net.hyze.factions.framework.FactionsPlugin;
import net.hyze.factions.framework.war.WarScoreboardManager;
import net.hyze.factions.framework.war.clock.phases.EnumWarPhase;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class WarClock {

    private BukkitTask bukkitTast;

    @Setter
    @Getter
    private Integer index = 0;

    @Getter
    private EnumWarPhase currentEnumWarPhase;

    @Getter
    private WarClockRunnable currentWarClockRunnable;

    public void next() {
        if (this.bukkitTast != null) {
            this.bukkitTast.cancel();
        }

        EnumWarPhase enumWarPhase = EnumWarPhase.get(this.index);

        if (enumWarPhase == null) {
            System.out.println("[WAR] Stoped. NextPhase is null.");
            stop();
            return;
        }

        this.currentEnumWarPhase = enumWarPhase;
        this.currentWarClockRunnable = new WarClockRunnable(enumWarPhase.getWarPhase());

        System.out.println("[WAR] Starting Phase " + enumWarPhase.getWarPhase().getDisplayName());

        this.bukkitTast = Bukkit.getScheduler().runTaskTimer(
                FactionsPlugin.getInstance(),
                this.currentWarClockRunnable,
                0L,
                20L
        );

        this.index++;
    }

    public void stop() {
        if (this.bukkitTast != null) {
            this.bukkitTast.cancel();
        }

        this.index = 0;
        this.currentEnumWarPhase = null;
        this.currentWarClockRunnable = null;

        WarScoreboardManager.update();
    }

}
