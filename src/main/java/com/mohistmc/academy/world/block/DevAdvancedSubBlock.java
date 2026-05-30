package com.mohistmc.academy.world.block;

import com.mohistmc.academy.world.block.entity.DevAdvancedSubBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class DevAdvancedSubBlock extends DevMachineSubBase {
    public static final MapCodec<DevAdvancedSubBlock> CODEC = simpleCodec(DevAdvancedSubBlock::new);

    public DevAdvancedSubBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<DevAdvancedSubBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DevAdvancedSubBlockEntity(pos, state);
    }
}
