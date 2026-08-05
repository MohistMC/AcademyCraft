package com.mohistmc.academy.world.block;

import com.mohistmc.academy.world.AcademyBlocks;
import com.mohistmc.academy.world.block.entity.MatrixBlockEntity;
import com.mohistmc.academy.world.menu.MatrixMenu;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.phys.BlockHitResult;
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

public class Matrix extends BaseEntityBlock {
    public static final MapCodec<Matrix> CODEC = simpleCodec(Matrix::new);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public Matrix(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<Matrix> codec() {
        return CODEC;
    }


    @Override
    public void setPlacedBy(net.minecraft.world.level.Level level, BlockPos pos, BlockState state, @org.jetbrains.annotations.Nullable net.minecraft.world.entity.LivingEntity placer, net.minecraft.world.item.ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (placer != null && level.getBlockEntity(pos) instanceof com.mohistmc.academy.world.block.entity.MatrixBlockEntity be) {
            be.setOwnerUUID(placer.getUUID());
        }
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            player.openMenu(getMenuProvider(state, level, pos), pos);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.CONSUME;
    }

    @Nullable
    @Override
    public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.empty();
            }

            @Override
            public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
                return new MatrixMenu(windowId, inv, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(pos));
            }
        };
    }

    @Override
    public void animateTick(BlockState p_220827_, Level p_220828_, BlockPos p_220829_, RandomSource p_220830_) {
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState p_60569_, boolean p_60570_) {
        Direction direction = state.getValue(HorizontalDirectionalBlock.FACING).getOpposite();
        Block subBlock = AcademyBlocks.MATRIX_SUB.get();
        switch (direction) {
            case NORTH -> {
                level.setBlock(pos.south(1), subBlock.defaultBlockState(), 19);
                level.setBlock(pos.east(1), subBlock.defaultBlockState(), 19);
                level.setBlock(pos.east(1).south(1), subBlock.defaultBlockState(), 19);
            }
            case SOUTH -> {
                level.setBlock(pos.north(1), subBlock.defaultBlockState(), 19);
                level.setBlock(pos.west(1), subBlock.defaultBlockState(), 19);
                level.setBlock(pos.west(1).north(1), subBlock.defaultBlockState(), 19);
            }
            case WEST -> {
                level.setBlock(pos.east(1), subBlock.defaultBlockState(), 19);
                level.setBlock(pos.north(1), subBlock.defaultBlockState(), 19);
                level.setBlock(pos.north(1).east(1), subBlock.defaultBlockState(), 19);
            }
            case EAST -> {
                level.setBlock(pos.west(1), subBlock.defaultBlockState(), 19);
                level.setBlock(pos.south(1), subBlock.defaultBlockState(), 19);
                level.setBlock(pos.south(1).west(1), subBlock.defaultBlockState(), 19);
            }
        }
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        Direction direction = state.getValue(HorizontalDirectionalBlock.FACING).getOpposite();
        switch (direction) {
            case NORTH -> {
                level.destroyBlock(pos.south(1), false);
                level.destroyBlock(pos.east(1), false);
                level.destroyBlock(pos.east(1).south(1), false);
            }
            case SOUTH -> {
                level.destroyBlock(pos.north(1), false);
                level.destroyBlock(pos.west(1), false);
                level.destroyBlock(pos.west(1).north(1), false);
            }
            case WEST -> {
                level.destroyBlock(pos.east(1), false);
                level.destroyBlock(pos.north(1), false);
                level.destroyBlock(pos.north(1).east(1), false);
            }
            case EAST -> {
                level.destroyBlock(pos.west(1), false);
                level.destroyBlock(pos.south(1), false);
                level.destroyBlock(pos.south(1).west(1), false);
            }
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighbor, boolean p_60514_) {
        if ((block instanceof MatrixSubBlock) && level.getBlockState(neighbor).getBlock() instanceof AirBlock) {
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
        return new MatrixBlockEntity(p_153215_, p_153216_);
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
