package com.mohistmc.academy.capability;

import net.minecraft.nbt.CompoundTag;

@AutoRegisterCapability
public interface IIFCapability {

    int getIF();

    CompoundTag serializeNBT(CompoundTag tag);

    void deserializeNBT(CompoundTag tag);
}
