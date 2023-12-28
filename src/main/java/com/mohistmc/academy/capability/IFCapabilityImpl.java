package com.mohistmc.academy.capability;

import net.minecraft.nbt.CompoundTag;

public class IFCapabilityImpl implements IIFCapability {

    private int content = 0;

    public IFCapabilityImpl(int content) {
        this.content = content;
    }

    @Override
    public int getIF() {
        return this.content;
    }


    public CompoundTag serializeNBT(CompoundTag tag) {
        tag.putInt("if_power", this.content);
        return tag;
    }


    public void deserializeNBT(CompoundTag tag) {
        this.content = tag.getInt("if_power");
    }
}
