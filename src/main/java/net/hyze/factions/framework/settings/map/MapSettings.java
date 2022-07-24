package net.hyze.factions.framework.settings.map;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import net.hyze.core.spigot.CoreSpigotSettings;
import net.hyze.core.spigot.misc.hologram.Hologram;
import net.hyze.core.spigot.misc.npc.CustomNPC;
import net.hyze.core.spigot.misc.npc.NPCScoreboard;
import net.hyze.factions.framework.FactionsCustomPlugin;
import net.hyze.factions.framework.misc.npc.impl.ranking.RankingNPC;
import net.hyze.factions.framework.settings.map.data.BannerFrame;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Getter
@Builder
public class MapSettings implements Listener {

    @Getter
    private static MapSettings instance = MapSettings.builder().build(true);

    @Singular
    private final Map<Class<? extends RankingNPC>, RankingNPC> rankings;

    @Singular
    private final List<BannerFrame> banners;

    @Singular
    private final List<CustomNPC> npcs;

    @Singular
    private final List<IMapSetup> setups;

    @Singular
    private final Map<Hologram, Location> holograms;

    private final boolean synchronizedTime;

    private static FactionsCustomPlugin plugin;
    private static BukkitTask synchronizedTimeTask;

    public void setup(FactionsCustomPlugin plugin) {
        MapSettings.plugin = plugin;

        NPCScoreboard.setup();

        this.rankings.values().forEach(RankingNPC::initialize);
//        this.banners.forEach(BannerFrame::setup);
        this.npcs.forEach(CustomNPC::spawn);
        this.holograms.forEach(Hologram::spawn);

        for (IMapSetup mapSetup : this.setups) {
            mapSetup.enable(plugin);
        }

        if (this.synchronizedTime) {

            if (Bukkit.getWorld(CoreSpigotSettings.getInstance().getWorldName()) != null) {
                synchronizedTimeTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    World world = Bukkit.getWorld(CoreSpigotSettings.getInstance().getWorldName());
                    if (world != null) {
                        world.setTime(getSynchronizedTime());
                    }
                }, 0, 20 * 5);
            }
        } else {
            World world = Bukkit.getWorld(CoreSpigotSettings.getInstance().getWorldName());

            if (world != null) {
                world.setTime(6000);
                world.setGameRuleValue("doDaylightCycle", "false");
            }
        }

        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        banners.forEach(BannerFrame::setup);
    }

    @EventHandler
    public void on(PluginDisableEvent event) {
        if (event.getPlugin().equals(MapSettings.plugin)) {

            HandlerList.unregisterAll(this);

            if (synchronizedTimeTask != null) {
                synchronizedTimeTask.cancel();
            }
        }
    }

    private long getSynchronizedTime() {
        double cycles = (double) Duration.between(LocalTime.MIDNIGHT, LocalTime.now()).toMillis() / TimeUnit.MINUTES.toMillis(20);
        double currentCycle = cycles - (int) cycles;
        return (long) (24000D * currentCycle);
    }

    public static Builder0 builder() {
        return new Builder0();
    }

    public static class Builder0 extends MapSettingsBuilder {

        private static boolean customBuild = false;

        private MapSettings build(boolean defaultBuild) {

            if (Builder0.customBuild) {
                throw new UnsupportedOperationException("MapSettings is already built.");
            }

            if (!defaultBuild) {
                Builder0.customBuild = true;
            }

            MapSettings settings = super.build();

            MapSettings.instance = settings;

            return settings;
        }

        @Override
        public MapSettings build() {
            return build(false);
        }
    }
}
