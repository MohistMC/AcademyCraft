package com.mohistmc.academy.capability;

import net.minecraft.nbt.CompoundTag;

/**
 * 方块能量存储接口
 */
public interface IFEnergyStorage {

    int getEnergyStored();

    int getMaxEnergyStored();

    default int extractEnergy(int maxExtract, boolean simulate) {
        int stored = getEnergyStored();
        int extracted = Math.min(stored, maxExtract);
        if (!simulate && extracted > 0) {
            setEnergy(stored - extracted);
        }
        return extracted;
    }

    default int receiveEnergy(int maxReceive, boolean simulate) {
        int stored = getEnergyStored();
        int max = getMaxEnergyStored();
        int received = Math.min(max - stored, maxReceive);
        if (!simulate && received > 0) {
            setEnergy(stored + received);
        }
        return received;
    }

    void setEnergy(int energy);

    default CompoundTag serializeEnergy(CompoundTag tag) {
        tag.putInt("if_energy", getEnergyStored());
        return tag;
    }

    default void deserializeEnergy(CompoundTag tag) {
        if (tag.contains("if_energy")) {
            setEnergy(tag.getInt("if_energy"));
        }
    }
}