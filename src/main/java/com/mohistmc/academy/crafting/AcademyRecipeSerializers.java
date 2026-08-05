package com.mohistmc.academy.crafting;

import com.mohistmc.academy.AcademyCraft;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AcademyRecipeSerializers {

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, AcademyCraft.MODID);

    public static final Supplier<RecipeSerializer<MetalFormingRecipe>> METAL_FORMING =
            SERIALIZERS.register("metal_forming", MetalFormingRecipe.Serializer::new);
}
