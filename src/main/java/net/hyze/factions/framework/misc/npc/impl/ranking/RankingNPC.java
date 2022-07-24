package net.hyze.factions.framework.misc.npc.impl.ranking;

import co.aikar.timings.Timing;
import co.aikar.timings.Timings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.skins.SkinRecord;
import net.hyze.core.spigot.misc.hologram.Hologram;
import net.hyze.core.spigot.misc.hologram.HologramPosition;
import net.hyze.core.spigot.misc.npc.CustomNPC;
import net.hyze.core.spigot.misc.utils.HeadTexture;
import net.hyze.core.spigot.misc.utils.LocationUtils;
import net.hyze.factions.framework.FactionsPlugin;
import net.hyze.factions.framework.faction.Faction;
import net.hyze.factions.framework.misc.utils.FactionUtils;
import net.hyze.factions.framework.user.FactionUser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public abstract class RankingNPC {

    private final String name;
    private final Vector hologramLocation;
    private final Vector locationLookAt;

    private final Set<Information> cache = Sets.newHashSet();

    Map<String, Timing> timings = Maps.newConcurrentMap();

    public RankingNPC position(Vector location, Faction faction, String value, int rank) {
        this.cache.add(new Information(location, faction, value, rank));
        return this;
    }

    public abstract void populate();

    public void initialize() {
        Hologram hologram = new Hologram(HologramPosition.DOWN).line(this.name);
        hologram.spawn(LocationUtils.center(this.hologramLocation.toLocation(Bukkit.getWorld("world"))));

        updateNPCs();
    }

    public void updateNPCs() {
        Timing timing = timings.getOrDefault(
                RankingNPC.this.getClass().getName(),
                Timings.of(
                        FactionsPlugin.getInstance(),
                        "RankingNPC: " + RankingNPC.this.getClass().getSimpleName()
                )
        );

        timing.startTimingIfSync();

        // Remove todos NPCs e hologramas.
        cache.forEach(Information::destroy);

        // Spawna todos NPCs e hologramas.
        populate();

        timing.stopTimingIfSync();
    }

    @Setter
    @Getter
    private static class Information {

        private final Vector location;
        private final Faction faction;
        private final String value;

        private final ArmorStand stand;
        private final Hologram hologram;

        public Information(Vector location, Faction faction, String value, int rank) {
            this.location = location;
            this.faction = faction;
            this.value = value;

            Location bukkitLocation = location.toLocation(Bukkit.getWorld("world"));

            bukkitLocation.setPitch(10);
            bukkitLocation.setYaw(-45);

            FactionUser user = FactionUtils.getLeader(faction);

            this.stand = bukkitLocation.getWorld().spawn(bukkitLocation, ArmorStand.class);

            this.stand.setCustomNameVisible(false);
            this.stand.setSmall(false);
            this.stand.setBasePlate(false);
            this.stand.setGravity(false);
            this.stand.setArms(true);

            this.stand.setHelmet(HeadTexture.getPlayerHead(user.getNick()));

            if (rank == 1) {
                this.stand.setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
                this.stand.setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
                this.stand.setBoots(new ItemStack(Material.DIAMOND_BOOTS));
                this.stand.setItemInHand(new ItemStack(Material.DIAMOND_SWORD));
            } else if (rank == 2) {
                this.stand.setChestplate(new ItemStack(Material.GOLD_CHESTPLATE));
                this.stand.setLeggings(new ItemStack(Material.GOLD_LEGGINGS));
                this.stand.setBoots(new ItemStack(Material.GOLD_BOOTS));
                this.stand.setItemInHand(new ItemStack(Material.GOLD_SWORD));
            } else {
                this.stand.setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
                this.stand.setLeggings(new ItemStack(Material.IRON_LEGGINGS));
                this.stand.setBoots(new ItemStack(Material.IRON_BOOTS));
                this.stand.setItemInHand(new ItemStack(Material.IRON_SWORD));
            }

            this.hologram = new Hologram(HologramPosition.DOWN)
                    .line("&7" + this.faction.getDisplayName())
                    .line("&eLíder " + user.getHandle().getHighestGroup().getColor() + user.getNick())
                    .line("&a" + this.value);

            this.hologram.spawn(bukkitLocation.clone().add(0, 2.7, 0));
        }

        public void destroy() {
            this.stand.remove();
            this.hologram.destroy();
        }
    }
}
