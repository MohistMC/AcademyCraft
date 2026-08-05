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
 * CP 和 Overload 栏 HUD —— 坐标与渲染方式对齐 Return CPBar。
 */
@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = AcademyCraft.MODID, value = Dist.CLIENT)
public class CPBarOverlay {

    private static final float WIDTH = 964;
    private static final float HEIGHT = 147;
    private static final float SCALE = 0.2f;
    private static final int MARGIN_RIGHT = 12;
    private static final int MARGIN_TOP = 12;

    // OL 区域(顶部条)
    private static final float OL_X0 = 0, OL_Y0 = 21, OL_W = 943, OL_H = 104;
    // CP 区域
    private static final float CP_X0 = 47, CP_Y0 = 30, CP_W = 883, CP_H = 84;

    private static final ResourceLocation TEX_BACK_NORMAL =
            ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/guis/cpbar/back_normal.png");
    private static final ResourceLocation TEX_BACK_OVERLOAD =
            ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/guis/cpbar/back_overload.png");
    private static final ResourceLocation TEX_CP =
            ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/guis/cpbar/cp.png");
    private static final ResourceLocation TEX_FRONT_OVERLOAD =
            ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/guis/cpbar/front_overload.png");
    private static final ResourceLocation TEX_OVERLOAD_HIGHLIGHT =
            ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/guis/cpbar/highlight_overload.png");

    /** CP 条颜色渐变：红 → 橙 → 白 */
    private static final float[][] CP_COLORS = {
            {0.0f, 0xf0 / 255f, 0x67 / 255f, 0x67 / 255f},
            {0.35f, 0xff / 255f, 0xae / 255f, 0x44 / 255f},
            {1.0f, 1.0f, 1.0f, 1.0f},
    };
    /** OL 条颜色渐变：青 → 橙 → 红 */
    private static final float[][] OL_COLORS = {
            {0.0f, 0xdf / 255f, 0xdf / 255f, 0xdf / 255f},
            {0.55f, 0xf0 / 255f, 0xd4 / 255f, 0x9d / 255f},
            {1.0f, 0xf5 / 255f, 0x64 / 255f, 0x64 / 255f},
    };

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

        float hudW = WIDTH * SCALE;
        float hudX = screenW - hudW - MARGIN_RIGHT;
        float hudY = MARGIN_TOP;

        g.pose().pushPose();
        g.pose().translate(hudX, hudY, 0);
        g.pose().scale(SCALE, SCALE, 1.0f);

        boolean overloaded = data.getMaxOverload() > 0
                && data.getCurrentOverload() >= data.getMaxOverload();
        if (overloaded) {
            drawOverload(g);
        } else {
            drawNormal(g, data);
            drawCPBar(g, data);
        }

