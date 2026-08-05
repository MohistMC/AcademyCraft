package com.mohistmc.academy.tutorial;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.tutorial.ACTutorial.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

/**
 * 预览视图工厂。
 */
public final class ViewGroups {

    private ViewGroups() {}

    public static ViewGroup drawsBlock(Block block) {
        return itemView(new ItemStack(block), Tag.VIEW);
    }

    public static ViewGroup drawsItem(ItemLike item) {
        return itemView(new ItemStack(item), Tag.VIEW);
    }

    public static ViewGroup recipes(ItemLike item) {
        final ItemStack target = new ItemStack(item);
        return new ViewGroup() {
            @Override
            public Tag getTag() {
                return Tag.CRAFT;
            }

            @Override
            public ItemStack recipeTarget() {
                return target;
            }

            @Override
            public String getDisplayText() {
                return Component.translatable("ac.tutorial.crafting", target.getHoverName()).getString();
            }
        };
    }

    public static ViewGroup displayIcon(String tex) {
        final ResourceLocation res = ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/" + tex + ".png");
        return new ViewGroup() {
            @Override
            public Tag getTag() {
                return Tag.VIEW;
            }

            @Override
            public ResourceLocation previewIcon() {
                return res;
            }
        };
    }

    private static ViewGroup itemView(ItemStack stack, Tag tag) {
        return new ViewGroup() {
            @Override
            public Tag getTag() {
                return tag;
            }

            @Override
            public ItemStack previewStack() {
                return stack;
            }

            @Override
            public String getDisplayText() {
                return stack.getHoverName().getString();
            }
        };
    }
}
