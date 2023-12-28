package com.mohistmc.academy.world.block;

import com.mohistmc.academy.client.block.entity.DevAdvancedBlockEntity;
import com.mohistmc.academy.world.AcademyBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;

public class DevAdvanced extends BaseEntityBlock {
    public static final MapCodec<DevAdvanced> CODEC = simpleCodec(DevAdvanced::new);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public DevAdvanced(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(FACING, Direction.NORTH));

    }

    @Override
    protected MapCodec<DevAdvanced> codec() {
        return CODEC;
    }


    @Override
    public void animateTick(BlockState p_220827_, Level p_220828_, BlockPos p_220829_, RandomSource p_220830_) {
    }

    /*
    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        Direction direction = p_60555_.getValue(HorizontalDirectionalBlock.FACING).getOpposite();
        return switch (direction) {
            case DOWN, UP, SOUTH -> Shapes.box(0, 0, 0, 1, 3, 3);
            case NORTH -> Shapes.box(0, 0, -2, 1, 3, 1);
            case EAST -> Shapes.box(0, 0, 0, 3, 3, 1);
            case WEST -> Shapes.box(-2, 0, 0, 1, 3, 1);
        };
    }

     */

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState p_60569_, boolean p_60570_) {
        Direction direction = state.getValue(HorizontalDirectionalBlock.FACING).getOpposite();
        Block subBlock = AcademyBlocks.DEV_ADVANCED_SUB.get();
        switch (direction) {
            case NORTH -> {
                level.setBlock(pos.north(1), subBlock.defaultBlockState(), 19);
                level.setBlock(pos.north(1).above(1), subBlock.defaultBlockState(), 19);
                level.setBlock(pos.north(2), subBlock.defaultBlockState(), 19);
                level.setBlock(pos.north(2).above(1), subBlock.defaultBlockState(), 19);
                level.setBlock(pos.north(2).above(2), subBlock.defaultBlockState(), 19);
            }
            case SOUTH -> {
                level.setBlock(pos.south(1), subBlock.defaultBlockState(), 19);
                level.setBlock(pos.south(1).above(1), subBlock.defaultBlockState(), 19);
                level.setBlock(pos.south(2), subBlock.defaultBlockState(), 19);
                level.setBlock(pos.south(2).above(1), subBlock.defaultBlockState(), 19);
                level.setBlock(pos.south(2).above(2), subBlock.defaultBlockState(), 19);
            }
            case WEST -> {
                level.setBlock(pos.west(1), subBlock.defaultBlockState(), 19);
                level.setBlock(pos.west(1).above(1), subBlock.defaultBlockState(), 19);
                level.setBlock(pos.west(2), subBlock.defaultBlockState(), 19);
                level.setBlock(pos.west(2).above(1), subBlock.defaultBlockState(), 19);
                level.setBlock(pos.west(2).above(2), subBlock.defaultBlockState(), 19);
            }
            case EAST -> {
                level.setBlock(pos.east(1), subBlock.defaultBlockState(), 19);
                level.setBlock(pos.east(1).above(1), subBlock.defaultBlockState(), 19);
                level.setBlock(pos.east(2), subBlock.defaultBlockState(), 19);
                level.setBlock(pos.east(2).above(1), subBlock.defaultBlockState(), 19);
                level.setBlock(pos.east(2).above(2), subBlock.defaultBlockState(), 19);
            }
        }
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        Direction direction = state.getValue(HorizontalDirectionalBlock.FACING).getOpposite();
        switch (direction) {
            case NORTH -> {
                level.destroyBlock(pos.north(1), false);
                level.destroyBlock(pos.north(1).above(1), false);
                level.destroyBlock(pos.north(2), false);
                level.destroyBlock(pos.north(2).above(1), false);
                level.destroyBlock(pos.north(2).above(2), false);
            }
            case SOUTH -> {
                level.destroyBlock(pos.south(1), false);
                level.destroyBlock(pos.south(1).above(1), false);
                level.destroyBlock(pos.south(2), false);
                level.destroyBlock(pos.south(2).above(1), false);
                level.destroyBlock(pos.south(2).above(2), false);
            }
            case WEST -> {
                level.destroyBlock(pos.west(1), false);
                level.destroyBlock(pos.west(1).above(1), false);
                level.destroyBlock(pos.west(2), false);
                level.destroyBlock(pos.west(2).above(1), false);
                level.destroyBlock(pos.west(2).above(2), false);
            }
            case EAST -> {
                level.destroyBlock(pos.east(1), false);
                level.destroyBlock(pos.east(1).above(1), false);
                level.destroyBlock(pos.east(2), false);
                level.destroyBlock(pos.east(2).above(1), false);
                level.destroyBlock(pos.east(2).above(2), false);
            }
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighbor, boolean p_60514_) {
        //Block block = level.getBlockState(pos).getBlock();
        if ((block instanceof DevAdvancedSubBlock || block instanceof DevAdvanced) && level.getBlockState(neighbor).getBlock() instanceof AirBlock) {
            level.destroyBlock(pos, false);
        }
        super.neighborChanged(state, level, pos, block, neighbor, p_60514_);
    }

    @Override
    public RenderShape getRenderShape(BlockState p_49232_) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new DevAdvancedBlockEntity(p_153215_, p_153216_);
    }

    @Override
    public BlockState rotate(BlockState p_48722_, Rotation p_48723_) {
        return p_48722_.setValue(FACING, p_48723_.rotate(p_48722_.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState p_48719_, Mirror p_48720_) {
        return p_48719_.rotate(p_48720_.getRotation(p_48719_.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_) {
        p_49915_.add(FACING);
        super.createBlockStateDefinition(p_49915_);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext p_49820_) {
        return this.defaultBlockState().setValue(FACING, p_49820_.getHorizontalDirection().getOpposite());
    }

}
