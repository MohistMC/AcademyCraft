package com.mohistmc.academy.crafting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/**
 * 虚能融合机配方 —— 数据驱动（JSON 配方文件），消耗液体与输入物品产出产物。
 */
public class ImagFusorRecipe implements Recipe<ImagFusorRecipeInput> {

    private final Ingredient input;
    private final int phaseLiquid;
    private final ItemStack output;

    public ImagFusorRecipe(Ingredient input, int phaseLiquid, ItemStack output) {
        this.input = input;
        this.phaseLiquid = phaseLiquid;
        this.output = output;
    }

    /** 所需 PhaseLiquid 量 */
    public int getPhaseLiquid() {
        return phaseLiquid;
    }

    public ItemStack getOutput() {
        return output.copy();
    }

    @Override
    public boolean matches(ImagFusorRecipeInput input, Level level) {
        return this.input.test(input.stack());
    }

    @Override
    public ItemStack assemble(ImagFusorRecipeInput input, HolderLookup.Provider provider) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return output.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return AcademyRecipeSerializers.IMAG_FUSOR.get();
    }

    @Override
    public RecipeType<?> getType() {
        return AcademyRecipeTypes.IMAG_FUSOR.get();
    }

    /** 不参与合成台，仅在虚能融合机中使用 */
    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(input);
        return ingredients;
    }

    public static class Serializer implements RecipeSerializer<ImagFusorRecipe> {

        private static final MapCodec<ImagFusorRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.fieldOf("input").forGetter(r -> r.input),
                com.mojang.serialization.Codec.INT.fieldOf("phaseLiquid").forGetter(r -> r.phaseLiquid),
                ItemStack.CODEC.fieldOf("output").forGetter(r -> r.output)
        ).apply(instance, ImagFusorRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, ImagFusorRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, r -> r.input,
                ByteBufCodecs.VAR_INT, r -> r.phaseLiquid,
                ItemStack.STREAM_CODEC, r -> r.output,
                ImagFusorRecipe::new);

        @Override
        public MapCodec<ImagFusorRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ImagFusorRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}