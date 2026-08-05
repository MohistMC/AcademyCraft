package com.mohistmc.academy.crafting;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.ItemStack;

/**
 * 想象熔炉配方注册。
 */
public class ImagFusorRecipes {

    public static final ImagFusorRecipes INSTANCE = new ImagFusorRecipes();

    private final List<IFRecipe> recipes = new ArrayList<>();

    private ImagFusorRecipes() {}

    /**
     * 添加配方。
     * @param consume 消耗的物品
     * @param phaseLiquid 需要的 PhaseLiquid 量
     * @param output 产物
     */
    public void add(ItemStack consume, int phaseLiquid, ItemStack output) {
        for (IFRecipe r : recipes) {
            if (r.matches(consume)) {
                throw new RuntimeException("Duplicate ImagFusor recipe for item: " + consume);
            }
        }
        recipes.add(new IFRecipe(consume, phaseLiquid, output));
    }

    /** 查找匹配的配方，找不到返回 null */
    public IFRecipe getRecipe(ItemStack input) {
        for (IFRecipe r : recipes) {
            if (r.matches(input)) return r;
        }
        return null;
    }

    public List<IFRecipe> getAllRecipes() {
        return recipes;
    }

    // ==================== 配方记录 ====================

    public record IFRecipe(ItemStack input, int phaseLiquid, ItemStack output) {

        public IFRecipe {
            // 防御性拷贝
            input = input.copy();
            output = output.copy();
        }

        public boolean matches(ItemStack stack) {
            return ItemStack.isSameItem(input, stack);
        }
    }
}
