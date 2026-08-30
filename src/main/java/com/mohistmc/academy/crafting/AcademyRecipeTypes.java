package com.mohistmc.academy.crafting;

import com.mohistmc.academy.AcademyCraft;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AcademyRecipeTypes {

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, AcademyCraft.MODID);

    public static final Supplier<RecipeType<MetalFormingRecipe>> METAL_FORMING =
            RECIPE_TYPES.register("metal_forming", () ->
                    RecipeType.simple(ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "metal_forming")));

    public static final Supplier<RecipeType<ImagFusorRecipe>> IMAG_FUSOR =
            RECIPE_TYPES.register("imag_fusor", () ->
                    RecipeType.simple(ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "imag_fusor")));
}
