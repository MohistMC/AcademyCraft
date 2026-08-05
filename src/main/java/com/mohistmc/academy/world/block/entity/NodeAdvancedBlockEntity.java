package com.mohistmc.academy.world.block.entity;

import com.mohistmc.academy.world.AcademyBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 高级无线虚能节点 —— 最高性能,范围 30,容量 16。
 * @author Mgazul
 */
public class NodeAdvancedBlockEntity extends BaseNodeBlockEntity {
    public NodeAdvancedBlockEntity(BlockPos pos, BlockState state) {
        super(AcademyBlockEntities.NODE_ADVANCED.get(), pos, state);
        setMaxEnergy(20000);
        setBandwidth(200);
    }

    @Override
    public double getRange() {
        return 30.0;
    }

    @Override
    public int getCapacity() {
        return 16;
    }
}
