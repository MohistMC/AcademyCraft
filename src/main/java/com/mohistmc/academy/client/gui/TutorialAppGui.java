package com.mohistmc.academy.client.gui;

import com.mohistmc.academy.client.KeyInputHandler;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TutorialAppGui extends Screen {

    private static final int GUI_WIDTH = 300;
    private static final int GUI_HEIGHT = 200;
    private static final int TOP_BAR = 28;
    private static final int LINE_HEIGHT = 12;
    private static final int BACK_BTN_SIZE = 18;

    private static final int COLOR_BG = 0xE0080818;
    private static final int COLOR_TOP_BAR = 0xFF0a1628;
    private static final int COLOR_ACCENT = 0xFF00bcd4;
    private static final int COLOR_TEXT_CYAN = 0xFF00e5ff;
    private static final int COLOR_TEXT_WHITE = 0xFFFFFFFF;
    private static final int COLOR_TEXT_GRAY = 0xFFaaaacc;
    private static final int COLOR_TEXT_DIM = 0xFF666688;
    private static final int COLOR_BACK_BG = 0xFF162040;
    private static final int COLOR_BACK_HOVER = 0xFF00bcd4;

    private int guiLeft;
    private int guiTop;
    private int scrollOffset = 0;
    private boolean hoveredBack = false;

    private final List<String> lines = new ArrayList<>();

    public TutorialAppGui() {
        super(Component.translatable("item.academy.app_tutorial"));
    }

    @Override
    protected void init() {
        super.init();
        this.guiLeft = (this.width - GUI_WIDTH) / 2;
        this.guiTop = (this.height - GUI_HEIGHT) / 2;
        buildContent();
    }

    private void buildContent() {
        lines.clear();
        lines.add("=== 御坂云终端 ===");
        lines.add("");
        lines.add("欢迎使用 AcademyCraft 数据终端。");
        lines.add("");
        lines.add("【快速入门】");
        lines.add("1. 使用 能力诱导因子 获取你的超能力");
        lines.add("2. 在 能力开发机 中开发和学习技能");
        lines.add("3. 按 " + KeyInputHandler.OPEN_SKILL_SLOT.getTranslatedKeyMessage().getString() + " 配置技能槽");
        lines.add("4. 按 " + KeyInputHandler.TOGGLE_ABILITY.getTranslatedKeyMessage().getString() + " 激活能力");
        lines.add("5. 使用技能键释放技能");
        lines.add("");
        lines.add("【数据终端】");
        lines.add("数据终端是你的综合管理平台。");
        lines.add("通过安装APP安装器来扩展功能。");
        lines.add("内置APP: 设置、御坂云终端");
        lines.add("可安装: 技能树、频率变送器、媒体播放器");
        lines.add("");
        lines.add("【能源系统】");
        lines.add("频率变送器APP用于建立能源连接。");
        lines.add("在机器、节点和矩阵之间传输能量。");
        lines.add("");
        lines.add("【提示】");
        lines.add("- 技能的熟练度通过使用来提升");
        lines.add("- 高级技能需要先学习前置技能");
        lines.add("- 合理使用预设组快速切换技能搭配");
        lines.add("");
        lines.add("--- AcademyCraft ---");
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 300);

        graphics.fill(guiLeft, guiTop, guiLeft + GUI_WIDTH, guiTop + GUI_HEIGHT, COLOR_BG);
        graphics.fill(guiLeft, guiTop, guiLeft + GUI_WIDTH, guiTop + TOP_BAR, COLOR_TOP_BAR);
        graphics.fill(guiLeft, guiTop + TOP_BAR, guiLeft + GUI_WIDTH, guiTop + TOP_BAR + 1, COLOR_ACCENT);

        int backX = guiLeft + 6;
        int backY = guiTop + 5;
        hoveredBack = mouseX >= backX && mouseX < backX + BACK_BTN_SIZE
                && mouseY >= backY && mouseY < backY + BACK_BTN_SIZE;

        graphics.fill(backX, backY, backX + BACK_BTN_SIZE, backY + BACK_BTN_SIZE,
                hoveredBack ? COLOR_BACK_HOVER : COLOR_BACK_BG);
        drawBorder(graphics, backX, backY, BACK_BTN_SIZE, BACK_BTN_SIZE, COLOR_ACCENT);
        String arrow = "<-";
        int aw = this.font.width(arrow);
        graphics.drawString(this.font, arrow, backX + (BACK_BTN_SIZE - aw) / 2, backY + 5, COLOR_TEXT_WHITE);

        String title = Component.translatable("item.academy.app_tutorial").getString();
        int titleX = backX + BACK_BTN_SIZE + 6;
        graphics.drawString(this.font, title, titleX, guiTop + 9, COLOR_TEXT_CYAN);

        graphics.enableScissor(guiLeft + 4, guiTop + TOP_BAR + 2, guiLeft + GUI_WIDTH - 4, guiTop + GUI_HEIGHT - 4);
        graphics.pose().pushPose();
        graphics.pose().translate(0, -scrollOffset, 0);

        int y = guiTop + TOP_BAR + 8;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            int lineY = y + i * LINE_HEIGHT;
            if (lineY + LINE_HEIGHT < guiTop + TOP_BAR || lineY > guiTop + GUI_HEIGHT) continue;

            if (line.isEmpty()) continue;

            int color = COLOR_TEXT_GRAY;
            if (line.startsWith("===") && line.endsWith("===")) {
                color = COLOR_TEXT_CYAN;
            } else if (line.startsWith("【") || line.startsWith("[") ) {
                color = COLOR_TEXT_WHITE;
            } else if (line.startsWith("---")) {
                color = COLOR_TEXT_DIM;
            }

            graphics.drawString(this.font, line, guiLeft + 12, lineY, color);
        }

        graphics.pose().popPose();
        graphics.disableScissor();

        int maxScroll = Math.max(0, lines.size() * LINE_HEIGHT - (GUI_HEIGHT - TOP_BAR - 8));
        if (maxScroll > 0) {
            int scrollBarX = guiLeft + GUI_WIDTH - 5;
            int scrollBarTop = guiTop + TOP_BAR + 2;
            int scrollBarH = GUI_HEIGHT - TOP_BAR - 6;
            int thumbH = Math.max(10, scrollBarH * scrollBarH / (lines.size() * LINE_HEIGHT));
            int thumbY = scrollBarTop + (scrollBarH - thumbH) * scrollOffset / maxScroll;
            graphics.fill(scrollBarX, scrollBarTop, scrollBarX + 2, scrollBarTop + scrollBarH, 0x44FFFFFF);
            graphics.fill(scrollBarX, thumbY, scrollBarX + 2, thumbY + thumbH, 0x8800bcd4);
        }

        graphics.pose().popPose();
    }

    private void drawBorder(GuiGraphics graphics, int x, int y, int w, int h, int color) {
        graphics.fill(x, y, x + w, y + 1, color);
        graphics.fill(x, y + h - 1, x + w, y + h, color);
        graphics.fill(x, y, x + 1, y + h, color);
        graphics.fill(x + w - 1, y, x + w, y + h, color);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (hoveredBack && button == 0) {
            Minecraft.getInstance().setScreen(new DataTerminalGui(true));
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int contentHeight = lines.size() * LINE_HEIGHT;
        int visibleHeight = GUI_HEIGHT - TOP_BAR - 8;
        int maxScroll = Math.max(0, contentHeight - visibleHeight);
        scrollOffset = (int) Math.clamp(scrollOffset - scrollY * 10, 0, maxScroll);
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
