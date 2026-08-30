package com.mohistmc.academy.support.jei;

import com.mohistmc.academy.crafting.ImagFusorRecipes.IFRecipe;
import com.mohistmc.academy.utils.Resources;
import com.mohistmc.academy.world.AcademyBlocks;
import com.mohistmc.academy.world.AcademyItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class FusorCategory implements IRecipeCategory<IFRecipe> {

    public static final RecipeType<IFRecipe> TYPE = new RecipeType<>(Resources.id("imag_fusor"), IFRecipe.class);

    private static final int WIDTH = 115, HEIGHT = 66;
    private static final ResourceLocation BG = Resources.id("textures/guis/nei_fusor.png");

    private final IDrawable background;
    private final IDrawable icon;

    public FusorCategory(IGuiHelper gui) {
        this.background = gui.drawableBuilder(BG, 0, 0, WIDTH, HEIGHT).setTextureSize(WIDTH, HEIGHT).build();
        this.icon = gui.createDrawableItemStack(new ItemStack(AcademyItems.IMAG_FUSOR.get()));
    }

    @Override
    public RecipeType<IFRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return AcademyBlocks.IMAG_FUSOR.get().getName();
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
    public void setRecipe(IRecipeLayoutBuilder builder, IFRecipe recipe, IFocusGroup focuses) {

        builder.addSlot(RecipeIngredientRole.INPUT, 6, 37).addItemStack(recipe.input());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 94, 37).addItemStack(recipe.output());
    }
}
