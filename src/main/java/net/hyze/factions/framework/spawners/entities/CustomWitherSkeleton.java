package net.hyze.factions.framework.spawners.entities;

import net.minecraft.server.v1_8_R3.EntitySkeleton;
import net.minecraft.server.v1_8_R3.World;

public class CustomWitherSkeleton extends EntitySkeleton {

    public CustomWitherSkeleton(World world) {
        super(world);

        setSkeletonType(1);
    }
}
