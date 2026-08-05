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
 * 屏幕遮罩特效 —— 能力激活时屏幕染上淡蓝色，超载时染红色，平滑渐变过渡。
 */
@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = AcademyCraft.MODID, value = Dist.CLIENT)
public class ScreenMaskOverlay {

    private static final ResourceLocation TEX_MASK =
            ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/effects/screen_mask.png");

    private static final float CHANGE_PER_SEC = 1.0f;

    private static final float[] COLOR_ACTIVE = {0.0f, 0.75f, 1.0f};
    private static final float ACTIVE_ALPHA = 0.24f;

    private static final float[] COLOR_OVERLOAD = {208 / 255f, 20 / 255f, 20 / 255f};
    private static final float OVERLOAD_ALPHA = 0.67f;

    private static double r, g, b, a;
    private static long lastFrame;

    @SubscribeEvent
    public static void onRender(RenderGuiLayerEvent.Post event) {
        if (!event.getName().equals(VanillaGuiLayers.HOTBAR)) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null || mc.options.hideGui) return;

        PlayerAbilityData data = mc.player.getData(AcademyAttachments.PLAYER_ABILITY);
        if (!data.hasAbility()) return;

        // 目标颜色与透明度
        float[] target;
        float targetA;
        if (data.getCurrentOverload() >= data.getMaxOverload() && data.getMaxOverload() > 0) {
            target = COLOR_OVERLOAD;
            targetA = OVERLOAD_ALPHA;
        } else if (data.isAbilityActive()) {
            target = COLOR_ACTIVE;
            targetA = ACTIVE_ALPHA;
        } else {
            target = null;
            targetA = 0;
        }

        long now = System.currentTimeMillis();
        long dt = lastFrame == 0 ? 0 : now - lastFrame;
        lastFrame = now;

        if (target == null) {
            if (a != 0) {
                // 逐渐淡出
                r = balanceTo(r, 0, dt);
                g = balanceTo(g, 0, dt);
                b = balanceTo(b, 0, dt);
                a = balanceTo(a, 0, dt);
            }
        } else {
            r = balanceTo(r, target[0], dt);
            g = balanceTo(g, target[1], dt);
            b = balanceTo(b, target[2], dt);
            a = balanceTo(a, targetA, dt);
        }

        if (a <= 0.005) {
            r = g = b = 0;
            return;
        }

        GuiGraphics gg = event.getGuiGraphics();
        int w = mc.getWindow().getGuiScaledWidth();
        int h = mc.getWindow().getGuiScaledHeight();
        // 与 Return BackgroundMask 一致：setShaderColor 调制 + 开启混合保留纹理 alpha
        com.mojang.blaze3d.systems.RenderSystem.enableBlend();
        com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();
        com.mojang.blaze3d.systems.RenderSystem.setShaderColor((float) r, (float) g, (float) b, (float) a);
        gg.blit(TEX_MASK, 0, 0, 0, 0, w, h, 512, 288);
        com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1, 1, 1, 1);
        com.mojang.blaze3d.systems.RenderSystem.disableBlend();
    }

    private static double balanceTo(double from, double to, long dt) {
        double delta = to - from;
        delta = Math.signum(delta) * Math.min(Math.abs(delta), dt / 1000.0 * CHANGE_PER_SEC);
        return from + delta;
    }
}
