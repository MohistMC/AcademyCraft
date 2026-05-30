package com.mohistmc.academy.world.block;

import com.mohistmc.academy.world.AcademyBlocks;
import com.mohistmc.academy.world.block.entity.DevNormalBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class DevNormal extends DevMachineBase {
    public static final MapCodec<DevNormal> CODEC = simpleCodec(DevNormal::new);

    public DevNormal(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<DevNormal> codec() {
        return CODEC;
    }

    @Override
    protected Block getSubBlock() {
        return AcademyBlocks.DEV_NORMAL_SUB.get();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DevNormalBlockEntity(pos, state);
    }
}
