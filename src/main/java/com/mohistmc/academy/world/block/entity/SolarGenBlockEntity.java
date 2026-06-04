package com.mohistmc.academy.world.block.entity;

import com.mohistmc.academy.capability.EnergyItemHelper;
import com.mohistmc.academy.capability.IFEnergyStorage;
import com.mohistmc.academy.world.AcademyBlockEntities;
import com.mohistmc.academy.world.AcademyItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class SolarGenBlockEntity extends AcademyContainerBlockEntity implements IFEnergyStorage {
    // 发电速率（IF/tick）
    private static final float STRONG_RATE = 3.0f;   // 晴天
    private static final float WEAK_RATE = 0.6f;     // 雨天
    // 内部存储上限
    private static final int MAX_STORAGE = 1000;

    // 当前存储的能量（支持小数累积）
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

        // 太阳能板必须有天空光照
        if (!level.canSeeSky(pos)) return;

        // 获取当前发电速率
        float rate = switch (getStatus()) {
            case STRONG -> STRONG_RATE;   // 晴天：3 IF/t
            case WEAK -> WEAK_RATE;       // 雨天：0.6 IF/t
            case STOPPED -> 0.0f;         // 夜晚：不发电
        };

        float oldEnergy = storedEnergy;

        // 将新产生的能量加入存储池
        if (rate > 0.0f) {
            storedEnergy = Math.min(MAX_STORAGE, storedEnergy + rate);
        }

        // 尝试用存储池中的整数能量给能源单元充能
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

    /**
     * 给槽位中的能源单元充能
     * @param amount 充能数量
     * @return 实际充能数量
     */
    private int chargeEnergyUnit(int amount) {
        ItemStack stack = getItems().getFirst();
        if (stack.isEmpty() || !stack.is(AcademyItems.ENERGY_UNIT.get())) return 0;

        int energy = EnergyItemHelper.getEnergy(stack);
        if (energy >= EnergyItemHelper.getEnergy(stack) && stack.getDamageValue() <= 0) return 0;

        return EnergyItemHelper.receiveEnergy(stack, amount, false);
    }

    @Override
    public int getEnergyStored() {
        return (int) storedEnergy;
    }

    @Override
    public int getMaxEnergyStored() {
        return MAX_STORAGE;
    }

    @Override
    public void setEnergy(int energy) {
        this.storedEnergy = Math.min(MAX_STORAGE, Math.max(0, energy));
        setChanged();
    }

    public float getStoredEnergy() {
        return storedEnergy;
    }

    public float getMaxStorage() {
        return MAX_STORAGE;
    }

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

    public enum SolarStatus { STRONG, STOPPED, WEAK }

    public SolarStatus getStatus() {
        if (level == null) return SolarStatus.STOPPED;

        long time = level.getDayTime() % 24000;
        if (time >= 12000) return SolarStatus.STOPPED; // 12000 太阳落山，月亮出现

        if (level.isRaining() || level.isThundering()) return SolarStatus.WEAK;
        return SolarStatus.STRONG;
    }
}
