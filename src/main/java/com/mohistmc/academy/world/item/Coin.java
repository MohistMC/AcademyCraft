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

        if (hasCoinInAir(player)) {
            return InteractionResultHolder.fail(itemstack);
        }

        if (!level.isClientSide) {
            CoinEntity coinEntity = new CoinEntity(AcademyEntities.COIN_ENTITY.get(), level);

            Vec3 eyePos = player.getEyePosition();
            coinEntity.setPos(
                    eyePos.x,
                    eyePos.y,
                    eyePos.z
            );

            coinEntity.setThrower(player);

            float verticalPower = 0.6F; // 垂直向上的力度
            coinEntity.setDeltaMovement(0, verticalPower, 0);

            coinEntity.setYRot(level.random.nextFloat() * 360.0F);
            coinEntity.setXRot(level.random.nextFloat() * 360.0F);

            level.addFreshEntity(coinEntity);
            player.playSound(AcademySounds.ENTITY_FLIPCOIN.value(), 1.0F, 1.0F);

            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
        }

        player.swing(hand);
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

    private boolean hasCoinInAir(Player player) {
        return CoinEntity.hasPlayerCoinInAir(player);
    }
}