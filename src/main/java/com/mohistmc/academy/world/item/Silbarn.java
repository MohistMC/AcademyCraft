package com.mohistmc.academy.world.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class Silbarn extends AcademyItem {

    public Silbarn() {
        super(new Properties());
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level p_41432_, Player p_41433_, InteractionHand p_41434_) {
        ItemStack stack = p_41433_.getItemInHand(p_41434_);
        p_41432_.playSound(
                p_41433_, p_41433_.getX(), p_41433_.getY(), p_41433_.getZ(),
                SoundEvents.EGG_THROW,
                SoundSource.PLAYERS,
                0.5F,
                0.4F / (p_41432_.random.nextFloat() * 0.4F + 0.8F)
        );
        return InteractionResultHolder.pass(stack);
    }
}