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

public abstract class BaseApp extends AcademyItem {

    public BaseApp(Properties p_41383_) {
        super(p_41383_);
    }

    public abstract String getAppId();

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        PlayerAbilityData data = player.getData(AcademyAttachments.PLAYER_ABILITY);

        if (!data.isTerminalInstalled()) {
            if (level.isClientSide()) {
                player.displayClientMessage(Component.literal("§7[数据终端] §c尚未安装数据终端，请先使用数据终端安装器。"), true);
            }
            return InteractionResultHolder.fail(stack);
        }

        if (data.hasApp(getAppId())) {
            if (level.isClientSide()) {
                player.displayClientMessage(Component.literal("§7[数据终端] §c该APP已安装，无需重复安装。"), true);
            }
            return InteractionResultHolder.fail(stack);
        }

        data.installApp(getAppId());
        data.syncTo(player);

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        if (level.isClientSide()) {
            String appName = Component.translatable(getDescriptionId()).getString();
            player.displayClientMessage(Component.literal("§7[数据终端] §aAPP \"" + appName + "\" 安装成功！"), false);
        }

        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext p_333372_, List<Component> components, TooltipFlag tooltipFlag) {
        components.add(Component.literal("§7右键安装至数据终端").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public Component getName(ItemStack p_41458_) {
        return Component.translatable(getDescriptionId());
    }
}
