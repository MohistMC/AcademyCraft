package com.mohistmc.academy.world.block.entity;

import com.mohistmc.academy.capability.EnergyItemHelper;
import com.mohistmc.academy.capability.IFEnergyStorage;
import com.mohistmc.academy.energy.api.block.IWirelessGenerator;
import com.mohistmc.academy.world.AcademyBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class WindGenBaseBlockEntity extends AcademyContainerBlockEntity
        implements IFEnergyStorage, IWirelessGenerator {
    private boolean validBlock = false;
    private boolean validMiddle = false;

    // 风力发电基础速率
    private static final int GENERATION_RATE = 1;
    // 内部存储上限
    private static final int MAX_STORAGE = 20000;
    // 当前存储的能量
    private float storedEnergy = 0.0f;

    public WindGenBaseBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(AcademyBlockEntities.WINDGEN_BASE.get(), p_155229_, p_155230_);
        setItems(net.minecraft.core.NonNullList.withSize(getContainerSize(), ItemStack.EMPTY));
    }

    public void tick(boolean validBlock, boolean validMiddle, int mainHeight) {
        this.validBlock = validBlock;
        this.validMiddle = validMiddle;

        float oldEnergy = storedEnergy;

        if (validBlock) {
            // 所处海拔越高，产生能量越多：每超出基础高度1格，发电速率+1
            int generationRate = GENERATION_RATE + Math.max(0, mainHeight - 5);

            // 累积能量到存储池
            storedEnergy = Math.min(MAX_STORAGE, storedEnergy + generationRate);
        }

        // 无论结构是否有效，都尝试用存储池中的整数能量给能源单元充能
        int chargeAmount = (int) storedEnergy;
        if (chargeAmount > 0) {
            int charged = chargeEnergyUnit(chargeAmount);
            storedEnergy -= charged;
        }

        if (oldEnergy != storedEnergy) {
            setChanged();
            if (level != null) {
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
        if (stack.isEmpty() || !EnergyItemHelper.isEnergyItem(stack)) return 0;
        return EnergyItemHelper.receiveEnergy(stack, amount, false);
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    public boolean isValidMiddle() {
        return validMiddle;
    }

    public boolean isValidMain() {
        return validBlock;
    }

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
        return 50; // 风力发电机每 tick 最大输出
    }

    // ==================== IFEnergyStorage ====================

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

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = super.getUpdateTag(provider);
        tag.putFloat("storedEnergy", storedEnergy);
        return tag;
    }

    @Override
    public void loadAdditional(CompoundTag p_331149_, HolderLookup.Provider p_333170_) {
        super.loadAdditional(p_331149_, p_333170_);
        if (p_331149_.contains("storedEnergy")) {
            this.storedEnergy = p_331149_.getFloat("storedEnergy");
        }
    }

    @Override
    public void saveAdditional(CompoundTag p_187471_, HolderLookup.Provider p_327783_) {
        super.saveAdditional(p_187471_, p_327783_);
        p_187471_.putFloat("storedEnergy", storedEnergy);
    }
}
