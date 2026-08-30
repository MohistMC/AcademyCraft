package com.mohistmc.academy.support.jei;

import com.mohistmc.academy.crafting.AcademyRecipeTypes;
import com.mohistmc.academy.crafting.ImagFusorRecipe;
import com.mohistmc.academy.crafting.MetalFormingRecipe;
import com.mohistmc.academy.utils.Resources;
import com.mohistmc.academy.world.AcademyItems;
import java.util.List;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JeiPlugin
public class ACJeiPlugin implements IModPlugin {

    private static final ResourceLocation UID = Resources.id("jei");
    private static final Logger LOGGER = LoggerFactory.getLogger(ACJeiPlugin.class);

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper gui = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new FusorCategory(gui),
                new MetalFormerCategory(gui));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {

        // ImagFusor / MetalForming recipes are data-driven and synced to the client's RecipeManager
        var level = Minecraft.getInstance().level;
        if (level == null) {
            LOGGER.warn("[AC-JEI] registerRecipes: Minecraft level is null, skipping recipe registration");
            return;
        }

        List<ImagFusorRecipe> imagFusor = level.getRecipeManager()
                .getAllRecipesFor(AcademyRecipeTypes.IMAG_FUSOR.get())
                .stream().map(RecipeHolder::value).toList();
        registration.addRecipes(FusorCategory.TYPE, imagFusor);

        List<RecipeHolder<MetalFormingRecipe>> mfHolders = level.getRecipeManager()
                .getAllRecipesFor(AcademyRecipeTypes.METAL_FORMING.get());
        List<MetalFormingRecipe> metalForming = mfHolders.stream().map(RecipeHolder::value).toList();

        LOGGER.info("[AC-JEI] registerRecipes: registering {} imag fusor / {} metal forming recipes",
                imagFusor.size(), metalForming.size());
        registration.addRecipes(MetalFormerCategory.TYPE, metalForming);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {

        registration.addRecipeCatalyst(new ItemStack(AcademyItems.IMAG_FUSOR.get()), FusorCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(AcademyItems.METAL_FORMER.get()), MetalFormerCategory.TYPE);
    }
}