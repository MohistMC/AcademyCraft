package com.mohistmc.academy.world.block.entity;

import com.mohistmc.academy.crafting.ImagFusorRecipes;
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
 * 想象熔炉方块实体 —— 可实现 IWirelessReceiver 从 IF 能源网络获取能量。
 * @author Mgazul
 */
public class ImagFusorBlockEntity extends AcademyContainerBlockEntity implements IWirelessReceiver {

    public static final int FLUID_INPUT_SLOT = 0;
    public static final int EMPTY_UNIT_SLOT = 1;
    public static final int INPUT_SLOT = 2;
    public static final int OUTPUT_SLOT = 3;

    private static final int MAX_FLUID = 8000;
    private static final int PHASE_LIQUID_PER_UNIT = 1000;
    private static final int PROCESSING_DURATION = 100;

    private int fluidAmount = 0;
    private int processingTime = 0;

    public ImagFusorBlockEntity(BlockPos pos, BlockState state) {
        super(AcademyBlockEntities.IMAG_FUSOR.get(), pos, state);
        setItems(NonNullList.withSize(getContainerSize(), ItemStack.EMPTY));
    }

    @Override
    public int getContainerSize() {
        return 4;
    }

    public int getFluidAmount() { return fluidAmount; }
    public int getProcessingTime() { return processingTime; }
    public int getMaxFluid() { return MAX_FLUID; }
    public int getProcessingDuration() { return PROCESSING_DURATION; }

    // ==================== IWirelessReceiver ====================

    @Override
    public double getRequiredEnergy() {
        if (processingTime > 0) return 20;
        return 0;
    }

    @Override
    public double injectEnergy(double amt) {
        // 当前使用 PhaseLiquid 而非 IF 能量驱动，保留接口以备后续扩展
        return amt;
    }

    @Override
    public double pullEnergy(double amt) {
        return 0;
    }

    @Override
    public double getBandwidth() {
        return 50;
    }

    // ==================== Tick ====================

    public void tick() {
        if (level == null || level.isClientSide) return;

        ItemStack fluidInput = getItems().get(FLUID_INPUT_SLOT);
        if (!fluidInput.isEmpty() && fluidInput.is(AcademyItems.MATTER_UNIT_PHASE_LIQUID.get())) {
            if (fluidAmount + PHASE_LIQUID_PER_UNIT <= MAX_FLUID) {
                fluidAmount += PHASE_LIQUID_PER_UNIT;
                fluidInput.shrink(1);
                ItemStack emptySlot = getItems().get(EMPTY_UNIT_SLOT);
                if (emptySlot.isEmpty()) {
                    getItems().set(EMPTY_UNIT_SLOT, new ItemStack(AcademyItems.MATTER_UNIT_NONE.get()));
                } else if (emptySlot.is(AcademyItems.MATTER_UNIT_NONE.get()) && emptySlot.getCount() < emptySlot.getMaxStackSize()) {
                    emptySlot.grow(1);
                }
                setChanged();
            }
        }

        ItemStack input = getItems().get(INPUT_SLOT);
        ItemStack output = getItems().get(OUTPUT_SLOT);

        if (input.isEmpty()) {
            processingTime = 0;
            return;
        }

        ImagFusorRecipes.IFRecipe recipe = ImagFusorRecipes.INSTANCE.getRecipe(input);
        if (recipe == null) {
            processingTime = 0;
            return;
        }

        if (fluidAmount < recipe.phaseLiquid()) {
            processingTime = 0;
            return;
        }

        ItemStack result = recipe.output();
        if (!output.isEmpty() && (!ItemStack.isSameItem(output, result)
                || output.getCount() >= output.getMaxStackSize())) {
            processingTime = 0;
            return;
        }

        processingTime++;
        if (processingTime >= PROCESSING_DURATION) {
            processingTime = 0;
            fluidAmount -= recipe.phaseLiquid();
            input.shrink(1);
            if (output.isEmpty()) {
                getItems().set(OUTPUT_SLOT, result.copy());
            } else {
                output.grow(1);
            }
            setChanged();
        }
    }

    // ==================== NBT ====================

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("fluidAmount")) fluidAmount = tag.getInt("fluidAmount");
        if (tag.contains("processingTime")) processingTime = tag.getInt("processingTime");
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt("fluidAmount", fluidAmount);
        tag.putInt("processingTime", processingTime);
    }
}
