package com.mohistmc.academy.tutorial;

import com.mohistmc.academy.tutorial.ACTutorial.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * 教程预览视图组。
 */
public interface ViewGroup {

    Tag getTag();

    default String getDisplayText() {
        return "";
    }

    default ItemStack previewStack() {
        return ItemStack.EMPTY;
    }

    default ResourceLocation previewIcon() {
        return null;
    }

    default ItemStack recipeTarget() {
        return ItemStack.EMPTY;
    }
}
