package com.mohistmc.academy.capability;

import net.minecraft.world.item.ItemStack;

/**
 * 可存储能量的物品接口
 */
public interface IEnergyItem {
    int getEnergyStored(ItemStack stack);

    int getMaxEnergyStored(ItemStack stack);

    void setEnergy(ItemStack stack, int energy);

    int extractEnergy(ItemStack stack, int maxExtract, boolean simulate);

    int receiveEnergy(ItemStack stack, int maxReceive, boolean simulate);
}
