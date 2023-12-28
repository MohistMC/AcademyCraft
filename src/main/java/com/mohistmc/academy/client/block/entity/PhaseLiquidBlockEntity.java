package com.mohistmc.academy.client.block.entity;

import com.mohistmc.academy.world.AcademyBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class PhaseLiquidBlockEntity extends TheEndPortalBlockEntity {
    public PhaseLiquidBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(AcademyBlockEntities.PHASE_LIQUID.get(), p_155229_, p_155230_);
    }

}
