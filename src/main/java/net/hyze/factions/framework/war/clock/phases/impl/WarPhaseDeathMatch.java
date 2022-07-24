package net.hyze.factions.framework.war.clock.phases.impl;

import net.hyze.factions.framework.war.War;
import net.hyze.factions.framework.war.WarUtils;
import net.hyze.factions.framework.war.clock.phases.AbstractWarPhase;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;

public class WarPhaseDeathMatch extends AbstractWarPhase {

    public WarPhaseDeathMatch() {
        super("Deathmatch", 36000, true);
    }

    @Override
    public void onStart() {

        World world = Bukkit.getWorld(War.CONFIG.getSpawn().getWorldName());
        WorldBorder border = world.getWorldBorder();

        border.setSize(36, 60);

        WarUtils.check();

    }

    @Override
    public void onMeantime(Integer second) {
        WarUtils.check();
    }

    @Override
    public void onEnd() {
        WarUtils.check();
    }

}
