package com.mohistmc.academy.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class AcademyScreen extends Screen {

    protected int guiLeft;
    protected int guiTop;
    protected int guiWidth = -1;
    protected int guiHeight = -1;

    protected AcademyScreen(Component title) {
        super(title);
    }

    protected void centerGui(int width, int height) {
        this.guiWidth = width;
        this.guiHeight = height;
        this.guiLeft = (this.width - width) / 2;
        this.guiTop = (this.height - height) / 2;
    }

    protected void drawBackground(GuiGraphics graphics, int color) {
        if (guiWidth > 0 && guiHeight > 0) {
            graphics.fill(guiLeft, guiTop, guiLeft + guiWidth, guiTop + guiHeight, color);
        }
    }

    protected void drawTopBar(GuiGraphics graphics, String title) {
        if (guiWidth <= 0) return;
        graphics.fill(guiLeft, guiTop, guiLeft + guiWidth, guiTop + 28, AcademyColors.BG_PANEL);
        graphics.fill(guiLeft, guiTop + 28, guiLeft + guiWidth, guiTop + 29, AcademyColors.ACCENT);
        graphics.drawString(this.font, title, guiLeft + 8, guiTop + 9, AcademyColors.TEXT_ACCENT);
    }

    protected boolean drawBackButton(GuiGraphics graphics, int x, int y, int mouseX, int mouseY) {
        boolean hovered = GuiUtils.isHovered(x, y, 18, 18, mouseX, mouseY);
        graphics.fill(x, y, x + 18, y + 18, hovered ? AcademyColors.BACK_HOVER : AcademyColors.BACK_BG);
        drawBorder(graphics, x, y, 18, 18, AcademyColors.ACCENT);
        String arrow = "<-";
        int aw = this.font.width(arrow);
        graphics.drawString(this.font, arrow, x + (18 - aw) / 2, y + 5, AcademyColors.TEXT);
        return hovered;
    }

    protected void drawBorder(GuiGraphics graphics, int x, int y, int w, int h, int color) {
        GuiUtils.drawBorder(graphics, x, y, w, h, color);
    }

    protected void pushZ(GuiGraphics graphics) {
        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 300);
    }

    protected void popZ(GuiGraphics graphics) {
        graphics.pose().popPose();
    }

    public static boolean isHovered(int x, int y, int w, int h, int mouseX, int mouseY) {
        return GuiUtils.isHovered(x, y, w, h, mouseX, mouseY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
