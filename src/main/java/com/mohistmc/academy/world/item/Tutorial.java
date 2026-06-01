package com.mohistmc.academy.world.item;

import com.mohistmc.academy.network.OpenTutorialGuiPacket;
import com.mohistmc.academy.skill.AcademyAttachments;
import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.terminal.AppRegistry;
import com.mohistmc.academy.utils.RandUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

public class Tutorial extends Item {
    public Tutorial() {
        super(new Item.Properties());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            PlayerAbilityData data = player.getData(AcademyAttachments.PLAYER_ABILITY);
            if (data.getMisakaId() < 0) {
                data.setMisakaId(RandUtils.rangei(1000, 19000));
            }
            if (!data.hasApp(AppRegistry.TUTORIAL.getAppId())) {
                data.installApp(AppRegistry.TUTORIAL.getAppId());
            }
            data.syncTo(player);
            PacketDistributor.sendToPlayer((ServerPlayer) player, new OpenTutorialGuiPacket());
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
