package com.mohistmc.academy.world.block;


import net.minecraft.core.BlockPos;


/**
 * @author Mgazul
 * @date 2026/6/2 18:35
 */
public interface IDevSubStructure extends IDevStructure {
    BlockPos getMainPos();
    void setMainPos(BlockPos pos);
}
