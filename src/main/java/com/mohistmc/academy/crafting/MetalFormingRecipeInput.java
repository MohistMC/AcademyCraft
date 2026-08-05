package com.mohistmc.academy.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

/**
 * 金属成型机配方输入 —— 物品 + 当前机器模式。
 */
public record MetalFormingRecipeInput(ItemStack input, MetalFormerRecipes.Mode mode) implements RecipeInput {

    @Override
    public ItemStack getItem(int index) {
        return index == 0 ? input : ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return 1;
    }
}
