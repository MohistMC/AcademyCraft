package com.mohistmc.academy.world.item;

import com.mohistmc.academy.skill.AcademyAttachments;
import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.terminal.AppRegistry;
import com.mohistmc.academy.terminal.MediaTrackRegistry;
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
import org.jetbrains.annotations.NotNull;

/**
 * @author Mgazul
 * @date 2026/5/31 04:04
 */
public abstract class MediaItem extends AcademyItem {

    public MediaItem() {
        super(new Properties());
    }

    public abstract String getMediaId();

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!MediaTrackRegistry.isRegistered(getMediaId())) {
            if (level.isClientSide()) {
                player.displayClientMessage(Component.literal("§7[媒体播放器] §c曲目未注册: " + getMediaId()), true);
            }
            return InteractionResultHolder.fail(stack);
        }

        PlayerAbilityData data = player.getData(AcademyAttachments.PLAYER_ABILITY);

        if (!data.isTerminalInstalled()) {
            if (level.isClientSide()) {
                player.displayClientMessage(Component.literal("§7[数据终端] §c尚未安装数据终端。"), true);
            }
            return InteractionResultHolder.fail(stack);
        }

        if (!data.hasApp(AppRegistry.MEDIA_PLAYER)) {
            if (level.isClientSide()) {
                player.displayClientMessage(Component.literal("§7[数据终端] §c尚未安装媒体播放器APP。"), true);
            }
            return InteractionResultHolder.fail(stack);
        }

        if (data.hasLoadedMedia(getMediaId())) {
            if (level.isClientSide()) {
                player.displayClientMessage(Component.literal("§7[媒体播放器] §c该曲目已加载，无需重复加载。"), true);
            }
            return InteractionResultHolder.fail(stack);
        }

        data.addLoadedMedia(getMediaId());
        player.setData(AcademyAttachments.PLAYER_ABILITY, data);

        if (level.isClientSide()) {
            String name = Component.translatable(getDescriptionId()).getString();
            player.displayClientMessage(Component.literal("§7[媒体播放器] §a已加载: " + name), false);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> components, TooltipFlag flag) {
        super.appendHoverText(stack, context, components, flag);
        components.add(Component.literal("§7右键加载至媒体播放器").withStyle(ChatFormatting.GRAY));
    }
}
