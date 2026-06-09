package com.mohistmc.academy.world.block.entity;

import com.mohistmc.academy.world.AcademyBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class NodeBasicBlockEntity extends BaseNodeBlockEntity {
    public NodeBasicBlockEntity(BlockPos pos, BlockState state) {
        super(AcademyBlockEntities.NODE_BASIC.get(), pos, state);
    }

    @Override
    public double getRange() {
        return 10.0;
    }

    @Override
    public int getCapacity() {
        return 3;
    }
}
