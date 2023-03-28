package com.mohistmc.academy.world.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AcademyItem extends Item {
    public AcademyItem(Properties p_41383_) {
        super(p_41383_);
    }


    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
        String key = getDescriptionId() + ".desc";
        Component tag = Component.translatable(key);
        if (!key.equalsIgnoreCase(tag.getString())) {
            /*
            CompoundTag compoundtag = itemStack.getTagElement(this.getDescriptionId());
            if (compoundtag == null){
                compoundtag = itemStack.getOrCreateTagElement(this.getDescriptionId());
            }
             */
            components.add(Component.translatable(key).withStyle(ChatFormatting.GRAY));
        }
    }
}
