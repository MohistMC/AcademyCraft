package com.mohistmc.academy.client;

import com.mohistmc.academy.AcademyCraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = AcademyCraft.MODID, value = Dist.CLIENT)
public class TerminalInstallProgress {
    private static boolean installing = false;
    private static long startTime = 0;
    private static final int DURATION_MS = 3000;

    public static void start() {
        installing = true;
        startTime = System.currentTimeMillis();
    }

    public static boolean isInstalling() {
        return installing;
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        if (!installing) return;

        long elapsed = System.currentTimeMillis() - startTime;
        float progress = Math.min(elapsed / (float) DURATION_MS, 1.0f);

        if (progress >= 1.0f) {
            installing = false;
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null) return;

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        GuiGraphics graphics = event.getGuiGraphics();

        int barWidth = 200;
        int barHeight = 20;
        int x = (screenWidth - barWidth) / 2;
        int y = screenHeight / 2 - barHeight / 2;

        graphics.fill(x, y, x + barWidth, y + barHeight, 0xFF1a1a2e);
        graphics.fill(x, y, x + (int)(barWidth * progress), y + barHeight, 0xFF00bcd4);

        String text = String.format("安装中... %.0f%%", progress * 100);
        int textWidth = mc.font.width(text);
        graphics.drawString(mc.font, text, (screenWidth - textWidth) / 2, y - 15, 0xFF00e5ff);
    }
}
