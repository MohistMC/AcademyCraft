package com.mohistmc.academy.world.block;

import com.mohistmc.academy.world.AcademyBlocks;
import com.mohistmc.academy.world.block.entity.DevAdvancedBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class DevAdvanced extends DevMachineBase {
    public static final MapCodec<DevAdvanced> CODEC = simpleCodec(DevAdvanced::new);

    public DevAdvanced(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<DevAdvanced> codec() {
        return CODEC;
    }

    @Override
    protected Block getSubBlock() {
        return AcademyBlocks.DEV_ADVANCED_SUB.get();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DevAdvancedBlockEntity(pos, state);
    }
}
