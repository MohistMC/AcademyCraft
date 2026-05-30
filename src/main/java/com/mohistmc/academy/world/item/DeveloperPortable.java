package com.mohistmc.academy.world.item;

import com.mohistmc.academy.client.gui.DevNormalGui;
import com.mohistmc.academy.network.LearnSkillPacket;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class DeveloperPortable extends AcademyItem {

    public static int maxEnergy = 10000;

    public DeveloperPortable() {
        super(new Properties().durability(maxEnergy));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            Minecraft.getInstance().setScreen(new DevNormalGui());
            return InteractionResultHolder.consume(stack);
        }
        if (player instanceof ServerPlayer serverPlayer) {
            LearnSkillPacket.syncToClient(serverPlayer);
        }
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void appendHoverText(ItemStack p_41421_, Item.TooltipContext p_333372_, List<Component> p_41423_, TooltipFlag p_41424_) {
        super.appendHoverText(p_41421_, p_333372_, p_41423_, p_41424_);
        p_41423_.add(Component.translatable((p_41421_.getMaxDamage() - p_41421_.getDamageValue()) + "/" + p_41421_.getMaxDamage() + " IF"));
    }
}