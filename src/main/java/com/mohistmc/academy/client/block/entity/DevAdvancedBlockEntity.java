package com.mohistmc.academy.client.block.entity;

import com.mohistmc.academy.world.AcademyBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.WaterFluid;

public class DevAdvancedBlockEntity extends BlockEntity {
    public DevAdvancedBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(AcademyBlockEntities.DEV_ADVANCED.get(), p_155229_, p_155230_);
    }

}
