package com.mohistmc.academy.world.block.entity;

import com.mohistmc.academy.capability.EnergyItemHelper;
import com.mohistmc.academy.crafting.AcademyRecipeTypes;
import com.mohistmc.academy.crafting.MetalFormingRecipe;
import com.mohistmc.academy.crafting.MetalFormingRecipeInput;
import com.mohistmc.academy.crafting.MetalFormerRecipes.Mode;
import com.mohistmc.academy.energy.api.block.IWirelessReceiver;
import com.mohistmc.academy.world.AcademyBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

/**
 * 金属成型机方块实体 —— 实现 IWirelessReceiver 从无线能源网络获取能量,支持 PLATE/INCISE/ETCH/REFINE 四种模式。
 */
public class MetalFomerBlockEntity extends AcademyContainerBlockEntity implements IWirelessReceiver {

    public static final int SLOT_IN = 0;
    public static final int SLOT_OUT = 1;
    public static final int SLOT_BATTERY = 2;

    public static final double MAX_ENERGY = 3000;
    public static final double BANDWIDTH = 50;
    public static final int WORK_TICKS = 60;
    public static final double CONSUME_PER_TICK = 13.3;

    private static final int SEARCH_TICKS = 5;

    private double energy = 0;
    private Mode mode = Mode.PLATE;
    private MetalFormingRecipe current;
    private int workCounter;

    private boolean clientWorking = false;
    private boolean lastWorking = false;

    public MetalFomerBlockEntity(BlockPos pos, BlockState state) {
        super(AcademyBlockEntities.METAL_FORMER.get(), pos, state);
        setItems(NonNullList.withSize(getContainerSize(), ItemStack.EMPTY));
    }

    @Override
    public int getContainerSize() {
        return 3;
    }

    public static boolean isSlotValid(Level level, int slot, ItemStack stack) {
        return switch (slot) {
            case SLOT_IN -> isValidInput(level, stack);
            case SLOT_OUT -> false;
            default -> EnergyItemHelper.isEnergyItem(stack);
        };
    }

    /** 遍历数据驱动配方，检查物品是否为任何配方的输入 */
    private static boolean isValidInput(Level level, ItemStack stack) {
        if (level == null || stack.isEmpty()) return false;
        for (RecipeHolder<MetalFormingRecipe> holder : level.getRecipeManager().getAllRecipesFor(AcademyRecipeTypes.METAL_FORMING.get())) {
            if (holder.value().matchesItem(stack)) return true;
        }
        return false;
    }

    // ==================== 状态访问 ====================

    public Mode getMode() { return mode; }

    public MetalFormingRecipe getCurrentRecipe() { return current; }

    public boolean isWorkInProgress() {
        return current != null && !isActionBlocked();
    }

    public double getWorkProgress() {
        return isWorkInProgress() ? (double) workCounter / WORK_TICKS : 0;
    }

    public double getEnergy() { return energy; }

    public double getMaxEnergy() { return MAX_ENERGY; }

    public boolean isClientWorking() { return clientWorking; }

    // ==================== IWirelessReceiver ====================

    @Override
    public double getRequiredEnergy() {
        return MAX_ENERGY - energy;
    }

    @Override
    public double injectEnergy(double amt) {
        double give = Math.min(amt, MAX_ENERGY - energy);
        energy += give;
        setChanged();
        return amt - give;
    }

    @Override
    public double pullEnergy(double amt) {
        double a = Math.min(amt, energy);
        energy -= a;
        setChanged();
        return a;
    }

    @Override
    public double getBandwidth() {
        return BANDWIDTH;
    }

    // ==================== 模式切换 ====================

    public void cycleMode(int delta) {
        int next = mode.ordinal() + delta;
        if (next >= Mode.values().length) next = 0;
        else if (next < 0) next = Mode.values().length - 1;
        mode = Mode.values()[next];

        current = null;
        workCounter = 0;
        setChanged();
    }

    // ==================== Tick ====================

