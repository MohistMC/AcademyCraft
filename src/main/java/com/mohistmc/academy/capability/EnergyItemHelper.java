package com.mohistmc.academy.capability;

import net.minecraft.world.item.ItemStack;

/**
 * @author Mgazul
 * @date 2026/6/2 04:36
 */
public final class EnergyItemHelper {

    private EnergyItemHelper() {}

    public static boolean isEnergyItem(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.getItem() instanceof IEnergyItem;
    }

    public static int getEnergy(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return 0;
        if (stack.getItem() instanceof IEnergyItem energyItem) {
            return energyItem.getEnergyStored(stack);
        }
        return 0;
    }

    public static void setEnergy(ItemStack stack, int energy) {
        if (stack == null || stack.isEmpty()) return;
        if (stack.getItem() instanceof IEnergyItem energyItem) {
            energyItem.setEnergy(stack, energy);
        }
    }

    public static int extractEnergy(ItemStack stack, int maxExtract, boolean simulate) {
        if (stack == null || stack.isEmpty()) return 0;
        if (stack.getItem() instanceof IEnergyItem energyItem) {
            return energyItem.extractEnergy(stack, maxExtract, simulate);
        }
        return 0;
    }

    public static int receiveEnergy(ItemStack stack, int maxReceive, boolean simulate) {
        if (stack == null || stack.isEmpty()) return 0;
        if (stack.getItem() instanceof IEnergyItem energyItem) {
            return energyItem.receiveEnergy(stack, maxReceive, simulate);
        }
        return 0;
    }
}
