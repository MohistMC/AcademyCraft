package com.mohistmc.academy.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

/**
 * GUI 半透明纹理绘制 —— 基于已验证正常的 g.blit + 内部自包含 enableBlend。
 */
public final class GuiRenderHelper {

    private GuiRenderHelper() {}

    /** 绘制带 alpha 混合的纹理(全图采样,平铺到 x,y,w,h) */
    public static void blitTranslucent(GuiGraphics g, ResourceLocation tex, int x, int y, int w, int h) {
        blitTranslucent(g, tex, x, y, w, h, 0, 0, 1, 1);
    }

    /** 绘制带 alpha 混合的纹理(指定 UV 区域采样) */
    public static void blitTranslucent(GuiGraphics g, ResourceLocation tex, int x, int y, int w, int h,
                                       float u0, float v0, float u1, float v1) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        int texW = (int) ((u1 - u0) > 0.0001f ? w / (u1 - u0) : w);
        int texH = (int) ((v1 - v0) > 0.0001f ? h / (v1 - v0) : h);
        int uOff = (int) (u0 * texW);
        int vOff = (int) (v0 * texH);
        g.blit(tex, x, y, uOff, vOff, w, h, texW, texH);
        RenderSystem.disableBlend();
    }
}
