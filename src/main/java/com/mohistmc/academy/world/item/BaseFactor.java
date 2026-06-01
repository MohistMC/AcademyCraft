package com.mohistmc.academy.world.item;

import com.mohistmc.academy.skill.AbilityCategory;
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

public class BaseFactor extends AcademyItem {

    private final AbilityCategory category;

    public BaseFactor(Properties properties, AbilityCategory category) {
        super(properties);
        this.category = category;
    }

    public AbilityCategory getCategory() {
        return category;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            return super.use(level, player, hand);
        }

        ItemStack stack = player.getItemInHand(hand);

        PlayerAbilityData data = player.getData(AcademyAttachments.PLAYER_ABILITY);
        if (data.hasAbility()) {
            if (player.isCreative()) {
                player.sendSystemMessage(Component.literal("[测试用/临时] 已更换职业为: ")
                        .append(Component.translatable(category.getTranslationKey())));
                data.reset();
            } else {
                player.sendSystemMessage(Component.literal("你已经有能力了!"));
                return InteractionResultHolder.fail(stack);
            }
        }

        data.setCurrentAbility(category);
        data.setPlayerLevel(1);
        data.syncTo(player);
        if (!player.isCreative()) {
            player.sendSystemMessage(Component.literal("成功学习: ").append(Component.translatable(category.getTranslationKey())));
        }

        if (!player.isCreative()) {
            stack.shrink(1);
        }

        return InteractionResultHolder.success(stack);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext p_333372_, List<Component> components, TooltipFlag tooltipFlag) {
        String key = getDescriptionId();
        Component tag = Component.translatable(key);
        if (!key.equalsIgnoreCase(tag.getString())) {
            components.add(Component.translatable(key).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public Component getName(ItemStack p_41458_) {
        return Component.translatable("item.academy.induction_factor");
    }
}
