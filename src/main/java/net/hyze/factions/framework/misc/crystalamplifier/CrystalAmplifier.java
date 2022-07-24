package net.hyze.factions.framework.misc.crystalamplifier;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.hyze.core.spigot.misc.hologram.Hologram;
import net.hyze.core.spigot.misc.hologram.HologramPosition;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;

@Getter
@RequiredArgsConstructor
public class CrystalAmplifier {

    private final int factionId;
    private final Long endTime;
    private final Location location;

    @Setter
    private Hologram hologram;

    @Setter
    private EnderCrystal enderCrystal;

    public void spawn() {

        this.hologram = new Hologram(HologramPosition.DOWN);

        this.hologram.line("&b&lCRISTAL AMPLIFICADOR");
        this.hologram.line("&eClique para abrir.");

        this.hologram.spawn(this.location.clone().add(0, 1.5, 0));

        this.enderCrystal = (EnderCrystal) this.location.getWorld().spawnEntity(this.location, EntityType.ENDER_CRYSTAL);

    }

    public void destroy() {

        if (this.hologram != null) {
            this.hologram.destroy();
        }

        this.location.getWorld().playSound(this.location, Sound.ENDERDRAGON_WINGS, 5, 1);
        this.location.getWorld().playSound(this.location, Sound.EXPLODE, 5, 1);
        this.location.getWorld().spigot().playEffect(this.location, Effect.EXPLOSION_HUGE);

        if (this.enderCrystal != null) {
            this.enderCrystal.remove();
        }

    }

}
