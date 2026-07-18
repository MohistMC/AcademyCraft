package com.mohistmc.academy.world.block.entity;

import com.mohistmc.academy.capability.IFEnergyStorage;
import com.mohistmc.academy.energy.api.block.IWirelessReceiver;
import com.mohistmc.academy.world.AcademyBlockEntities;
import com.mohistmc.academy.world.AcademyItems;
import com.mohistmc.academy.world.block.IDevStructure;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 高级能力开发机方块实体 —— 支持物品槽（线圈+因子）和 IF 能量接收。
 *
 * @author Mgazul
 */
public class DevAdvancedBlockEntity extends AcademyContainerBlockEntity
        implements IFEnergyStorage, IDevStructure, IWirelessReceiver {

    public static final int MAX_ENERGY = 10000;
    private static final double MAX_BANDWIDTH = 50;

    private int energy = 0;
    private UUID structureId;

    public DevAdvancedBlockEntity(BlockPos pos, BlockState state) {
        super(AcademyBlockEntities.DEV_ADVANCED.get(), pos, state);
        setItems(NonNullList.withSize(getContainerSize(), ItemStack.EMPTY));
    }

    @Override
    public int getContainerSize() {
        return 2; // 0=线圈, 1=因子
    }

    // ==================== 重置状态检查 ====================

    /** 是否有高压磁增幅线圈 */
    public boolean hasCoil() {
        ItemStack coil = getItems().get(0);
        return !coil.isEmpty() && coil.is(AcademyItems.MAGNETIC_COIL.get());
    }

    /** 是否有能力诱导因子 */
    public boolean hasFactor() {
        ItemStack factor = getItems().get(1);
        return !factor.isEmpty() && factor.getItem() instanceof com.mohistmc.academy.world.item.BaseFactor;
    }

    /** 是否满足重置条件 */
    public boolean isReadyForReset() {
        return hasCoil() && hasFactor();
    }

    // ==================== IDevStructure ====================

    @Override
    public UUID getStructureId() {
        return structureId;
    }

    @Override
    public void setStructureId(UUID structureId) {
        this.structureId = structureId;
        setChanged();
    }

    // ==================== IFEnergyStorage ====================

    @Override
    public int getEnergyStored() {
        return energy;
    }

    @Override
    public int getMaxEnergyStored() {
        return MAX_ENERGY;
    }

    @Override
    public void setEnergy(int energy) {
        this.energy = Math.clamp(energy, 0, MAX_ENERGY);
        setChanged();
    }

    // ==================== IWirelessReceiver ====================

    @Override
    public double getRequiredEnergy() {
        return MAX_ENERGY - energy > 0 ? 100 : 0;
    }

    @Override
    public double injectEnergy(double amt) {
        double accepted = Math.min(amt, MAX_ENERGY - energy);
        energy += (int) accepted;
        setChanged();
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

    // ==================== NBT ====================

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("energy")) {
            this.energy = tag.getInt("energy");
        }
        if (tag.contains("structureId")) {
            this.structureId = UUID.fromString(tag.getString("structureId"));
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt("energy", energy);
        if (structureId != null) {
            tag.putString("structureId", structureId.toString());
        }
    }
}
