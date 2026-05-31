package com.mohistmc.academy.world.block.entity;

import com.mohistmc.academy.world.AcademyBlockEntities;
import com.mohistmc.academy.world.AcademyItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class ImagFusorBlockEntity extends AcademyContainerBlockEntity {
    public static final int FLUID_INPUT_SLOT = 0;
    public static final int EMPTY_UNIT_SLOT = 1;
    public static final int INPUT_SLOT = 2;
    public static final int OUTPUT_SLOT = 3;

    private static final int MAX_FLUID = 8000;
    private static final int RECIPE_LOW_TO_NORMAL = 3000;
    private static final int RECIPE_NORMAL_TO_PURE = 8000;
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

    public int getFluidAmount() {
        return fluidAmount;
    }

    public int getProcessingTime() {
        return processingTime;
    }

    public int getMaxFluid() {
        return MAX_FLUID;
    }

    public int getProcessingDuration() {
        return PROCESSING_DURATION;
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("fluidAmount")) {
            this.fluidAmount = tag.getInt("fluidAmount");
        }
        if (tag.contains("processingTime")) {
            this.processingTime = tag.getInt("processingTime");
        }
        NonNullList<ItemStack> items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        CompoundTag contentItems = tag.getCompound("contentItems");
        for (int i = 0; i < getContainerSize(); i++) {
            if (contentItems.contains(String.valueOf(i))) {
                items.set(i, ItemStack.parse(provider, contentItems.getCompound(String.valueOf(i))).orElse(ItemStack.EMPTY));
            }
        }
        setItems(items);
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt("fluidAmount", fluidAmount);
        tag.putInt("processingTime", processingTime);
        CompoundTag contentItems = new CompoundTag();
        NonNullList<ItemStack> items = getItems();
        for (int i = 0; i < getContainerSize(); i++) {
            if (!items.get(i).isEmpty()) {
                contentItems.put(String.valueOf(i), items.get(i).save(provider));
            }
        }
        tag.put("contentItems", contentItems);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        ItemStack fluidInput = getItems().get(FLUID_INPUT_SLOT);

        if (!fluidInput.isEmpty() && fluidInput.is(AcademyItems.MATTER_UNIT_PHASE_LIQUID.get())) {
            if (fluidAmount + 1000 <= MAX_FLUID) {
                fluidAmount += 1000;
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

        int requiredFluid = 0;
        ItemStack result = ItemStack.EMPTY;

        if (input.is(AcademyItems.CRYSTAL_LOW.get())) {
            requiredFluid = RECIPE_LOW_TO_NORMAL;
            result = new ItemStack(AcademyItems.CRYSTAL_NORMAL.get());
        } else if (input.is(AcademyItems.CRYSTAL_NORMAL.get())) {
            requiredFluid = RECIPE_NORMAL_TO_PURE;
            result = new ItemStack(AcademyItems.CRYSTAL_PURE.get());
        }

        if (requiredFluid == 0 || result.isEmpty()) {
            processingTime = 0;
            return;
        }

        if (fluidAmount < requiredFluid) {
            processingTime = 0;
            return;
        }

        if (!output.isEmpty() && (!output.is(result.getItem()) || output.getCount() >= output.getMaxStackSize())) {
            processingTime = 0;
            return;
        }

        processingTime++;
        if (processingTime >= PROCESSING_DURATION) {
            processingTime = 0;
            fluidAmount -= requiredFluid;
            input.shrink(1);
            if (output.isEmpty()) {
                getItems().set(OUTPUT_SLOT, result.copy());
            } else {
                output.grow(1);
            }
            setChanged();
        }
    }
}
