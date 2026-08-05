package com.mohistmc.academy.world.block;

import net.minecraft.core.BlockPos;

public interface IDevSubStructure extends IDevStructure {
    BlockPos getMainPos();
    void setMainPos(BlockPos pos);
}
