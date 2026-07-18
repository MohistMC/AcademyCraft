package com.mohistmc.academy.crafting;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.ItemStack;

/**
 * 金属成型机配方注册。
 *
 * @author WeAthFolD (original), Mgazul (port)
 */
public class MetalFormerRecipes {

    public enum Mode {
        /** 切割 — 晶圆切割等 */
        INCISE,
        /** 蚀刻 — 芯片制造 */
        ETCH,
        /** 压板 — 金属板成型 */
        PLATE,
        /** 精炼 — 矿物倍产 */
        REFINE
    }

    public static final MetalFormerRecipes INSTANCE = new MetalFormerRecipes();

    private final List<RecipeObject> recipes = new ArrayList<>();

    private MetalFormerRecipes() {}

    /**
     * 添加配方。
     */
    public void add(ItemStack input, ItemStack output, Mode mode) {
        recipes.add(new RecipeObject(input.copy(), output.copy(), mode));
    }

    public RecipeObject getRecipe(ItemStack input, Mode mode) {
        for (RecipeObject recipe : recipes) {
            if (recipe.accepts(input, mode)) return recipe;
        }
        return null;
    }

    public List<RecipeObject> getAllRecipes() {
        return recipes;
    }

    // ==================== 配方记录 ====================

    public static class RecipeObject {

        public final Mode mode;
        public final ItemStack input;
        public final ItemStack output;

        private RecipeObject(ItemStack input, ItemStack output, Mode mode) {
            this.input = input;
            this.output = output;
            this.mode = mode;
        }

        public boolean accepts(ItemStack stack, Mode targetMode) {
            return mode == targetMode
                    && ItemStack.isSameItem(input, stack)
                    && input.getCount() <= stack.getCount();
        }
    }
}
