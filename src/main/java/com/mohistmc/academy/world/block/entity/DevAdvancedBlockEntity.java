package com.mohistmc.academy.world.block.entity;

import com.mohistmc.academy.capability.IFEnergyStorage;
import com.mohistmc.academy.world.AcademyBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DevAdvancedBlockEntity extends BlockEntity implements IFEnergyStorage {
    public static final int MAX_ENERGY = 10000;
    private int energy = 0;

    public DevAdvancedBlockEntity(BlockPos pos, BlockState state) {
        super(AcademyBlockEntities.DEV_ADVANCED.get(), pos, state);
    }

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

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("energy")) {
            this.energy = tag.getInt("energy");
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt("energy", energy);
    }
}
