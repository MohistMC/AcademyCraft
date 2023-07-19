package com.mohistmc.academy.client.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseNodeBlockEntity extends AcademyContainerBlockEntity {
    public BaseNodeBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    @Override
    public int getContainerSize() {
        return 2;
    }

    public abstract int getRange();

    public boolean isConnected() {
        //TODO: 连接/断开矩阵
        return false;
    }
}
