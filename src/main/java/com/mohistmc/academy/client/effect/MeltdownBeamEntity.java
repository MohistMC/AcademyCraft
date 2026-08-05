package com.mohistmc.academy.client.effect;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * 熔毁光束实体 — 客户端渲染的绿色高能射线。
 */
public class MeltdownBeamEntity extends Entity {

    private static final int MAX_LIFE = 20;

    private static final EntityDataAccessor<Float> START_X =
            SynchedEntityData.defineId(MeltdownBeamEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> START_Y =
            SynchedEntityData.defineId(MeltdownBeamEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> START_Z =
            SynchedEntityData.defineId(MeltdownBeamEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DIR_X =
            SynchedEntityData.defineId(MeltdownBeamEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DIR_Y =
            SynchedEntityData.defineId(MeltdownBeamEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DIR_Z =
            SynchedEntityData.defineId(MeltdownBeamEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> BEAM_LENGTH =
            SynchedEntityData.defineId(MeltdownBeamEntity.class, EntityDataSerializers.FLOAT);

    public MeltdownBeamEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.noCulling = true;
    }

    public void setBeam(Vec3 startPos, Vec3 direction, double length) {
        this.entityData.set(START_X, (float) startPos.x);
        this.entityData.set(START_Y, (float) startPos.y);
        this.entityData.set(START_Z, (float) startPos.z);
        this.entityData.set(DIR_X, (float) direction.x);
        this.entityData.set(DIR_Y, (float) direction.y);
        this.entityData.set(DIR_Z, (float) direction.z);
        this.entityData.set(BEAM_LENGTH, (float) length);
    }

    public Vec3 getStartPos() {
        return new Vec3(entityData.get(START_X), entityData.get(START_Y), entityData.get(START_Z));
    }

    public Vec3 getBeamDirection() {
        return new Vec3(entityData.get(DIR_X), entityData.get(DIR_Y), entityData.get(DIR_Z));
    }

    public double getBeamLength() {
        return entityData.get(BEAM_LENGTH);
    }

    public float getLifeProgress() {
        return Math.min((float) tickCount / MAX_LIFE, 1.0f);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(START_X, 0f);
        builder.define(START_Y, 0f);
        builder.define(START_Z, 0f);
        builder.define(DIR_X, 0f);
        builder.define(DIR_Y, 0f);
        builder.define(DIR_Z, 0f);
        builder.define(BEAM_LENGTH, 0f);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {}

    @Override
    public void tick() {
        super.tick();
        if (tickCount >= MAX_LIFE) discard();
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double dist) {
        return true;
    }
}
