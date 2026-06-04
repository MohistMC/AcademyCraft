package com.mohistmc.academy.capability;

import net.minecraft.world.item.ItemStack;

/**
 * @author Mgazul
 * @date 2026/6/5 02:14
 */
public interface IEnergyItem {
    int getEnergyStored(ItemStack stack);

    int getMaxEnergyStored(ItemStack stack);

    void setEnergy(ItemStack stack, int energy);

    int extractEnergy(ItemStack stack, int maxExtract, boolean simulate);

    int receiveEnergy(ItemStack stack, int maxReceive, boolean simulate);
}
