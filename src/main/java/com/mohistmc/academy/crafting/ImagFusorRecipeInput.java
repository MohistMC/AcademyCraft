package com.mohistmc.academy.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

/**
 * ImagFusor recipe input -- a single input item stack.
 */
public record ImagFusorRecipeInput(ItemStack stack) implements RecipeInput {

    @Override
    public ItemStack getItem(int index) {
        return index == 0 ? stack : ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return 1;
    }
}