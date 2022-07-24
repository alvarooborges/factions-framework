package net.hyze.factions.framework.entities;

import net.hyze.factions.framework.FactionsPlugin;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftCreeper;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.util.UnsafeList;
import org.bukkit.metadata.FixedMetadataValue;

import java.lang.reflect.Field;

public class CreeperEntity extends EntityCreeper {

    public CreeperEntity(World world) {
        super(world);

        try {
            Field bField = PathfinderGoalSelector.class.getDeclaredField("b");

            bField.setAccessible(true);
            bField.set(goalSelector, new UnsafeList<>());
            bField.set(targetSelector, new UnsafeList<>());

            Field cField = PathfinderGoalSelector.class.getDeclaredField("c");

            cField.setAccessible(true);
            cField.set(goalSelector, new UnsafeList<>());
            cField.set(targetSelector, new UnsafeList<>());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.0D);

    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (damagesource instanceof EntityDamageSource) {
            return super.damageEntity(damagesource, f);
        }

        return false;
    }

    @Override
    public CraftEntity getBukkitEntity() {
        if (bukkitEntity == null) {
            bukkitEntity = new NormalCraftCreeper();
        }

        return bukkitEntity;
    }

    @Override
    protected void burnFromLava() {
    }

    @Override
    protected void burn(float i) {
    }

    private class NormalCraftCreeper extends CraftCreeper {

        public NormalCraftCreeper() {
            super((CraftServer) Bukkit.getServer(), CreeperEntity.this);

            setMetadata("showHealthOnName", new FixedMetadataValue(FactionsPlugin.getInstance(), true));
            setMetadata("obsidiandestroyer:damage", new FixedMetadataValue(FactionsPlugin.getInstance(), 1));

            setMaxHealth(10);
            setHealth(10);
        }

        @Override
        public boolean isPowered() {
            return false;
        }

        @Override
        public void setPowered(boolean powered) {
            getHandle().setPowered(false);
        }
    }
}
