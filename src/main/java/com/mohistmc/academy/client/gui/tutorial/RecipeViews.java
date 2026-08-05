package com.mohistmc.academy.client.gui.tutorial;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.client.gui.GuiRenderHelper;
import com.mohistmc.academy.crafting.AcademyRecipeTypes;
import com.mohistmc.academy.crafting.ImagFusorRecipes;
import com.mohistmc.academy.crafting.MetalFormingRecipe;
import com.mohistmc.academy.tutorial.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 教程预览视图 —— 合成配方 / 机器配方 / 物品展示。
 */
@OnlyIn(Dist.CLIENT)
public final class RecipeViews {

    public interface PreviewView {
        void render(GuiGraphics g, float areaW, float areaH, double mx, double my, boolean hovering,
                   float screenOffX, float screenOffY, float fscale);
    }

    private RecipeViews() {}

    /** 最近一次悬停的物品与屏幕坐标(由 TutorialAppGui 在 render 末尾渲染原版 tooltip) */
    public static ItemStack lastHoverStack = ItemStack.EMPTY;
    public static int lastHoverX = 0, lastHoverY = 0;

    private static final ResourceLocation TEX_GRID = tex("guis/tutorial/crafting_grid");
    private static final ResourceLocation TEX_FUSOR = tex("guis/tutorial_fusor");
    private static final ResourceLocation TEX_FORMER = tex("guis/tutorial_metalformer");
    private static final ResourceLocation TEX_SMELT = tex("guis/tutorial_smelting");
    private static final ResourceLocation PROG_FUSOR = tex("guis/progress/progress_fusor");
    private static final ResourceLocation PROG_FORMER = tex("guis/progress/progress_metalformer");

    private static final double ALTERNATE_TIME = 2.0;
    private static final float ITEM_PX = 32f;

    private static ResourceLocation tex(String path) {
        return ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/" + path + ".png");
    }

    public static List<PreviewView> buildFor(ViewGroup g) {
        ItemStack recipeTarget = g.recipeTarget();
        if (!recipeTarget.isEmpty()) return buildRecipes(recipeTarget);
        ItemStack stack = g.previewStack();
        if (!stack.isEmpty()) return List.of(new ItemView(stack));
        ResourceLocation icon = g.previewIcon();
        if (icon != null) return List.of(new IconView(icon));
        return List.of();
    }

    private static List<PreviewView> buildRecipes(ItemStack target) {
        List<PreviewView> out = new ArrayList<>();
        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        if (level == null) return out;
        RegistryAccess ra = level.registryAccess();
        RecipeManager rm = level.getRecipeManager();

        for (RecipeHolder<CraftingRecipe> holder : rm.getAllRecipesFor(RecipeType.CRAFTING)) {
            CraftingRecipe r = holder.value();
            ItemStack result = r.getResultItem(ra);
            if (!sameItem(result, target)) continue;
            if (r instanceof ShapedRecipe sr) {
                out.add(new CraftingView(remapShaped(sr), result, "shaped"));
            } else {
                out.add(new CraftingView(rowMajor(r.getIngredients()), result, "shapeless"));
            }
        }

        for (ImagFusorRecipes.IFRecipe r : ImagFusorRecipes.INSTANCE.getAllRecipes()) {
            if (sameItem(r.output(), target)) {
                out.add(MachineView.fusor(new ItemStack[]{r.input()}, r.output(), r.phaseLiquid()));
            }
        }

        for (RecipeHolder<MetalFormingRecipe> holder : rm.getAllRecipesFor(AcademyRecipeTypes.METAL_FORMING.get())) {
            MetalFormingRecipe r = holder.value();
            ItemStack o = r.getOutput();
            if (o.isEmpty() || !sameItem(o, target)) continue;
            ItemStack[] ins = r.getIngredients().stream()
                    .filter(ing -> !ing.isEmpty())
                    .map(ing -> ing.getItems().length == 0 ? ItemStack.EMPTY : ing.getItems()[0])
                    .toArray(ItemStack[]::new);
            out.add(MachineView.former(ins, o, modeTex(r.getMode())));
        }

        for (RecipeHolder<SmeltingRecipe> holder : rm.getAllRecipesFor(RecipeType.SMELTING)) {
            SmeltingRecipe r = holder.value();
            ItemStack result = r.getResultItem(ra);
            if (!sameItem(result, target)) continue;
            ItemStack[] ins = r.getIngredients().isEmpty()
                    ? new ItemStack[0] : r.getIngredients().get(0).getItems();
            out.add(MachineView.smelt(ins, result));
        }
        return out;
    }

