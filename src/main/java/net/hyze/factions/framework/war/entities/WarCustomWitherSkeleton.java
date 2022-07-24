package net.hyze.factions.framework.war.entities;

import lombok.Getter;
import net.hyze.core.spigot.misc.utils.NMS;
import net.hyze.end.creatures.api.CreatureUtils;
import net.hyze.factions.framework.war.War;
import net.minecraft.server.v1_8_R3.EntitySkeleton;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.entity.CreatureSpawnEvent;

@Getter
public class WarCustomWitherSkeleton extends EntitySkeleton {

    public WarCustomWitherSkeleton(World world) {
        super(world);
    }

    public WarCustomWitherSkeleton() {
        super(((CraftWorld) War.CONFIG.getWorld()).getHandle());
    }

    public Skeleton spawn(Location location) {
        setSkeletonType(1);
        
        NMS.clearEntitySelectors(this);
        CreatureUtils.giveBasePathfinders(this);
        CreatureUtils.giveAggressivePathfinders(this);

        this.setLocation(
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch()
        );

        this.getWorld().addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);

        return (Skeleton) this.getBukkitEntity();
    }

}
