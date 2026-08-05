package com.mohistmc.academy.world.block.entity;

import com.mohistmc.academy.capability.EnergyItemHelper;
import com.mohistmc.academy.capability.IFEnergyStorage;
import com.mohistmc.academy.energy.api.block.IWirelessGenerator;
import com.mohistmc.academy.world.AcademyBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 太阳能发电机 —— 实现 IWirelessGenerator 接口以向 IF 能源网络供电。
 * @author Mgazul
 */
public class SolarGenBlockEntity extends AcademyContainerBlockEntity
        implements IFEnergyStorage, IWirelessGenerator {

    private static final float STRONG_RATE = 3.0f;
    private static final float WEAK_RATE = 0.6f;
    private static final int MAX_STORAGE = 1000;

    private float storedEnergy = 0.0f;

    public SolarGenBlockEntity(BlockPos pos, BlockState state) {
        super(AcademyBlockEntities.SOLAR_GEN.get(), pos, state);
        setItems(NonNullList.withSize(getContainerSize(), ItemStack.EMPTY));
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;
        if (!level.canSeeSky(pos)) return;

        float rate = switch (getStatus()) {
            case STRONG -> STRONG_RATE;
            case WEAK -> WEAK_RATE;
            case STOPPED -> 0.0f;
        };

        float oldEnergy = storedEnergy;
        if (rate > 0.0f) {
            storedEnergy = Math.min(MAX_STORAGE, storedEnergy + rate);
        }

        int chargeAmount = (int) storedEnergy;
        if (chargeAmount > 0) {
            int charged = chargeEnergyUnit(chargeAmount);
            storedEnergy -= charged;
        }

        if (oldEnergy != storedEnergy || rate > 0.0f) {
            setChanged();
            if (oldEnergy != storedEnergy) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    private int chargeEnergyUnit(int amount) {
        ItemStack stack = getItems().getFirst();
        if (stack.isEmpty() || !EnergyItemHelper.isEnergyItem(stack)) return 0;
        return EnergyItemHelper.receiveEnergy(stack, amount, false);
    }

    // ==================== IFEnergyStorage ====================

    @Override
    public int getEnergyStored() { return (int) storedEnergy; }

    @Override
    public int getMaxEnergyStored() { return MAX_STORAGE; }

    @Override
    public void setEnergy(int energy) {
        this.storedEnergy = Math.min(MAX_STORAGE, Math.max(0, energy));
        setChanged();
    }

    public float getStoredEnergy() { return storedEnergy; }

    // ==================== IWirelessGenerator ====================

    @Override
    public double getProvidedEnergy(double req) {
        double give = Math.min(req, storedEnergy);
        storedEnergy -= (float) give;
        if (give > 0) setChanged();
        return give;
    }

    @Override
    public double getBandwidth() {
        return 20; // 每 tick 最大输出 20 IF
    }

    // ==================== NBT ====================

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("storedEnergy")) {
            this.storedEnergy = tag.getFloat("storedEnergy");
        }
        deserializeEnergy(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putFloat("storedEnergy", storedEnergy);
        serializeEnergy(tag);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = super.getUpdateTag(provider);
        tag.putFloat("storedEnergy", storedEnergy);
        return tag;
    }

    // ==================== Solar State ====================

    public enum SolarStatus { STRONG, STOPPED, WEAK }

    public SolarStatus getStatus() {
        if (level == null) return SolarStatus.STOPPED;
        long time = level.getDayTime() % 24000;
        if (time >= 12000) return SolarStatus.STOPPED;
        if (level.isRaining() || level.isThundering()) return SolarStatus.WEAK;
        return SolarStatus.STRONG;
    }
}
