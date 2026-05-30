package com.mohistmc.academy.world.block;

import com.mohistmc.academy.world.block.entity.DevNormalSubBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class DevNormalSubBlock extends DevMachineSubBase {
    public static final MapCodec<DevNormalSubBlock> CODEC = simpleCodec(DevNormalSubBlock::new);

    public DevNormalSubBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<DevNormalSubBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DevNormalSubBlockEntity(pos, state);
    }
}
