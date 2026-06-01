package com.mohistmc.academy.capability;

import net.minecraft.world.item.ItemStack;

/**
 * @author Mgazul
 * @date 2026/6/2 04:36
 */
public final class EnergyItemHelper {

    private EnergyItemHelper() {}

    public static int getEnergy(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return 0;
        return stack.getMaxDamage() - stack.getDamageValue();
    }

    public static void setEnergy(ItemStack stack, int energy) {
        if (stack == null || stack.isEmpty()) return;
        int maxEnergy = stack.getMaxDamage();
        int newDamage = Math.max(0, maxEnergy - energy);
        stack.setDamageValue(newDamage);
    }

    public static int extractEnergy(ItemStack stack, int maxExtract, boolean simulate) {
        int energy = getEnergy(stack);
        int extracted = Math.min(energy, maxExtract);
        if (!simulate && extracted > 0) {
            setEnergy(stack, energy - extracted);
        }
        return extracted;
    }

    public static int receiveEnergy(ItemStack stack, int maxReceive, boolean simulate) {
        int energy = getEnergy(stack);
        int maxEnergy = stack.getMaxDamage();
        int received = Math.min(maxEnergy - energy, maxReceive);
        if (!simulate && received > 0) {
            setEnergy(stack, energy + received);
        }
        return received;
    }

    public static boolean isEnergyItem(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.getMaxDamage() > 0;
    }
}
