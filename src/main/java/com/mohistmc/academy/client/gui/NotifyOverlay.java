package com.mohistmc.academy.client.gui;

import com.mohistmc.academy.AcademyCraft;
import java.util.ArrayList;
import java.util.List;
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
 * 通知浮层 — 短暂显示系统消息（教程更新、能力解锁等）。
 */
@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = AcademyCraft.MODID, value = Dist.CLIENT)
public class NotifyOverlay {

    private static final int MAX_NOTIFICATIONS = 3;
    private static final long DURATION_MS = 5000;
    private static final long FADE_MS = 500;

    private static final List<Notification> notifications = new ArrayList<>();

    private static final ResourceLocation TEX_BG =
            ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/guis/notification/back.png");

    /**
     * 发送一条通知（线程安全，可从任何地方调用）。
     */
    public static void notify(String title, String content) {
        synchronized (notifications) {
            notifications.add(new Notification(title, content, System.currentTimeMillis()));
            while (notifications.size() > MAX_NOTIFICATIONS) {
                notifications.remove(0);
            }
        }
    }

    /** 发送一条通知，带图标提示。 */
    public static void notify(String title, String content, ResourceLocation icon) {
        synchronized (notifications) {
            notifications.add(new Notification(title, content, icon, System.currentTimeMillis()));
            while (notifications.size() > MAX_NOTIFICATIONS) {
                notifications.remove(0);
            }
        }
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiLayerEvent.Post event) {
        if (!event.getName().equals(VanillaGuiLayers.HOTBAR)) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) return;

        GuiGraphics g = event.getGuiGraphics();
        int screenW = mc.getWindow().getGuiScaledWidth();
        long now = System.currentTimeMillis();

        synchronized (notifications) {
            notifications.removeIf(n -> now - n.time > DURATION_MS + FADE_MS);

            int y = 50;
            for (int i = notifications.size() - 1; i >= 0; i--) {
                Notification n = notifications.get(i);
                long elapsed = now - n.time;
                float alpha = 1.0f;
                if (elapsed < FADE_MS) {
                    alpha = (float) elapsed / FADE_MS;
                } else if (elapsed > DURATION_MS) {
                    alpha = 1.0f - (float)(elapsed - DURATION_MS) / FADE_MS;
                }
                if (alpha <= 0) continue;

                int notifW = 220;
                int notifH = 50;
                int x = screenW - notifW - 20;

                int bgColor = (int)(alpha * 200) << 24 | 0x0A1A30;
                g.fill(x, y, x + notifW, y + notifH, bgColor);
                int borderColor = (int)(alpha * 180) << 24 | 0x00BFFF;
                g.renderOutline(x, y, notifW, notifH, borderColor);

                int textX = x + 8;
                if (n.icon != null) {
                    g.blit(n.icon, x + 4, y + 4, 0, 0, 16, 16, 16, 16);
                    textX = x + 26;
                }

                int titleColor = (int)(alpha * 255) << 24 | 0xFFFFFF;
                g.drawString(mc.font, n.title, textX, y + 6, titleColor, false);
                int contentColor = (int)(alpha * 200) << 24 | 0xCCDDEE;
                g.drawString(mc.font, n.content, textX, y + 24, contentColor, false);

                y += notifH + 4;
            }
        }
    }

    private static class Notification {
        final String title;
        final String content;
        final ResourceLocation icon;
        final long time;

        Notification(String title, String content, long time) {
            this(title, content, null, time);
        }

        Notification(String title, String content, ResourceLocation icon, long time) {
            this.title = title;
            this.content = content;
            this.icon = icon;
            this.time = time;
        }
    }
}
