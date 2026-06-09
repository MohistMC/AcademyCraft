package com.mohistmc.academy.client.gui;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.skill.AcademyAttachments;
import com.mohistmc.academy.skill.PlayerAbilityData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

/**
 * CP 和 Overload 栏 HUD — 单色风格（CP: 白→黄→红, OL: 蓝→橙→红）。
 * 不依赖 Shader，只用原版 GuiGraphics。
 *
 * @author Mgazul
 */
@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = AcademyCraft.MODID, value = Dist.CLIENT)
public class CPBarOverlay {

    private static final int TEX_WIDTH = 964;
    private static final int TEX_HEIGHT = 147;
    private static final int TEX_OL_Y = 21;   // OL 区域的起始像素
    private static final int TEX_OL_H = 104;  // OL 区域的高度
    private static final int TEX_CP_Y = 30;   // CP 区域的起始像素
    private static final int TEX_CP_H = 84;   // CP 区域的高度
    private static final int TEX_CP_X = 47;   // CP 区域左边距
    private static final int TEX_CP_W = 883;  // CP 区域宽度

    private static final ResourceLocation TEX_CP_BG =
            ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/guis/cpbar/back_normal.png");
    private static final ResourceLocation TEX_CP_FG =
            ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/guis/cpbar/cp.png");
    private static final ResourceLocation TEX_OL_BG =
            ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/guis/cpbar/back_overload.png");

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiLayerEvent.Post event) {
        if (!event.getName().equals(VanillaGuiLayers.HOTBAR)) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null || mc.options.hideGui) return;

        PlayerAbilityData data = mc.player.getData(AcademyAttachments.PLAYER_ABILITY);
        if (!data.hasAbility()) return;
        if (!data.isAbilityActive()) return;

        GuiGraphics g = event.getGuiGraphics();
        int screenW = mc.getWindow().getGuiScaledWidth();

        int hudWidth = screenW / 2 - 40;
        int hudX = screenW - hudWidth - 30;
        int hudY = 15;
        float scale = (float) hudWidth / TEX_WIDTH;

        // ==== Draw BG ====
        g.pose().pushPose();
        g.pose().translate(hudX, hudY, 0);
        g.pose().scale(scale, scale, 1.0f);

        // Render overload-bg if overloaded, otherwise normal
        if (data.getCurrentOverload() >= data.getMaxOverload() && data.getMaxOverload() > 0) {
            g.blit(TEX_OL_BG, 0, 0, 0, 0, TEX_WIDTH, TEX_HEIGHT, TEX_WIDTH, TEX_HEIGHT);
        } else {
            g.blit(TEX_CP_BG, 0, 0, 0, 0, TEX_WIDTH, TEX_HEIGHT, TEX_WIDTH, TEX_HEIGHT);

            // Overload progress (blue/turquoise gradient area in the top band)
            float olRatio = data.getMaxOverload() > 0
                    ? Math.min(1.0f, data.getCurrentOverload() / data.getMaxOverload()) : 0;
            if (olRatio > 0) {
                int olFillW = (int) (TEX_CP_W * olRatio);
                // Alpha overlay: blend blue transparency
                g.fill(TEX_CP_X, TEX_OL_Y,
                        TEX_CP_X + olFillW, TEX_OL_Y + TEX_OL_H,
                        (int)(olRatio * 120) << 24 | 0x00BFFF);
            }

            // CP bar (white gradient, right-aligned)
            float cpRatio = data.getMaxCp() > 0
                    ? Math.min(1.0f, data.getCurrentCp() / data.getMaxCp()) : 0;
            if (cpRatio > 0) {
                int cpFillW = (int) (TEX_CP_W * cpRatio);
                int cpRightX = TEX_CP_X + TEX_CP_W;
                g.blit(TEX_CP_FG,
                        cpRightX - cpFillW, TEX_CP_Y,
                        cpRightX - cpFillW, TEX_CP_Y,
                        cpFillW, TEX_CP_H,
                        TEX_WIDTH, TEX_HEIGHT);
            }
        }

        g.pose().popPose();

        // Draw CP/OL text numbers (outside the scaled pose)
        String cpText = String.format("CP %.0f/%.0f", data.getCurrentCp(), data.getMaxCp());
        String olText = String.format("OL %.0f/%.0f", data.getCurrentOverload(), data.getMaxOverload());
        int textX = hudX + 8;
        g.drawString(mc.font, cpText, textX, hudY + 16, 0xFFFFFFFF, false);
        g.drawString(mc.font, olText, textX, hudY + 28, 0xFFAAAAAA, false);
    }
}