    public void serverTick() {
        if (level == null || level.isClientSide) return;

        if (current != null) {
            if (!isActionBlocked() && pullEnergy(CONSUME_PER_TICK) == CONSUME_PER_TICK) {
                ++workCounter;
                if (workCounter == WORK_TICKS) {
                    finishJob();
                }
            } else {
                current = null;
                workCounter = 0;
            }
        } else {
            if (++workCounter >= SEARCH_TICKS) {
                current = level.getRecipeManager().getRecipeFor(AcademyRecipeTypes.METAL_FORMING.get(),
                        new MetalFormingRecipeInput(getItems().get(SLOT_IN), mode), level)
                        .map(RecipeHolder::value).orElse(null);
                workCounter = 0;
            }
        }

        chargeFromSlot();

        boolean w = isWorkInProgress();
        if (w != lastWorking) {
            lastWorking = w;
            clientWorking = w;
            setChanged();
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    private void finishJob() {
        ItemStack in = getItems().get(SLOT_IN);
        ItemStack out = getItems().get(SLOT_OUT);
        ItemStack result = current.getOutput();

        in.shrink(current.getInputCount());
        if (in.getCount() == 0) {
            getItems().set(SLOT_IN, ItemStack.EMPTY);
        }
        if (out.isEmpty()) {
            getItems().set(SLOT_OUT, result);
        } else {
            out.grow(result.getCount());
        }
        current = null;
        workCounter = 0;
        setChanged();
    }

    public boolean isActionBlocked() {
        if (current == null) return true;
        ItemStack in = getItems().get(SLOT_IN);
        ItemStack out = getItems().get(SLOT_OUT);
        if (!current.matchesItem(in) || in.getCount() < current.getInputCount()) return true;

        ItemStack result = current.getOutput();
        if (result.isEmpty()) return true;
        return !(out.isEmpty()
                || (out.getItem() == result.getItem()
                    && out.getCount() + result.getCount() <= out.getMaxStackSize()));
    }

    private void chargeFromSlot() {
        ItemStack stack = getItems().get(SLOT_BATTERY);
        if (!EnergyItemHelper.isEnergyItem(stack)) return;
        double want = Math.min(getMaxEnergy() - energy, getBandwidth());
        if (want <= 0) return;
        int gain = EnergyItemHelper.extractEnergy(stack, (int) want, false);
        if (gain > 0) injectEnergy(gain);
    }

    // ==================== 侧面自动输入输出 (hopper 等) ====================

    private final IItemHandler handlerDown = new SidedItems(new int[]{SLOT_OUT, SLOT_BATTERY}, true);
    private final IItemHandler handlerUp = new SidedItems(new int[]{SLOT_IN}, false);
    private final IItemHandler handlerSide = new SidedItems(new int[]{SLOT_BATTERY}, false);
    private final IItemHandler handlerNull = new SidedItems(new int[]{SLOT_IN, SLOT_OUT, SLOT_BATTERY}, true);

    private final class SidedItems implements IItemHandler {
        private final int[] slots;
        private final boolean canExtract;

        SidedItems(int[] slots, boolean canExtract) {
            this.slots = slots;
            this.canExtract = canExtract;
        }

        @Override
        public int getSlots() {
            return slots.length;
        }

        @NotNull
        @Override
        public ItemStack getStackInSlot(int slot) {
            return getItems().get(slots[slot]);
        }

        @NotNull
        @Override
        public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            ItemStack existing = getItems().get(slots[slot]);
            if (existing.isEmpty()) {
                if (!simulate) {
                    getItems().set(slots[slot], stack.copy());
                    setChanged();
                }
                return ItemStack.EMPTY;
            }
            if (ItemStack.isSameItem(existing, stack)) {
                int canAdd = Math.min(stack.getCount(), existing.getMaxStackSize() - existing.getCount());
                if (canAdd <= 0) return stack;
                if (!simulate) {
                    existing.grow(canAdd);
                    setChanged();
                }
                ItemStack left = stack.copy();
                left.shrink(canAdd);
                return left;
            }
            return stack;
        }

        @NotNull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (!canExtract) return ItemStack.EMPTY;
            ItemStack existing = getItems().get(slots[slot]);
            if (existing.isEmpty()) return ItemStack.EMPTY;
            int n = Math.min(amount, existing.getCount());
            ItemStack out = existing.copy();
            out.setCount(n);
            if (!simulate) {
                existing.shrink(n);
                if (existing.isEmpty()) getItems().set(slots[slot], ItemStack.EMPTY);
                setChanged();
            }
            return out;
        }

        @Override
        public int getSlotLimit(int slot) {
            return getItems().get(slots[slot]).getMaxStackSize();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return MetalFomerBlockEntity.isSlotValid(MetalFomerBlockEntity.this.level, slots[slot], stack);
        }
    }

    /** 供 RegisterCapabilitiesEvent 按朝向返回物品栏能力 */
    public IItemHandler getHandlerForSide(Direction side) {
        if (side == null) return handlerNull;
        return switch (side) {
            case DOWN -> handlerDown;
            case UP -> handlerUp;
            default -> handlerSide;
        };
    }

    // ==================== 掉落 ====================

    public void dropContents() {
        if (level == null) return;
        BlockPos p = getBlockPos();
        for (ItemStack s : getItems()) {
            if (!s.isEmpty()) {
                Containers.dropItemStack(level, p.getX(), p.getY(), p.getZ(), s);
            }
        }
    }

    // ==================== NBT ====================

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("energy")) energy = tag.getDouble("energy");
        if (tag.contains("mode")) mode = Mode.byOrdinal(tag.getInt("mode"));
        if (tag.contains("workCounter")) workCounter = tag.getInt("workCounter");
        if (tag.contains("working")) clientWorking = tag.getBoolean("working");
        // current 配方不持久化，tick 时由 RecipeManager 重新查找
        current = null;
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putDouble("energy", energy);
        tag.putInt("mode", mode.ordinal());
        tag.putInt("workCounter", workCounter);
        tag.putBoolean("working", lastWorking);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = super.getUpdateTag(provider);
        tag.putBoolean("working", lastWorking);
        tag.putInt("mode", mode.ordinal());
        return tag;
    }
}
