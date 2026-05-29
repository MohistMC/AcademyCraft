package com.mohistmc.academy.world.item;

import com.mohistmc.academy.world.AcademyEntities;
import com.mohistmc.academy.world.AcademySounds;
import com.mohistmc.academy.world.entity.CoinEntity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class Coin extends AcademyItem {
    public Coin() {
        super(new Properties());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        // 检查玩家是否已经有硬币在空中
        if (hasCoinInAir(player)) {
            return InteractionResultHolder.fail(itemstack);
        }

        if (!level.isClientSide) {
            // 创建硬币实体
            CoinEntity coinEntity = new CoinEntity(AcademyEntities.COIN_ENTITY.get(), level);

            // 设置位置在玩家眼前
            Vec3 eyePos = player.getEyePosition();
            coinEntity.setPos(
                    eyePos.x,
                    eyePos.y,
                    eyePos.z
            );

            // 设置抛出的玩家
            coinEntity.setThrower(player);

            // 垂直向上抛，只给Y轴速度，X和Z轴速度为0
            float verticalPower = 0.6F; // 垂直向上的力度
            coinEntity.setDeltaMovement(0, verticalPower, 0); // 只设置Y轴速度

            // 设置随机初始旋转
            coinEntity.setYRot(level.random.nextFloat() * 360.0F);
            coinEntity.setXRot(level.random.nextFloat() * 360.0F);

            level.addFreshEntity(coinEntity);
            level.playSound(null, player.blockPosition(), AcademySounds.FLIPCOIN.value(), SoundSource.PLAYERS, 1.0F, 1.0F);

            // 消耗一个硬币
            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
        }

        player.swing(hand);
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

    // 检查玩家是否有硬币在空中
    private boolean hasCoinInAir(Player player) {
        return CoinEntity.hasPlayerCoinInAir(player);
    }
}