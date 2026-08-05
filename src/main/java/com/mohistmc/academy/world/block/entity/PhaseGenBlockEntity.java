package com.mohistmc.academy.world.block.entity;

import com.mohistmc.academy.energy.api.block.IWirelessGenerator;
import com.mohistmc.academy.world.AcademyBlockEntities;
import com.mohistmc.academy.world.AcademyItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 虚相位能量发生机 —— 消耗 PhaseLiquid 单元产生 IF 能量,实现 IWirelessGenerator 向无线能源网络供电。
 * @author Mgazul
 */
public class PhaseGenBlockEntity extends AcademyContainerBlockEntity implements IWirelessGenerator {

    private static final int PROCESS_TICKS = 40;       // 2秒处理一个单元
    private static final int ENERGY_PER_LIQUID = 200;  // 每个液相单元产生 200 IF
    private static final double MAX_BANDWIDTH = 30;
    private static final int MAX_STORAGE = 5000;

    private int progress = 0;
    private float storedEnergy = 0;

    public PhaseGenBlockEntity(BlockPos pos, BlockState state) {
        super(AcademyBlockEntities.PHASE_GEN.get(), pos, state);
        setItems(NonNullList.withSize(getContainerSize(), ItemStack.EMPTY));
    }

    @Override
    public int getContainerSize() {
        return 2; // 0=输入(PhaseLiquid单元), 1=输出(空单元)
    }

    // ==================== Tick Logic ====================

    public void tick() {
        if (level == null || level.isClientSide) return;

        ItemStack input = getItems().get(0);
        ItemStack output = getItems().get(1);

        if (!input.isEmpty() && input.is(AcademyItems.MATTER_UNIT_PHASE_LIQUID.get())) {
            boolean canOutput = output.isEmpty()
                    || (output.is(AcademyItems.MATTER_UNIT_NONE.get())
                    && output.getCount() < output.getMaxStackSize());

            if (canOutput && storedEnergy < MAX_STORAGE) {
                progress++;
                if (progress >= PROCESS_TICKS) {
                    progress = 0;
                    storedEnergy += ENERGY_PER_LIQUID;
                    input.shrink(1);
                    if (output.isEmpty()) {
                        getItems().set(1, new ItemStack(AcademyItems.MATTER_UNIT_NONE.get()));
                    } else {
                        output.grow(1);
                    }
                    setChanged();
                }
            } else {
                progress = 0;
            }
        } else {
            progress = 0;
        }
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
        return MAX_BANDWIDTH;
    }

    // ==================== Accessors ====================

    public float getStoredEnergy() { return storedEnergy; }
    public int getMaxEnergy() { return MAX_STORAGE; }
    public int getProgress() { return progress; }
    public int getProcessTicks() { return PROCESS_TICKS; }

    /** 是否正在工作中 */
    public boolean isWorking() {
        if (getItems().isEmpty()) return false;
        ItemStack input = getItems().get(0);
        return !input.isEmpty() && input.is(AcademyItems.MATTER_UNIT_PHASE_LIQUID.get());
    }

    // ==================== NBT ====================

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("progress")) progress = tag.getInt("progress");
        if (tag.contains("storedEnergy")) storedEnergy = tag.getFloat("storedEnergy");
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt("progress", progress);
        tag.putFloat("storedEnergy", storedEnergy);
    }
}
