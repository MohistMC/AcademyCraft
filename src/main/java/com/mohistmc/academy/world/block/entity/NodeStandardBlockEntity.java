package com.mohistmc.academy.world.block.entity;

import com.mohistmc.academy.world.AcademyBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 改良无线虚能节点 —— 中等性能。
 * 范围 20，容量 8。
 *
 * @author Mgazul
 */
public class NodeStandardBlockEntity extends BaseNodeBlockEntity {
    public NodeStandardBlockEntity(BlockPos pos, BlockState state) {
        super(AcademyBlockEntities.NODE_STANDARD.get(), pos, state);
        setMaxEnergy(10000);
        setBandwidth(100);
    }

    @Override
    public double getRange() {
        return 20.0;
    }

    @Override
    public int getCapacity() {
        return 8;
    }
}
