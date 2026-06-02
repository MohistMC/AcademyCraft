package com.mohistmc.academy.client.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/**
 * @author Mgazul
 * @date 2026/6/2 17:02
 */
public final class GuiUtils {
    private GuiUtils() {}

    public static void drawBorder(GuiGraphics graphics, int x, int y, int w, int h, int color) {
        graphics.fill(x, y, x + w, y + 1, color);
        graphics.fill(x, y + h - 1, x + w, y + h, color);
        graphics.fill(x, y, x + 1, y + h, color);
        graphics.fill(x + w - 1, y, x + w, y + h, color);
    }

    public static boolean isHovered(int x, int y, int w, int h, int mouseX, int mouseY) {
        return mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
    }

    public static List<String> wrapText(String text, int maxWidth, Font font) {
        List<String> result = new ArrayList<>();
        if (text == null || text.isEmpty() || maxWidth <= 0) {
            return result;
        }

        int start = 0;
        while (start < text.length()) {
            int end = start + 1;
            while (end <= text.length()) {
                if (font.width(text.substring(start, end)) > maxWidth - 4) {
                    break;
                }
                end++;
            }
            end--;
            if (end <= start) {
                end = start + 1;
            }
            result.add(text.substring(start, end));
            start = end;
        }
        return result;
    }

    public static void drawScrollBar(GuiGraphics graphics, int x, int y, int width, int height,
                                     int contentHeight, int scrollOffset, int maxScroll, int trackColor, int thumbColor) {
        if (maxScroll <= 0 || contentHeight <= 0) {
            return;
        }
        int thumbHeight = Math.max(10, height * height / contentHeight);
        int thumbY = y + (height - thumbHeight) * scrollOffset / maxScroll;
        graphics.fill(x, y, x + width, y + height, trackColor);
        graphics.fill(x, thumbY, x + width, thumbY + thumbHeight, thumbColor);
    }

    public static int drawProgressBar(GuiGraphics graphics, Font font, int x, int y, int width,
                                      String label, int value, int max, int barColor, int bgColor) {
        int barHeight = 8;
        graphics.drawString(font, label + ": " + value + "/" + max, x, y, 0xFFFFFFFF);

        int barY = y + 12;
        graphics.fill(x, barY, x + width, barY + barHeight, 0xFF444455);
        graphics.fill(x + 1, barY + 1, x + width - 1, barY + barHeight - 1, bgColor);

        if (max > 0) {
            int innerWidth = width - 2;
            int fillWidth = (int) Math.min(innerWidth, (long) innerWidth * value / max);
            graphics.fill(x + 1, barY + 1, x + 1 + fillWidth, barY + barHeight - 1, barColor);
        }

        return barY + barHeight;
    }
}
