package com.mohistmc.academy.client.effect;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * 能量护盾实体 — 半透明发光球形/圆形护盾。
 */
public class ShieldEffectEntity extends Entity {

    private static final int MAX_LIFE = 60;

    private static final EntityDataAccessor<Float> SHIELD_RADIUS =
            SynchedEntityData.defineId(ShieldEffectEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> SHIELD_COLOR =
            SynchedEntityData.defineId(ShieldEffectEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> LAYER_COUNT =
            SynchedEntityData.defineId(ShieldEffectEntity.class, EntityDataSerializers.INT);

    public ShieldEffectEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.noCulling = true;
    }

    public ShieldEffectEntity setData(float radius, int color, int layers) {
        entityData.set(SHIELD_RADIUS, radius);
        entityData.set(SHIELD_COLOR, color);
        entityData.set(LAYER_COUNT, layers);
        return this;
    }

    public float getRadius() { return entityData.get(SHIELD_RADIUS); }
    public int getColor() { return entityData.get(SHIELD_COLOR); }
    public int getLayerCount() { return entityData.get(LAYER_COUNT); }

    public float getLifeProgress() {
        return Math.min((float) tickCount / MAX_LIFE, 1.0f);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(SHIELD_RADIUS, 2.0f);
        builder.define(SHIELD_COLOR, 0xFF44AAFF);
        builder.define(LAYER_COUNT, 3);
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
