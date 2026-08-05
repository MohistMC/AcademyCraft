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
 * 金属成型机配方 —— 数据驱动（JSON 配方文件），按模式匹配输入输出。
 */
public class MetalFormingRecipe implements Recipe<MetalFormingRecipeInput> {

    private final MetalFormerRecipes.Mode mode;
    private final Ingredient input;
    private final int inputCount;
    private final ItemStack output;

    public MetalFormingRecipe(MetalFormerRecipes.Mode mode, Ingredient input, int inputCount, ItemStack output) {
        this.mode = mode;
        this.input = input;
        this.inputCount = inputCount;
        this.output = output;
    }

    public MetalFormerRecipes.Mode getMode() {
        return mode;
    }

    /** 检查物品是否是该配方的输入（用于输入槽限制） */
    public boolean matchesItem(ItemStack stack) {
        return input.test(stack);
    }

    /** 需要消耗的输入数量 */
    public int getInputCount() {
        return inputCount;
    }

    public ItemStack getOutput() {
        return output.copy();
    }

    @Override
    public boolean matches(MetalFormingRecipeInput input_, Level level) {
        return input_.mode() == mode && input.test(input_.input());
    }

    @Override
    public ItemStack assemble(MetalFormingRecipeInput input_, HolderLookup.Provider provider) {
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
        return AcademyRecipeSerializers.METAL_FORMING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return AcademyRecipeTypes.METAL_FORMING.get();
    }

    /** 不参与合成台，仅在金属成型机中使用 */
    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(input);
    }

    public static class Serializer implements RecipeSerializer<MetalFormingRecipe> {

        private static final MapCodec<MetalFormingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                com.mojang.serialization.Codec.STRING.fieldOf("mode").forGetter(r -> r.mode.name().toLowerCase(java.util.Locale.ROOT)),
                Ingredient.CODEC.fieldOf("input").forGetter(r -> r.input),
                com.mojang.serialization.Codec.INT.optionalFieldOf("count", 1).forGetter(r -> r.inputCount),
                ItemStack.CODEC.fieldOf("output").forGetter(r -> r.output)
        ).apply(instance, (mode, input, count, output) ->
                new MetalFormingRecipe(MetalFormerRecipes.Mode.valueOf(mode.toUpperCase(java.util.Locale.ROOT)), input, count, output)));

        private static final StreamCodec<RegistryFriendlyByteBuf, MetalFormingRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, r -> r.mode.name(),
                Ingredient.CONTENTS_STREAM_CODEC, r -> r.input,
                ByteBufCodecs.VAR_INT, r -> r.inputCount,
                ItemStack.STREAM_CODEC, r -> r.output,
                (mode, input, count, output) ->
                        new MetalFormingRecipe(MetalFormerRecipes.Mode.valueOf(mode), input, count, output));

        @Override
        public MapCodec<MetalFormingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, MetalFormingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
