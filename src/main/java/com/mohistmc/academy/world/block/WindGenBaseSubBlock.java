package com.mohistmc.academy.world.block;

import com.mohistmc.academy.world.block.entity.WindGenBaseSubBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
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

    @Nullable
    private WindGenBase getMainBlock(BlockGetter level, BlockPos pos) {
        BlockState state = level.getBlockState(pos.below(1));
        return state.getBlock() instanceof WindGenBase wg ? wg : null;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        WindGenBase main = getMainBlock(level, pos);
        if (main != null) {
            return new ItemStack(main.asItem());
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighbor, boolean p_60514_) {
        if (block instanceof WindGenBase && level.getBlockState(neighbor).getBlock() instanceof AirBlock) {
            level.destroyBlock(pos, false);
        }
        super.neighborChanged(state, level, pos, block, neighbor, p_60514_);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState p_60503_, Level level, BlockPos pos, Player player, BlockHitResult p_60508_) {
        BlockPos mainPos = pos.below(1);
        WindGenBase main = getMainBlock(level, pos);
        if (main != null) {
            return main.useWithoutItem(level.getBlockState(mainPos), level, mainPos, player, p_60508_);
        }
        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new WindGenBaseSubBlockEntity(p_153215_, p_153216_);
    }
}
