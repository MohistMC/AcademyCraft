package com.mohistmc.academy.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 频率变送器 App 界面 —— 显示操作指南和当前状态。
 */
@OnlyIn(Dist.CLIENT)
public class FreqTransmitterGui extends Screen {

    private static final int GUI_WIDTH = 240;
    private static final int GUI_HEIGHT = 160;

    private static final int COLOR_BG = 0xE0080818;
    private static final int COLOR_TITLE = 0xFF00bcd4;
    private static final int COLOR_TEXT = 0xFFcccccc;
    private static final int COLOR_ACCENT = 0xFF004d5a;

    private int guiLeft;
    private int guiTop;

    public FreqTransmitterGui() {
        super(Component.translatable("app.academy.freq_transmitter"));
    }

    @Override
    protected void init() {
        super.init();
        this.guiLeft = (this.width - GUI_WIDTH) / 2;
        this.guiTop = (this.height - GUI_HEIGHT) / 2;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 300);

        // 背景
        graphics.fill(guiLeft, guiTop, guiLeft + GUI_WIDTH, guiTop + GUI_HEIGHT, COLOR_BG);
        graphics.fill(guiLeft, guiTop, guiLeft + GUI_WIDTH, guiTop + 1, COLOR_ACCENT);
        graphics.fill(guiLeft, guiTop + GUI_HEIGHT - 1, guiLeft + GUI_WIDTH, guiTop + GUI_HEIGHT, COLOR_ACCENT);

        // 标题
        String title = "§l频率变送器";
        graphics.drawString(this.font, title, guiLeft + 8, guiTop + 8, COLOR_TITLE);

        // 使用说明
        int y = guiTop + 32;
        graphics.drawString(this.font, "§b使用说明:", guiLeft + 8, y, COLOR_TEXT);
        y += 14;
        graphics.drawString(this.font, "§71. 右击无线节点以选择", guiLeft + 12, y, COLOR_TEXT);
        y += 12;
        graphics.drawString(this.font, "§72. 右击机器以完成连接", guiLeft + 12, y, COLOR_TEXT);
        y += 12;
        graphics.drawString(this.font, "§73. 节点名称和密码可在节点GUI中设置", guiLeft + 12, y, COLOR_TEXT);
        y += 16;

        // 当前状态

        graphics.pose().popPose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