    private static ResourceLocation modeTex(com.mohistmc.academy.crafting.MetalFormerRecipes.Mode mode) {
        return ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID,
                "textures/guis/icons/icon_former_" + mode.name().toLowerCase(java.util.Locale.ROOT) + ".png");
    }

    private static ItemStack[][] remapShaped(ShapedRecipe sr) {
        int w = sr.getWidth();
        List<Ingredient> ing = sr.getIngredients();
        ItemStack[][] grid = new ItemStack[9][];
        for (int i = 0; i < ing.size(); i++) {
            int row = i / w, col = i % w;
            if (col < 3 && row < 3) grid[col + row * 3] = candidates(ing.get(i));
        }
        return grid;
    }

    private static ItemStack[][] rowMajor(List<Ingredient> ing) {
        ItemStack[][] grid = new ItemStack[9][];
        for (int i = 0; i < ing.size() && i < 9; i++) grid[i] = candidates(ing.get(i));
        return grid;
    }

    private static ItemStack[] candidates(Ingredient ing) {
        if (ing == null || ing.isEmpty()) return null;
        ItemStack[] items = ing.getItems();
        return items.length == 0 ? null : items;
    }

    private static boolean sameItem(ItemStack a, ItemStack b) {
        return !a.isEmpty() && !b.isEmpty() && ItemStack.isSameItem(a, b);
    }

    private static ItemStack pick(ItemStack[] cands) {
        if (cands == null || cands.length == 0) return ItemStack.EMPTY;
        int i = (int) ((System.currentTimeMillis() / 1000.0 / ALTERNATE_TIME)) % cands.length;
        return cands[i];
    }

    /** 在缩放坐标系中渲染物品图标(16px 图标,以 sizePx 为目标尺寸) */
    private static void renderStack(GuiGraphics g, ItemStack stack, double cx, double cy, double sizePx) {
        if (stack == null || stack.isEmpty()) return;
        double s = sizePx / 16.0;
        g.pose().pushPose();
        g.pose().translate(cx - 8 * s, cy - 8 * s, 0);
        g.pose().scale((float) s, (float) s, 1);
        g.renderItem(stack, 0, 0);
        g.pose().popPose();
    }

    /** 记录悬停物品与屏幕坐标(原版 tooltip 由 TutorialAppGui 在无变换 pose 渲染) */
    private static void drawTooltip(GuiGraphics g, ItemStack stack, double mx, double my) {
        lastHoverStack = stack;
        lastHoverX = (int) mx;
        lastHoverY = (int) my;
    }

    static final class ItemView implements PreviewView {
        private final ItemStack stack;

        ItemView(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public void render(GuiGraphics g, float areaW, float areaH, double mx, double my, boolean hovering,
                           float screenOffX, float screenOffY, float fscale) {
            float s = areaH * 0.55f;
            g.pose().pushPose();
            g.pose().translate(areaW / 2f, areaH / 2f, 0);
            g.pose().scale(s / 16f, s / 16f, 1);
            g.renderItem(stack, -8, -8);
            g.pose().popPose();
        }
    }

    static final class IconView implements PreviewView {
        private final ResourceLocation icon;

        IconView(ResourceLocation icon) {
            this.icon = icon;
        }

        @Override
        public void render(GuiGraphics g, float areaW, float areaH, double mx, double my, boolean hovering,
                           float screenOffX, float screenOffY, float fscale) {
            float sz = areaH * 0.6f;
            GuiRenderHelper.blitTranslucent(g, icon, (int) (areaW / 2f - sz / 2), (int) (areaH / 2f - sz / 2), (int) sz, (int) sz);
        }
    }

    static final class CraftingView implements PreviewView {
        static final float CW = 196, CH = 128, SCALE = 0.6f, STEP = 43;
        private final ItemStack[][] grid;
        private final ItemStack output;
        private final String desc;

        CraftingView(ItemStack[][] grid, ItemStack output, String desc) {
            this.grid = grid;
            this.output = output;
            this.desc = desc;
        }

        @Override
        public void render(GuiGraphics g, float areaW, float areaH, double mx, double my, boolean hovering,
                           float screenOffX, float screenOffY, float fscale) {
            com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();
            float ox = (areaW - CW * SCALE) / 2f, oy = (areaH - CH * SCALE) / 2f;
            g.pose().pushPose();
            g.pose().translate(ox, oy, 0);
            g.pose().scale(SCALE, SCALE, 1);

            GuiRenderHelper.blitTranslucent(g, TEX_GRID, 0, 0, (int) CW, (int) CH);

            ItemStack hoverStack = ItemStack.EMPTY;
            for (int i = 0; i < 9; i++) {
                ItemStack s = pick(grid[i]);
                double cx = 5 + (i % 3) * STEP + 16, cy = 5 + (i / 3) * STEP + 16;
                renderStack(g, s, cx, cy, ITEM_PX);
                if (hoverStack.isEmpty() && hovering && isOver(mx, my, ox, oy, SCALE, cx, cy, 16) && !s.isEmpty()) {
                    hoverStack = s;
                }
            }
            renderStack(g, output, 169, 65, ITEM_PX);
            if (hoverStack.isEmpty() && hovering && isOver(mx, my, ox, oy, SCALE, 169, 65, 16)) {
                hoverStack = output;
            }

            Minecraft mc = Minecraft.getInstance();
            String typeText = "ac.gui.crafttype." + desc;
            String localized = net.minecraft.network.chat.Component.translatable(typeText).getString();
            if (localized.equals(typeText)) localized = desc;
            int tw = mc.font.width(localized);
            mc.font.drawInBatch(localized, (68 - tw / 2f), -28, 0xFFFFFFFF, false,
                    g.pose().last().pose(), g.bufferSource(), net.minecraft.client.gui.Font.DisplayMode.NORMAL, 0, 0);
            g.flush();

            g.pose().popPose();
            drawTooltip(g, hoverStack, mx * fscale + screenOffX, my * fscale + screenOffY);
        }
    }

    static final class MachineView implements PreviewView {
        private final ResourceLocation bg;
        private final float cw, ch, scale;
        private final ItemStack[] in;
        private final ItemStack out;
        private final float inCx, inCy, outCx, outCy;
        private final ResourceLocation modeTex;
        private final float modeX, modeY, modeSz;
        private final int amount;
        private final float amountCx, amountCy;
        private final ResourceLocation progTex;
        private final float progX, progY, progW, progH;

        private MachineView(ResourceLocation bg, float cw, float ch, float scale,
                            ItemStack[] in, ItemStack out, float inCx, float inCy, float outCx, float outCy,
                            ResourceLocation modeTex, float modeX, float modeY, float modeSz,
                            int amount, float amountCx, float amountCy,
                            ResourceLocation progTex, float progX, float progY, float progW, float progH) {
            this.bg = bg;
            this.cw = cw;
            this.ch = ch;
            this.scale = scale;
            this.in = in;
            this.out = out;
            this.inCx = inCx;
            this.inCy = inCy;
            this.outCx = outCx;
            this.outCy = outCy;
            this.modeTex = modeTex;
            this.modeX = modeX;
            this.modeY = modeY;
            this.modeSz = modeSz;
            this.amount = amount;
            this.amountCx = amountCx;
            this.amountCy = amountCy;
            this.progTex = progTex;
            this.progX = progX;
            this.progY = progY;
            this.progW = progW;
            this.progH = progH;
        }

        static MachineView former(ItemStack[] in, ItemStack out, ResourceLocation modeTex) {
            return new MachineView(TEX_FORMER, 192, 192, 0.5f,
                    in, out, 11.33f + 12.5f, 88.5f + 12.5f, 155.33f + 12.5f, 88.5f + 12.5f,
                    modeTex, 82.67f, 22.7f, 25f,
                    -1, 0, 0,
                    PROG_FORMER, 77.67f, 83.5f, 36.67f, 23.33f);
        }

        static MachineView fusor(ItemStack[] in, ItemStack out, int amount) {
            return new MachineView(TEX_FUSOR, 196, 128, 0.6f,
                    in, out, 19f + 16f, 62.5f + 15.5f, 147f + 16f, 62.5f + 15.5f,
                    null, 0, 0, 0,
                    amount, 81f + 26f, 14.5f + 7.5f,
                    PROG_FUSOR, 66f, 68.5f, 66f, 18f);
        }

        static MachineView smelt(ItemStack[] in, ItemStack out) {
            return new MachineView(TEX_SMELT, 192, 128, 0.6f,
                    in, out, 30f + 16f, 43.17f + 16f, 123.33f + 16f, 43.17f + 16f,
                    null, 0, 0, 0,
                    -1, 0, 0,
                    null, 0, 0, 0, 0);
        }

        @Override
        public void render(GuiGraphics g, float areaW, float areaH, double mx, double my, boolean hovering,
                           float screenOffX, float screenOffY, float fscale) {
            com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();
            float ox = (areaW - cw * scale) / 2f, oy = (areaH - ch * scale) / 2f;
            g.pose().pushPose();
            g.pose().translate(ox, oy, 0);
            g.pose().scale(scale, scale, 1);

            GuiRenderHelper.blitTranslucent(g, bg, 0, 0, (int) cw, (int) ch);

            if (progTex != null) {
                double disp = (System.currentTimeMillis() / 1000.0 % 2.0) / 2.0;
                GuiRenderHelper.blitTranslucent(g, progTex, (int) progX, (int) progY,
                        (int) (progW * disp), (int) progH, 0, 0, (float) disp, 1);
            }
            if (modeTex != null) {
                GuiRenderHelper.blitTranslucent(g, modeTex, (int) modeX, (int) modeY, (int) modeSz, (int) modeSz);
            }
            if (amount >= 0) {
                Minecraft mc = Minecraft.getInstance();
                String s = String.valueOf(amount);
                int tw = mc.font.width(s);
                mc.font.drawInBatch(s, amountCx - tw / 2f, amountCy - 7, 0xFFFFFFFF, false,
                        g.pose().last().pose(), g.bufferSource(), net.minecraft.client.gui.Font.DisplayMode.NORMAL, 0, 0);
                g.flush();
            }

            ItemStack curIn = pick(in);
            renderStack(g, curIn, inCx, inCy, ITEM_PX);
            renderStack(g, out, outCx, outCy, ITEM_PX);

            ItemStack hoverStack = ItemStack.EMPTY;
            if (hovering) {
                if (isOver(mx, my, ox, oy, scale, inCx, inCy, 16) && !curIn.isEmpty()) {
                    hoverStack = curIn;
                } else if (isOver(mx, my, ox, oy, scale, outCx, outCy, 16)) {
                    hoverStack = out;
                }
            }
            g.pose().popPose();
            drawTooltip(g, hoverStack, mx * fscale + screenOffX, my * fscale + screenOffY);
        }
    }

    private static boolean isOver(double mx, double my, float ox, float oy, float scale,
                                  double slotCx, double slotCy, double half) {
        double lx = (mx - ox) / scale, ly = (my - oy) / scale;
        return lx >= slotCx - half && lx <= slotCx + half && ly >= slotCy - half && ly <= slotCy + half;
    }
}
