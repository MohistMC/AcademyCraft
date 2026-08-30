package com.mohistmc.academy.support.jei;

import com.mohistmc.academy.crafting.MetalFormingRecipe;
import com.mohistmc.academy.utils.Resources;
import com.mohistmc.academy.world.AcademyBlocks;
import com.mohistmc.academy.world.AcademyItems;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class MetalFormerCategory implements IRecipeCategory<MetalFormingRecipe> {

    public static final RecipeType<MetalFormingRecipe> TYPE =
            new RecipeType<>(Resources.id("metal_former"), MetalFormingRecipe.class);

    private static final int WIDTH = 94, HEIGHT = 57;
    private static final ResourceLocation BG = Resources.id("textures/guis/nei_metalformer.png");

    private static final ResourceLocation[] MODE_ICONS = {
            modeIcon("plate"), modeIcon("incise"), modeIcon("etch"), modeIcon("refine")
    };
    private final IDrawable background;
    private final IDrawable icon;
    public MetalFormerCategory(IGuiHelper gui) {
        this.background = gui.drawableBuilder(BG, 0, 0, WIDTH, HEIGHT).setTextureSize(WIDTH, HEIGHT).build();
        this.icon = gui.createDrawableItemStack(new ItemStack(AcademyItems.METAL_FORMER.get()));
    }

    private static ResourceLocation modeIcon(String m) {
        return Resources.id("textures/guis/icons/icon_former_" + m + ".png");
    }

    @Override
    public RecipeType<MetalFormingRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return AcademyBlocks.METAL_FORMER.get().getName();
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MetalFormingRecipe recipe, IFocusGroup focuses) {

        List<ItemStack> inputs = recipe.getIngredients().stream()
                .map(Ingredient::getItems)
                .map(items -> items.length == 0 ? ItemStack.EMPTY : items[0])
                .filter(s -> !s.isEmpty())
                .toList();
        if (!inputs.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 6, 24).addItemStacks(inputs);
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 72, 24).addItemStack(recipe.getOutput());
    }

    @Override
    public void draw(MetalFormingRecipe recipe, IRecipeSlotsView view, GuiGraphics g, double mouseX, double mouseY) {

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        g.blit(MODE_ICONS[recipe.getMode().ordinal()], 39, 24, 16, 16, 0, 0, 16, 16, 16, 16);
    }
}