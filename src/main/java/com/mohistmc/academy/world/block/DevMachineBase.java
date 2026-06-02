package com.mohistmc.academy.world.block;

import com.mohistmc.academy.capability.IFEnergyStorage;
import com.mohistmc.academy.network.LearnSkillPacket;
import com.mohistmc.academy.network.OpenDevGuiPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public abstract class DevMachineBase extends BaseEntityBlock implements IDevMachine {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public DevMachineBase(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
    }

    protected abstract Block getSubBlock();

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.box(0, 0, 0, 1, 1.6, 1);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, net.minecraft.util.RandomSource random) {
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (player instanceof ServerPlayer serverPlayer) {
            LearnSkillPacket.syncToClient(serverPlayer);
            DevMachineType devType = this instanceof DevAdvanced ? DevMachineType.ADVANCED : DevMachineType.NORMAL;
            int energy = 0;
            int maxEnergy = 0;
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof IFEnergyStorage storage) {
                energy = storage.getEnergyStored();
                maxEnergy = storage.getMaxEnergyStored();
            }
            PacketDistributor.sendToPlayer(serverPlayer, new OpenDevGuiPacket(devType.ordinal(), energy, maxEnergy));
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moved) {
        Direction dir = state.getValue(FACING).getOpposite();
        Block sub = getSubBlock();
        for (int dist = 1; dist <= 2; dist++) {
            for (int dy = 0; dy <= (dist == 2 ? 2 : 1); dy++) {
                level.setBlock(pos.relative(dir, dist).above(dy), sub.defaultBlockState(), 19);
            }
        }
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        Direction dir = state.getValue(FACING).getOpposite();
        for (int dist = 1; dist <= 2; dist++) {
            for (int dy = 0; dy <= (dist == 2 ? 2 : 1); dy++) {
                level.destroyBlock(pos.relative(dir, dist).above(dy), false);
            }
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighbor, boolean moved) {
        if (block instanceof IDevMachine && level.getBlockState(neighbor).getBlock() instanceof net.minecraft.world.level.block.AirBlock) {
            level.destroyBlock(pos, false);
        }
        super.neighborChanged(state, level, pos, block, neighbor, moved);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        super.createBlockStateDefinition(builder);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }
}
