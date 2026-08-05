package com.mohistmc.academy.world.item;

import com.mohistmc.academy.world.AcademyBlocks;
import com.mohistmc.academy.world.AcademyItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class MatterUnitNone extends AcademyItem {
    public MatterUnitNone() {
        super(new Properties());
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        BlockState clickedState = level.getBlockState(clickedPos);

        if (isPhaseLiquidSource(clickedState)) {
            return handleInteraction(context, clickedPos);
        }

        // 否则检查点击的面所指向的相邻位置（液体通常在这个方向）
        BlockPos targetPos = clickedPos.relative(context.getClickedFace());
        BlockState targetState = level.getBlockState(targetPos);
        if (isPhaseLiquidSource(targetState)) {
            return handleInteraction(context, targetPos);
        }

        return super.useOn(context);
    }

    private boolean isPhaseLiquidSource(BlockState state) {
        return state.is(AcademyBlocks.PHASE_LIQUID.get()) && state.getFluidState().isSource();
    }

    private InteractionResult handleInteraction(UseOnContext context, BlockPos pos) {
        Level level = context.getLevel();
        if (!level.isClientSide) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);

            Player player = context.getPlayer();
            ItemStack stack = context.getItemInHand();
            stack.shrink(1);

            ItemStack result = new ItemStack(AcademyItems.MATTER_UNIT_PHASE_LIQUID.get());
            if (player != null && !player.addItem(result)) {
                player.drop(result, false);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
