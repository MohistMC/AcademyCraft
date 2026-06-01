package com.mohistmc.academy.world.block;

import com.mohistmc.academy.network.LearnSkillPacket;
import com.mohistmc.academy.network.OpenDevGuiPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public abstract class DevMachineSubBase extends BaseEntityBlock implements IDevMachine {

    public DevMachineSubBase(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.box(0, 0, 0, 1, 1.5, 1);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        BlockPos mainPos = findMainBlock((Level) level, pos);
        if (mainPos != null) {
            Item item = level.getBlockState(mainPos).getBlock().asItem();
            if (item != net.minecraft.world.item.Items.AIR) {
                return new ItemStack(item);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, net.minecraft.world.level.block.Block block, BlockPos neighbor, boolean moved) {
        if (block instanceof IDevMachine && level.getBlockState(neighbor).getBlock() instanceof AirBlock) {
            level.destroyBlock(pos, false);
        }
        super.neighborChanged(state, level, pos, block, neighbor, moved);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        BlockPos mainPos = findMainBlock(level, pos);
        if (mainPos != null) {
            BlockState mainState = level.getBlockState(mainPos);
            if (mainState.getBlock() instanceof DevMachineBase base) {
                return base.useWithoutItem(mainState, level, mainPos, player, hitResult);
            }
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            LearnSkillPacket.syncToClient(serverPlayer);
            PacketDistributor.sendToPlayer(serverPlayer, OpenDevGuiPacket.INSTANCE);
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    private BlockPos findMainBlock(Level level, BlockPos subPos) {
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            for (int dist = 1; dist <= 2; dist++) {
                for (int dy = 0; dy <= (dist == 2 ? 2 : 1); dy++) {
                    BlockPos check = subPos.relative(dir, dist).above(dy);
                    if (level.getBlockState(check).getBlock() instanceof DevMachineBase) {
                        return check;
                    }
                }
            }
        }
        return null;
    }
}
