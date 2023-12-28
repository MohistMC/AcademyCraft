package com.mohistmc.academy.world.block;

import com.mohistmc.academy.client.block.entity.MatrixSubBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class MatrixSubBlock extends BaseEntityBlock {
    public static final MapCodec<MatrixSubBlock> CODEC = simpleCodec(MatrixSubBlock::new);
    public MatrixSubBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<MatrixSubBlock> codec() {
        return CODEC;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighbor, boolean p_60514_) {
        //Block block = level.getBlockState(pos).getBlock();
        if ((block instanceof MatrixSubBlock || block instanceof Matrix) && level.getBlockState(neighbor).getBlock() instanceof AirBlock) {
            level.destroyBlock(pos, false);
        }
        super.neighborChanged(state, level, pos, block, neighbor, p_60514_);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new MatrixSubBlockEntity(p_153215_, p_153216_);
    }


}
