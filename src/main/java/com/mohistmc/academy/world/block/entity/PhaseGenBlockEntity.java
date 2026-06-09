package com.mohistmc.academy.world.block.entity;

import com.mohistmc.academy.energy.api.block.IWirelessReceiver;
import com.mohistmc.academy.world.AcademyBlockEntities;
import com.mohistmc.academy.world.AcademyItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 相位液体生成器 —— 消耗 IF 能量生成 PhaseLiquid。
 * 实现 IWirelessReceiver 从无线能源网络获取能量。
 *
 * @author Mgazul
 */
public class PhaseGenBlockEntity extends AcademyContainerBlockEntity implements IWirelessReceiver {

    private static final int MAX_ENERGY = 20000;
    private static final int ENERGY_PER_LIQUID = 100;
    private static final double MAX_BANDWIDTH = 30;

    private float storedEnergy = 0;
    private int progress = 0;

    public PhaseGenBlockEntity(BlockPos pos, BlockState state) {
        super(AcademyBlockEntities.PHASE_GEN.get(), pos, state);
        setItems(NonNullList.withSize(getContainerSize(), ItemStack.EMPTY));
    }

    @Override
    public int getContainerSize() {
        return 2; // 输入槽(空 MatterUnit) + 输出槽(PhaseLiquid MatterUnit)
    }

    // ==================== IWirelessReceiver ====================

    @Override
    public double getRequiredEnergy() {
        return Math.min(MAX_BANDWIDTH, MAX_ENERGY - storedEnergy);
    }

    @Override
    public double injectEnergy(double amt) {
        double accepted = Math.min(amt, MAX_ENERGY - storedEnergy);
        storedEnergy += (float) accepted;
        setChanged();
        return amt - accepted;
    }

    @Override
    public double pullEnergy(double amt) {
        double pulled = Math.min(amt, storedEnergy);
        storedEnergy -= (float) pulled;
        setChanged();
        return pulled;
    }

    @Override
    public double getBandwidth() {
        return MAX_BANDWIDTH;
    }

    // ==================== Tick ====================

    public void tick() {
        if (level == null || level.isClientSide) return;

        if (storedEnergy >= ENERGY_PER_LIQUID) {
            progress++;
            if (progress >= 40) { // 2秒生产一个
                progress = 0;
                ItemStack input = getItems().get(0);
                ItemStack output = getItems().get(1);

                if (!input.isEmpty() && input.is(AcademyItems.MATTER_UNIT_NONE.get())) {
                    boolean canOutput = output.isEmpty()
                            || (output.is(AcademyItems.MATTER_UNIT_PHASE_LIQUID.get())
                            && output.getCount() < output.getMaxStackSize());

                    if (canOutput) {
                        storedEnergy -= ENERGY_PER_LIQUID;
                        input.shrink(1);
                        if (output.isEmpty()) {
                            getItems().set(1, new ItemStack(AcademyItems.MATTER_UNIT_PHASE_LIQUID.get()));
                        } else {
                            output.grow(1);
                        }
                        setChanged();
                    }
                }
            }
        } else {
            progress = 0;
        }
    }

    public float getStoredEnergy() { return storedEnergy; }
    public int getMaxEnergy() { return MAX_ENERGY; }
    public int getProgress() { return progress; }

    // ==================== NBT ====================

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("storedEnergy")) storedEnergy = tag.getFloat("storedEnergy");
        if (tag.contains("progress")) progress = tag.getInt("progress");
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putFloat("storedEnergy", storedEnergy);
        tag.putInt("progress", progress);
    }
}
