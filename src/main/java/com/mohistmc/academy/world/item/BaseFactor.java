package com.mohistmc.academy.world.item;

import com.mohistmc.academy.skill.AbilityCategory;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

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
