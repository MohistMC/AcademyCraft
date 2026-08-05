package com.mohistmc.academy.world.entity;

import com.mohistmc.academy.world.AcademyItems;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CoinEntity extends Entity {
    private static final EntityDataAccessor<Float> SPIN_SPEED =
            SynchedEntityData.defineId(CoinEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> LIFETIME =
            SynchedEntityData.defineId(CoinEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> THROWER_UUID =
            SynchedEntityData.defineId(CoinEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> IS_RETURNING =
            SynchedEntityData.defineId(CoinEntity.class, EntityDataSerializers.BOOLEAN);

    private static final Map<UUID, CoinEntity> playerCoins = new HashMap<>();

    private float rotationY = 0;
    private float rotationX = 0;
    private float rotationZ = 0;
    private int returnTimer = 0;
    private boolean hasReachedApex = false;

    public CoinEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.setSpinSpeed(20.0F + random.nextFloat() * 10.0F);
        this.setLifetime(100);
        this.setNoGravity(false);
        this.setIsReturning(false);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(SPIN_SPEED, 20.0F);
        builder.define(LIFETIME, 100);
        builder.define(THROWER_UUID, "");
        builder.define(IS_RETURNING, false);
    }

    public static boolean hasPlayerCoinInAir(Player player) {
        CoinEntity coin = playerCoins.get(player.getUUID());
        return coin != null && coin.isAlive();
    }

    @Override
    public void tick() {
        super.tick();

        float spinSpeed = getSpinSpeed();
        rotationY += spinSpeed;
        rotationX += spinSpeed * 0.7F;
        rotationZ += spinSpeed * 0.3F;

        if (rotationY >= 360.0F) rotationY -= 360.0F;
        if (rotationX >= 360.0F) rotationX -= 360.0F;
        if (rotationZ >= 360.0F) rotationZ -= 360.0F;

        Player thrower = getThrower();

        if (!level().isClientSide) {
            if (!hasReachedApex && getDeltaMovement().y < 0) {
                hasReachedApex = true;
            }

            if (!isReturning() && (getLifetime() <= 0 ||
                    (hasReachedApex && thrower != null && this.getY() <= thrower.getEyeY() + 0.5) ||
                    (thrower != null && thrower.distanceToSqr(this) > 100.0D))) {
                if (thrower != null) {
                    returnCoinToPlayer(thrower);
                } else {
                    startReturning();
                }
            }

            if (isReturning()) {
                returnTimer++;
                if (returnTimer > 40) {
                    this.discard();
                }
            }

            if (!isReturning()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
                this.move(net.minecraft.world.entity.MoverType.SELF, this.getDeltaMovement());

                int lifetime = getLifetime() - 1;
                setLifetime(lifetime);
            }
        }
    }

    private void startReturning() {
        this.setIsReturning(true);
        this.returnTimer = 0;
        this.setNoGravity(true);
    }

    private void returnCoinToPlayer(Player player) {
        if (!player.getAbilities().instabuild) {
            boolean added = player.getInventory().add(new ItemStack(AcademyItems.COIN.get()));
            if (!added && !level().isClientSide) {
                player.drop(new ItemStack(AcademyItems.COIN.get()), false);
            }
        }

        playerCoins.remove(player.getUUID());
        this.discard();
    }

    private double getThrowerEyeHeight() {
        Player thrower = getThrower();
        return thrower != null ? thrower.getEyeY() : this.getY();
    }

    public Player getThrower() {
        String uuidStr = this.entityData.get(THROWER_UUID);
        if (uuidStr.isEmpty()) return null;

        try {
            UUID uuid = UUID.fromString(uuidStr);
            return level().getPlayerByUUID(uuid);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public void setThrower(Player player) {
        this.entityData.set(THROWER_UUID, player.getUUID().toString());
        playerCoins.put(player.getUUID(), this);
    }

    public float getRotationY() {
        return rotationY;
    }

    public float getRotationX() {
        return rotationX;
    }

    public float getRotationZ() {
        return rotationZ;
    }

    public float getSpinSpeed() {
        return this.entityData.get(SPIN_SPEED);
    }

    public void setSpinSpeed(float speed) {
        this.entityData.set(SPIN_SPEED, speed);
    }

    public int getLifetime() {
        return this.entityData.get(LIFETIME);
    }

    public void setLifetime(int lifetime) {
        this.entityData.set(LIFETIME, lifetime);
    }

    public boolean isReturning() {
        return this.entityData.get(IS_RETURNING);
    }

    public void setIsReturning(boolean returning) {
        this.entityData.set(IS_RETURNING, returning);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.contains("SpinSpeed")) {
            setSpinSpeed(compound.getFloat("SpinSpeed"));
        }
        if (compound.contains("Lifetime")) {
            setLifetime(compound.getInt("Lifetime"));
        }
        if (compound.contains("ThrowerUUID")) {
            this.entityData.set(THROWER_UUID, compound.getString("ThrowerUUID"));
        }
        if (compound.contains("RotationY")) {
            rotationY = compound.getFloat("RotationY");
        }
        if (compound.contains("RotationX")) {
            rotationX = compound.getFloat("RotationX");
        }
        if (compound.contains("RotationZ")) {
            rotationZ = compound.getFloat("RotationZ");
        }
        if (compound.contains("IsReturning")) {
            setIsReturning(compound.getBoolean("IsReturning"));
        }
        if (compound.contains("ReturnTimer")) {
            returnTimer = compound.getInt("ReturnTimer");
        }
        if (compound.contains("HasReachedApex")) {
            hasReachedApex = compound.getBoolean("HasReachedApex");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putFloat("SpinSpeed", getSpinSpeed());
        compound.putInt("Lifetime", getLifetime());
        compound.putString("ThrowerUUID", this.entityData.get(THROWER_UUID));
        compound.putFloat("RotationY", rotationY);
        compound.putFloat("RotationX", rotationX);
        compound.putFloat("RotationZ", rotationZ);
        compound.putBoolean("IsReturning", isReturning());
        compound.putInt("ReturnTimer", returnTimer);
        compound.putBoolean("HasReachedApex", hasReachedApex);
    }

    @Override
    public void remove(RemovalReason reason) {
        Player thrower = getThrower();
        if (thrower != null) {
            playerCoins.remove(thrower.getUUID());
        }
        super.remove(reason);
    }

    @Override
    public boolean isPickable() {
        return false;
    }
}
