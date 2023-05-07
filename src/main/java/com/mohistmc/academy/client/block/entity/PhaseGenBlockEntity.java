package com.mohistmc.academy.client.block.entity;

import com.mohistmc.academy.world.AcademyBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class PhaseGenBlockEntity extends BlockEntity {
    public PhaseGenBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(AcademyBlockEntities.PHASE_GEN.get(), p_155229_, p_155230_);
    }

}
