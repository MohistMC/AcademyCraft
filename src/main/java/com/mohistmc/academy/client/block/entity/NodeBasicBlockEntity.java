package com.mohistmc.academy.client.block.entity;

import com.mohistmc.academy.world.AcademyBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class NodeBasicBlockEntity extends BaseNodeBlockEntity {
    public NodeBasicBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(AcademyBlockEntities.NODE_BASIC.get(), p_155229_, p_155230_);
    }

    @Override
    public int getContainerSize() {
        return 2;
    }

    @Override
    public int getRange() {
        return 1;
    }


}
