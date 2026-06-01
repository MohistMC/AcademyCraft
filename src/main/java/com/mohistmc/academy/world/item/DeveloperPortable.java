package com.mohistmc.academy.world.item;

import com.mohistmc.academy.capability.EnergyItemHelper;
import com.mohistmc.academy.network.LearnSkillPacket;
import com.mohistmc.academy.network.OpenDevGuiPacket;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

public class DeveloperPortable extends AcademyItem {

    public static final int MAX_ENERGY = 10000;

    public DeveloperPortable() {
        super(new Properties().durability(MAX_ENERGY));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer serverPlayer) {
            LearnSkillPacket.syncToClient(serverPlayer);
            PacketDistributor.sendToPlayer(serverPlayer, OpenDevGuiPacket.INSTANCE);
        }
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void appendHoverText(ItemStack p_41421_, Item.TooltipContext p_333372_, List<Component> p_41423_, TooltipFlag p_41424_) {
        super.appendHoverText(p_41421_, p_333372_, p_41423_, p_41424_);
        int energy = EnergyItemHelper.getEnergy(p_41421_);
        p_41423_.add(Component.literal(energy + "/" + MAX_ENERGY + " IF"));
    }
}