        g.pose().popPose();
    }

    /** 正常模式：背景 + OL 条（背景 alpha 0.8，与 Return setColor(1,1,1,.8) 一致） */
    private static void drawNormal(GuiGraphics g, PlayerAbilityData data) {
        com.mojang.blaze3d.systems.RenderSystem.enableBlend();
        com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();
        g.setColor(1, 1, 1, 0.8f);
        g.blit(TEX_BACK_NORMAL, 0, 0, 0, 0, (int) WIDTH, (int) HEIGHT, (int) WIDTH, (int) HEIGHT);
        g.setColor(1, 1, 1, 1);

        float olRatio = data.getMaxOverload() > 0
                ? Math.min(1.0f, data.getCurrentOverload() / data.getMaxOverload()) : 0;
        if (olRatio > 0) {
            float[] col = autoLerp(OL_COLORS, olRatio);
            float len = OL_W * olRatio;
            float x = OL_X0 + OL_W - len;
            g.fill((int) x, (int) OL_Y0, (int) (x + len), (int) (OL_Y0 + OL_H),
                    colorOf(col[1], col[2], col[3], 0.8f));
        }
        com.mojang.blaze3d.systems.RenderSystem.disableBlend();
    }

    /** CP 条：右对齐填充，0.16~0.96 比例 */
    private static void drawCPBar(GuiGraphics g, PlayerAbilityData data) {
        float cpRatio = data.getMaxCp() > 0
                ? Math.min(1.0f, data.getCurrentCp() / data.getMaxCp()) : 0;
        if (cpRatio <= 0) return;

        float[] col = autoLerp(CP_COLORS, cpRatio);
        float prog = 0.16f + cpRatio * 0.8f;
        float len = CP_W * prog;
        float x = CP_X0 + (CP_W - len);

        // 用 cp.png 纹理按比例右对齐绘制,叠加渐变着色
        com.mojang.blaze3d.systems.RenderSystem.enableBlend();
        com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();
        g.setColor(col[1], col[2], col[3], 1.0f);
        g.blit(TEX_CP, (int) x, (int) CP_Y0, (int) x, (int) CP_Y0,
                (int) len, (int) CP_H, (int) WIDTH, (int) HEIGHT);
        g.setColor(1, 1, 1, 1);

        // 职业图标：CP 条右侧正方形
        var ability = Minecraft.getInstance().player.getData(
                com.mohistmc.academy.skill.AcademyAttachments.PLAYER_ABILITY).getCurrentAbility();
        if (ability != null) {
            float iconSize = 64;
            float iconX = CP_X0 + CP_W - iconSize;
            float iconY = CP_Y0 + (CP_H - iconSize) / 2;
            g.setColor(1, 1, 1, 0.8f);
            g.blit(ability.getOverlayIcon(), (int) iconX, (int) iconY,
                    0, 0, (int) iconSize, (int) iconSize, 64, 64);
            g.setColor(1, 1, 1, 1);
        }
        com.mojang.blaze3d.systems.RenderSystem.disableBlend();
    }

    /** 过载模式：背景 + 前景 + 高亮呼吸 */
    private static void drawOverload(GuiGraphics g) {
        com.mojang.blaze3d.systems.RenderSystem.enableBlend();
        com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();
        g.setColor(1, 1, 1, 0.8f);
        g.blit(TEX_BACK_OVERLOAD, 0, 0, 0, 0, (int) WIDTH, (int) HEIGHT, (int) WIDTH, (int) HEIGHT);
        g.setColor(1, 1, 1, 1);
        g.blit(TEX_FRONT_OVERLOAD, 0, 0, 0, 0, (int) WIDTH, (int) HEIGHT, (int) WIDTH, (int) HEIGHT);
        float breathe = 0.3f + 0.35f * (float) (Math.sin(System.currentTimeMillis() / 200.0) + 1);
        g.setColor(1, 1, 1, breathe);
        g.blit(TEX_OVERLOAD_HIGHLIGHT, 0, 0, 0, 0, (int) WIDTH, (int) HEIGHT, (int) WIDTH, (int) HEIGHT);
        g.setColor(1, 1, 1, 1);
        com.mojang.blaze3d.systems.RenderSystem.disableBlend();
    }

    /** 渐变颜色插值 */
    private static float[] autoLerp(float[][] list, double prog) {
        for (int i = 0; i < list.length; i++) {
            if (list[i][0] >= prog) {
                if (i == 0) return list[0];
                float[] last = list[i - 1];
                float f = (float) ((prog - last[0]) / (list[i][0] - last[0]));
                return new float[]{
                        0, lerp(last[1], list[i][1], f), lerp(last[2], list[i][2], f), lerp(last[3], list[i][3], f)};
            }
        }
        return list[list.length - 1];
    }

    private static float lerp(float a, float b, float f) {
        return a + (b - a) * f;
    }

    private static int colorOf(float r, float g, float b, float a) {
        return ((int) (a * 255) << 24) | ((int) (r * 255) << 16) | ((int) (g * 255) << 8) | (int) (b * 255);
    }
}
