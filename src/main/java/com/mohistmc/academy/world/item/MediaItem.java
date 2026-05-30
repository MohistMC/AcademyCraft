package com.mohistmc.academy.world.item;

import com.mohistmc.academy.skill.AcademyAttachments;
import com.mohistmc.academy.skill.PlayerAbilityData;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * @author Mgazul
 * @date 2026/5/31 04:04
 */
public class MediaItem extends AcademyItem {

    private final String mediaId;
    private final String loadMessage;

    public MediaItem(String mediaId, String loadMessage) {
        super(new Properties());
        this.mediaId = mediaId;
        this.loadMessage = loadMessage;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        PlayerAbilityData data = player.getData(AcademyAttachments.PLAYER_ABILITY);

        if (!data.isTerminalInstalled()) {
            if (level.isClientSide()) {
                player.displayClientMessage(Component.literal("§7[数据终端] §c尚未安装数据终端。"), true);
            }
            return InteractionResultHolder.fail(stack);
        }

        if (!data.hasApp("media_player")) {
            if (level.isClientSide()) {
                player.displayClientMessage(Component.literal("§7[数据终端] §c尚未安装媒体播放器APP。"), true);
            }
            return InteractionResultHolder.fail(stack);
        }

        data.addLoadedMedia(mediaId);
        player.setData(AcademyAttachments.PLAYER_ABILITY, data);

        if (level.isClientSide()) {
            player.displayClientMessage(Component.literal(loadMessage), false);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> components, TooltipFlag flag) {
        super.appendHoverText(stack, context, components, flag);
        components.add(Component.literal("§7右键加载至媒体播放器").withStyle(ChatFormatting.GRAY));
    }
}
