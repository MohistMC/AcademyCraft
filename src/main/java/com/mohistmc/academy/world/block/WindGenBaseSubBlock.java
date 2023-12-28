package com.mohistmc.academy.world.block;

import com.mohistmc.academy.client.block.entity.DevAdvancedSubBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class WindGenBaseSubBlock extends BaseEntityBlock {
    public static final MapCodec<WindGenBaseSubBlock> CODEC = simpleCodec(WindGenBaseSubBlock::new);
    private boolean validBlock = false;

    public WindGenBaseSubBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<WindGenBaseSubBlock> codec() {
        return CODEC;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighbor, boolean p_60514_) {
        //Block block = level.getBlockState(pos).getBlock();
        if (block instanceof WindGenBase && level.getBlockState(neighbor).getBlock() instanceof AirBlock) {
            level.destroyBlock(pos, false);
        }
        super.neighborChanged(state, level, pos, block, neighbor, p_60514_);
    }

    @Override
    public InteractionResult use(BlockState p_60503_, Level level, BlockPos pos, Player player, InteractionHand p_60507_, BlockHitResult p_60508_) {
        BlockState state = level.getBlockState(pos.below(1));
        return state.getBlock().use(state, level, pos.below(1), player, p_60507_, p_60508_);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new DevAdvancedSubBlockEntity(p_153215_, p_153216_);
    }


}
