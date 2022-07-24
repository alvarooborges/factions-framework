package net.hyze.factions.framework.war.entities;

import lombok.Getter;
import net.hyze.core.spigot.misc.utils.NMS;
import net.hyze.factions.framework.war.War;
import net.minecraft.server.v1_8_R3.EntityWither;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Wither;
import org.bukkit.event.entity.CreatureSpawnEvent;

@Getter
public class WarCustomWither extends EntityWither {

    public WarCustomWither(World world) {
        super(world);
    }

    public WarCustomWither() {
        super(((CraftWorld) War.CONFIG.getWorld()).getHandle());
    }

    public Wither spawn(Location location) {

        NMS.clearEntitySelectors(this);

        this.setLocation(
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch()
        );

        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0);

        this.getWorld().addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);

        return (Wither) this.getBukkitEntity();
    }

    @Override
    public boolean cm() {
        return true;
    }

    @Override
    public void move(double x, double y, double z) {
    }
    
}
