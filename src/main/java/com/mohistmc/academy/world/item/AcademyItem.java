package com.mohistmc.academy.world.item;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class AcademyItem extends Item {
    public AcademyItem(Properties p_41383_) {
        super(p_41383_);
    }


    @Override
    public void appendHoverText(ItemStack p_41421_, Item.TooltipContext p_333372_, List<Component> p_41423_, TooltipFlag p_41424_) {
        String key = getDescriptionId() + ".desc";
        Component tag = Component.translatable(key);
        if (!key.equalsIgnoreCase(tag.getString())) {
            /*
            CompoundTag compoundtag = itemStack.getTagElement(this.getDescriptionId());
            if (compoundtag == null){
                compoundtag = itemStack.getOrCreateTagElement(this.getDescriptionId());
            }
             */
            p_41423_.add(Component.translatable(key).withStyle(ChatFormatting.GRAY));
        }
        super.appendHoverText(p_41421_, p_333372_, p_41423_, p_41424_);
    }
}
