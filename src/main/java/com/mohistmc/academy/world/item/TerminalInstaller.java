package com.mohistmc.academy.world.item;

import com.mohistmc.academy.skill.AcademyAttachments;
import com.mohistmc.academy.skill.PlayerAbilityData;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class TerminalInstaller extends AcademyItem {
    public TerminalInstaller() {
        super(new Properties());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        PlayerAbilityData data = player.getData(AcademyAttachments.PLAYER_ABILITY);

        if (data.isTerminalInstalled()) {
            if (level.isClientSide()) {
                player.displayClientMessage(Component.literal("§7[数据终端] §c你已经安装过数据终端了。"), true);
            }
            return InteractionResultHolder.fail(stack);
        }

        data.setTerminalInstalled(true);
        data.syncTo(player);

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        if (level.isClientSide()) {
            player.displayClientMessage(Component.literal("§7[数据终端] §a数据终端安装成功！按 Alt 打开终端界面。"), false);
        }

        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> components, TooltipFlag flag) {
        components.add(Component.literal("§7右键安装数据终端"));
        components.add(Component.literal("§7安装后可按 Alt 打开终端界面"));
    }
}
