package com.mohistmc.academy.world.item;

import com.mohistmc.academy.world.AcademyBlocks;
import com.mohistmc.academy.world.AcademyItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class MatterUnitPhaseLiquid extends AcademyItem {
    public MatterUnitPhaseLiquid() {
        super(new Properties());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        BlockHitResult hitResult = getPlayerPOVHitResult(level, player);

        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = hitResult.getBlockPos();
            Direction direction = hitResult.getDirection();
            BlockPos placePos = pos.relative(direction);

            if (!level.mayInteract(player, pos)) {
                return InteractionResultHolder.pass(stack);
            }

            BlockState placeState = level.getBlockState(placePos);

            if (placeState.isAir() || placeState.canBeReplaced()) {
                if (!level.isClientSide) {
                    // 如果目标位置有可被替换的方块（如高草），先破坏
                    if (!placeState.isAir()) {
                        level.destroyBlock(placePos, true);
                    }

                    level.setBlock(placePos, AcademyBlocks.PHASE_LIQUID.get().defaultBlockState(), 3);
                    level.playSound(null, placePos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);

                    // 非创造模式：消耗一个液体单元，给予一个空单元
                    if (!player.isCreative()) {
                        ItemStack result = stack.copy();
                        result.shrink(1);
                        ItemStack emptyStack = new ItemStack(AcademyItems.MATTER_UNIT_NONE.get());

                        if (result.isEmpty()) {
                            return InteractionResultHolder.success(emptyStack);
                        } else {
                            if (!player.addItem(emptyStack)) {
                                player.drop(emptyStack, false);
                            }
                            return InteractionResultHolder.success(result);
                        }
                    }
                }

                return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
            }
        }

        return InteractionResultHolder.pass(stack);
    }

    /**
     * 获取玩家视线方向的方块命中结果
     */
    private BlockHitResult getPlayerPOVHitResult(Level level, Player player) {
        Vec3 eyePos = player.getEyePosition(1.0F);
        Vec3 viewVec = player.getViewVector(1.0F);
        double reach = 5.0;
        Vec3 endPos = eyePos.add(viewVec.x * reach, viewVec.y * reach, viewVec.z * reach);

        return level.clip(new ClipContext(
                eyePos, endPos,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                player
        ));
    }
}
