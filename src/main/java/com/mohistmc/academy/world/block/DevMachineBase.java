package com.mohistmc.academy.world.block;

import com.mohistmc.academy.capability.IFEnergyStorage;
import com.mohistmc.academy.network.LearnSkillPacket;
import com.mohistmc.academy.network.OpenDevGuiPacket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
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
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public abstract class DevMachineBase extends BaseEntityBlock implements IDevMachine {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    private final List<SubBlockPos> subBlocks = new ArrayList<>();
    private List<SubBlockPos>[] rotatedBuffer;
    private boolean init = false;

    public record SubBlockPos(int dx, int dy, int dz) {}

    public DevMachineBase(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
        this.addSubBlock(0, 1, 0);
        this.addSubBlock(0, 0, 1);
        this.addSubBlock(0, 1, 1);
        this.addSubBlock(0, 2, 1);
        this.addSubBlock(0, 0, 2);
        this.addSubBlock(0, 1, 2);
        this.addSubBlock(0, 2, 2);
        finishInit();
    }

    protected void addSubBlock(int dx, int dy, int dz) {
        if (init) {
            throw new RuntimeException("Trying to add a sub block after block init finished");
        }
        subBlocks.add(new SubBlockPos(dx, dy, dz));
    }

    @SuppressWarnings("unchecked")
    private void finishInit() {
        rotatedBuffer = new List[4];
        for (int i = 0; i < 4; i++) {
            Direction dir = Direction.from2DDataValue(i);
            rotatedBuffer[i] = new ArrayList<>();
            for (SubBlockPos s : subBlocks) {
                rotatedBuffer[i].add(rotateSouth(s, dir));
            }
        }
        init = true;
    }

    public List<SubBlockPos> getRotatedSubBlocks(Direction dir) {
        return rotatedBuffer[dir.get2DDataValue()];
    }

    private static SubBlockPos rotateSouth(SubBlockPos s, Direction dir) {
        return switch (dir) {
            case SOUTH -> new SubBlockPos(s.dx, s.dy, s.dz);
            case NORTH -> new SubBlockPos(-s.dx, s.dy, -s.dz);
            case EAST -> new SubBlockPos(s.dz, s.dy, -s.dx);
            case WEST -> new SubBlockPos(-s.dz, s.dy, s.dx);
            default -> new SubBlockPos(s.dx, s.dy, s.dz);
        };
    }

    protected abstract Block getSubBlock();

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
            PacketDistributor.sendToPlayer(serverPlayer, new OpenDevGuiPacket(devType.ordinal(), energy, maxEnergy, java.util.Optional.of(pos)));
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moved) {
        Direction dir = state.getValue(FACING).getOpposite();
        Block sub = getSubBlock();
        List<SubBlockPos> subList = getRotatedSubBlocks(dir);

        for (SubBlockPos subPos : subList) {
            BlockPos target = pos.offset(subPos.dx, subPos.dy, subPos.dz);
            if (!level.isEmptyBlock(target)) {
                level.destroyBlock(pos, true);
                return;
            }
        }

        UUID id = UUID.randomUUID();
        BlockEntity mainBe = level.getBlockEntity(pos);
        if (mainBe instanceof IDevStructure s) {
            s.setStructureId(id);
        }

        for (SubBlockPos subPos : subList) {
            BlockPos target = pos.offset(subPos.dx, subPos.dy, subPos.dz);
            level.setBlock(target, sub.defaultBlockState(), 19);
            BlockEntity subBe = level.getBlockEntity(target);
            if (subBe instanceof IDevSubStructure s) {
                s.setStructureId(id);
                s.setMainPos(pos);
            }
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!level.isClientSide() && !newState.is(state.getBlock())) {
            Direction dir = state.getValue(FACING).getOpposite();
            BlockEntity mainBe = level.getBlockEntity(pos);
            UUID mainId = (mainBe instanceof IDevStructure dev) ? dev.getStructureId() : null;
            List<SubBlockPos> subList = getRotatedSubBlocks(dir);
            for (SubBlockPos sub : subList) {
                BlockPos subPos = pos.offset(sub.dx, sub.dy, sub.dz);
                BlockEntity subBe = level.getBlockEntity(subPos);
                if (subBe instanceof IDevSubStructure s) {
                    UUID subId = s.getStructureId();
                    if (pos.equals(s.getMainPos()) && (mainId == null || mainId.equals(subId))) {
                        level.destroyBlock(subPos, false);
                    }
                }
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
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
        BlockState state = this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
        Direction dir = state.getValue(FACING).getOpposite();
        BlockPos pos = context.getClickedPos();
        List<SubBlockPos> subList = getRotatedSubBlocks(dir);

        for (SubBlockPos sub : subList) {
            BlockPos subPos = pos.offset(sub.dx, sub.dy, sub.dz);
            if (!context.getLevel().isEmptyBlock(subPos)) {
                return null;
            }
        }

        return state;
    }
}
