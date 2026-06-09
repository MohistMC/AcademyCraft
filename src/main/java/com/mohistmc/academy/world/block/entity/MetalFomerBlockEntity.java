package com.mohistmc.academy.world.block.entity;

import com.mohistmc.academy.crafting.MetalFormerRecipes;
import com.mohistmc.academy.crafting.MetalFormerRecipes.Mode;
import com.mohistmc.academy.crafting.MetalFormerRecipes.RecipeObject;
import com.mohistmc.academy.energy.api.block.IWirelessReceiver;
import com.mohistmc.academy.world.AcademyBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 金属成型机方块实体 —— 实现 IWirelessReceiver 从无线能源网络获取能量。
 * 支持四种模式：INCISE (切割)、ETCH (蚀刻)、PLATE (压板)、REFINE (精炼)。
 *
 * @author Mgazul
 */
public class MetalFomerBlockEntity extends AcademyContainerBlockEntity implements IWirelessReceiver {

    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 1;

    private static final int DEFAULT_PROCESS_TIME = 60;
    private static final double ENERGY_PER_OP = 10;
    private static final double MAX_BANDWIDTH = 50;

    private Mode currentMode = Mode.INCISE;
    private int progress = 0;
    private float storedEnergy = 0;

    public MetalFomerBlockEntity(BlockPos pos, BlockState state) {
        super(AcademyBlockEntities.METAL_FORMER.get(), pos, state);
        setItems(NonNullList.withSize(getContainerSize(), ItemStack.EMPTY));
    }

    @Override
    public int getContainerSize() {
        return 2;
    }

    // ==================== Mode ====================

    public Mode getMode() { return currentMode; }

    public void setMode(Mode mode) {
        this.currentMode = mode;
        this.progress = 0;
        setChanged();
    }

    public void cycleMode() {
        Mode[] values = Mode.values();
        int next = (currentMode.ordinal() + 1) % values.length;
        setMode(values[next]);
    }

    // ==================== IWirelessReceiver ====================

    @Override
    public double getRequiredEnergy() {
        if (progress > 0) return ENERGY_PER_OP;
        return 0;
    }

    @Override
    public double injectEnergy(double amt) {
        double accepted = Math.min(amt, ENERGY_PER_OP);
        storedEnergy += (float) accepted;
        return amt - accepted;
    }

    @Override
    public double pullEnergy(double amt) {
        return 0;
    }

    @Override
    public double getBandwidth() {
        return MAX_BANDWIDTH;
    }

    // ==================== Tick ====================

    public void tick() {
        if (level == null || level.isClientSide) return;

        ItemStack input = getItems().get(INPUT_SLOT);
        ItemStack output = getItems().get(OUTPUT_SLOT);

        if (input.isEmpty()) {
            progress = 0;
            return;
        }

        RecipeObject recipe = MetalFormerRecipes.INSTANCE.getRecipe(input, currentMode);
        if (recipe == null) {
            progress = 0;
            return;
        }

        // 检查输出
        ItemStack result = recipe.output;
        if (!output.isEmpty()
                && (!ItemStack.isSameItem(output, result)
                || output.getCount() + result.getCount() > output.getMaxStackSize())) {
            progress = 0;
            return;
        }

        // 需要能量
        if (storedEnergy < ENERGY_PER_OP) {
            return;
        }

        progress++;
        storedEnergy -= (float) ENERGY_PER_OP;

        if (progress >= DEFAULT_PROCESS_TIME) {
            progress = 0;
            input.shrink(recipe.input.getCount());
            if (output.isEmpty()) {
                getItems().set(OUTPUT_SLOT, result.copy());
            } else {
                output.grow(result.getCount());
            }
            setChanged();
        }
    }

    public int getProgress() { return progress; }
    public int getMaxProgress() { return DEFAULT_PROCESS_TIME; }
    public float getStoredEnergy() { return storedEnergy; }

    // ==================== NBT ====================

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("mode")) currentMode = Mode.valueOf(tag.getString("mode"));
        if (tag.contains("progress")) progress = tag.getInt("progress");
        if (tag.contains("storedEnergy")) storedEnergy = tag.getFloat("storedEnergy");
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putString("mode", currentMode.name());
        tag.putInt("progress", progress);
        tag.putFloat("storedEnergy", storedEnergy);
    }
}
