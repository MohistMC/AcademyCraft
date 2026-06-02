package com.mohistmc.academy.world.block;

import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

public abstract class DevMachineSubBase extends BaseEntityBlock implements IDevMachine {

    public DevMachineSubBase(Properties properties) {
        super(properties);
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
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!level.isClientSide() && !newState.is(state.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof IDevSubStructure s) {
                BlockPos mainPos = s.getMainPos();
                if (mainPos != null) {
                    BlockState mainState = level.getBlockState(mainPos);
                    if (mainState.getBlock() instanceof DevMachineBase base) {
                        Direction dir = mainState.getValue(DevMachineBase.FACING).getOpposite();
                        List<DevMachineBase.SubBlockPos> subs = base.getRotatedSubBlocks(dir);
                        for (DevMachineBase.SubBlockPos sub : subs) {
                            BlockPos subPos = mainPos.offset(sub.dx(), sub.dy(), sub.dz());
                            if (!subPos.equals(pos)) {
                                BlockEntity subBe = level.getBlockEntity(subPos);
                                if (subBe instanceof IDevSubStructure subS) {
                                    if (mainPos.equals(subS.getMainPos())) {
                                        level.destroyBlock(subPos, false);
                                    }
                                }
                            }
                        }
                        level.destroyBlock(mainPos, false);
                    }
                }
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
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
        return InteractionResult.PASS;
    }

    @Nullable
    private BlockPos findMainBlock(Level level, BlockPos subPos) {
        BlockEntity be = level.getBlockEntity(subPos);
        if (be instanceof IDevSubStructure s) {
            BlockPos mainPos = s.getMainPos();
            if (mainPos != null) {
                BlockEntity mainBe = level.getBlockEntity(mainPos);
                if (mainBe instanceof IDevStructure main) {
                    UUID subId = s.getStructureId();
                    UUID mainId = main.getStructureId();
                    if (subId != null && subId.equals(mainId)) {
                        return mainPos;
                    }
                }
            }
        }
        return null;
    }
}
