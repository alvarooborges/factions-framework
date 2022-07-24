package net.hyze.factions.framework.spawners.entities;

import net.minecraft.server.v1_8_R3.*;

public class CustomMagmaCube extends EntityMagmaCube {

    public CustomMagmaCube(World world) {
        super(world);
    }

    @Override
    protected void e(EntityLiving entityliving) {

    }

    @Override
    public void setSize(int i) {
        this.datawatcher.watch(16, (byte) i);
        this.setSize(0.51000005F * (float) i, 0.51000005F * (float) i);
        this.setPosition(this.locX, this.locY, this.locZ);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.2F + 0.1F * (float) i);
        this.setHealth(this.getMaxHealth());
        this.b_ = i;
    }

    @Override
    public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, GroupDataEntity groupdataentity) {
        GroupDataEntity out = super.prepare(difficultydamagescaler, groupdataentity);
        setSize(2);
        return out;
    }

}
