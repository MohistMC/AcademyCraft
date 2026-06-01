package com.mohistmc.academy.client.gui;

import com.mohistmc.academy.client.KeyInputHandler;
import com.mohistmc.academy.skill.AcademyAttachments;
import com.mohistmc.academy.skill.PlayerAbilityData;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SettingsAppGui extends Screen {

    private static final int GUI_WIDTH = 280;
    private static final int GUI_HEIGHT = 200;
    private static final int TOP_BAR = 28;
    private static final int ROW_HEIGHT = 20;
    private static final int BACK_BTN_SIZE = 18;

    private static final int COLOR_BG = 0xE0080818;
    private static final int COLOR_TOP_BAR = 0xFF0a1628;
    private static final int COLOR_ACCENT = 0xFF00bcd4;
    private static final int COLOR_ROW_EVEN = 0xFF101828;
    private static final int COLOR_ROW_ODD = 0xFF141e30;
    private static final int COLOR_TEXT_WHITE = 0xFFFFFFFF;
    private static final int COLOR_TEXT_CYAN = 0xFF00e5ff;
    private static final int COLOR_TEXT_GRAY = 0xFF888899;
    private static final int COLOR_TEXT_GREEN = 0xFF2ecc71;
    private static final int COLOR_TEXT_RED = 0xFFe74c3c;
    private static final int COLOR_SEPARATOR = 0xFF004d5a;
    private static final int COLOR_HOVER = 0x2200bcd4;
    private static final int COLOR_BACK_BG = 0xFF162040;
    private static final int COLOR_BACK_HOVER = 0xFF00bcd4;

    private int guiLeft;
    private int guiTop;
    private int scrollOffset = 0;
    private int hoveredRow = -1;
    private boolean hoveredBack = false;

    private final List<SettingRow> rows = new ArrayList<>();

    public SettingsAppGui() {
        super(Component.translatable("item.academy.app_settings"));
    }

    @Override
    protected void init() {
        super.init();
        this.guiLeft = (this.width - GUI_WIDTH) / 2;
        this.guiTop = (this.height - GUI_HEIGHT) / 2;
        buildRows();
    }

    private void buildRows() {
        rows.clear();
        rows.add(new SettingRow("快捷键设置", "", RowType.HEADER));

        rows.add(new SettingRow("技能槽界面",
                KeyInputHandler.OPEN_SKILL_SLOT.getTranslatedKeyMessage().getString(), RowType.INFO));
        rows.add(new SettingRow("激活/关闭能力",
                KeyInputHandler.TOGGLE_ABILITY.getTranslatedKeyMessage().getString(), RowType.INFO));
        rows.add(new SettingRow("切换预设组",
                KeyInputHandler.SWITCH_PRESET.getTranslatedKeyMessage().getString(), RowType.INFO));

        for (int i = 0; i < KeyInputHandler.getSkillKeys().length; i++) {
            rows.add(new SettingRow("技能槽 " + (i + 1),
                    KeyInputHandler.getSkillKeys()[i].getTranslatedKeyMessage().getString(), RowType.INFO));
        }

        rows.add(new SettingRow("", "", RowType.SEPARATOR));
        rows.add(new SettingRow("能力信息", "", RowType.HEADER));

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            PlayerAbilityData data = mc.player.getData(AcademyAttachments.PLAYER_ABILITY);
            if (data.hasAbility()) {
                String abilityName = Component.translatable("item.academy.factor_" + data.getCurrentAbility().id()).getString();
                rows.add(new SettingRow("当前能力", abilityName, RowType.INFO));
                rows.add(new SettingRow("能力等级", "Lv." + data.getPlayerLevel(), RowType.INFO));
                rows.add(new SettingRow("计算力", String.format("%.0f / %.0f", data.getCurrentCp(), data.getMaxCp()), RowType.INFO));
                rows.add(new SettingRow("过载值", String.format("%.0f / %.0f", data.getCurrentOverload(), data.getMaxOverload()), RowType.INFO));
                rows.add(new SettingRow("已学技能", data.getLearnedSkills().size() + " 个", RowType.INFO));
                rows.add(new SettingRow("能力状态", data.isAbilityActive() ? "已激活" : "未激活", RowType.STATUS));
            } else {
                rows.add(new SettingRow("当前能力", "尚未获得能力", RowType.INFO));
            }
        }
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

        String title = Component.translatable("item.academy.app_settings").getString();
        int titleX = backX + BACK_BTN_SIZE + 6;
        graphics.drawString(this.font, title, titleX, guiTop + 9, COLOR_TEXT_CYAN);

        graphics.enableScissor(guiLeft, guiTop + TOP_BAR + 1, guiLeft + GUI_WIDTH, guiTop + GUI_HEIGHT);
        graphics.pose().pushPose();
        graphics.pose().translate(0, -scrollOffset, 0);

        int adjustedMouseY = mouseY + scrollOffset;
        hoveredRow = -1;

        int y = guiTop + TOP_BAR + 6;
        for (int i = 0; i < rows.size(); i++) {
            SettingRow row = rows.get(i);
            int rowY = y + i * ROW_HEIGHT;

            if (row.type == RowType.SEPARATOR) {
                graphics.fill(guiLeft + 8, rowY + ROW_HEIGHT / 2, guiLeft + GUI_WIDTH - 8, rowY + ROW_HEIGHT / 2 + 1, COLOR_SEPARATOR);
                continue;
            }

            if (row.type != RowType.HEADER) {
                int bgColor = (i % 2 == 0) ? COLOR_ROW_EVEN : COLOR_ROW_ODD;
                graphics.fill(guiLeft + 2, rowY, guiLeft + GUI_WIDTH - 2, rowY + ROW_HEIGHT, bgColor);
            }

            boolean isHovered = mouseX >= guiLeft + 2 && mouseX < guiLeft + GUI_WIDTH - 2
                    && adjustedMouseY >= rowY && adjustedMouseY < rowY + ROW_HEIGHT;
            if (isHovered && row.type != RowType.HEADER) hoveredRow = i;

            if (isHovered && row.type != RowType.HEADER) {
                graphics.fill(guiLeft + 2, rowY, guiLeft + GUI_WIDTH - 2, rowY + ROW_HEIGHT, COLOR_HOVER);
            }

            if (row.type == RowType.HEADER) {
                graphics.drawString(this.font, row.label, guiLeft + 8, rowY + 6, COLOR_TEXT_CYAN);
            } else {
                graphics.drawString(this.font, row.label, guiLeft + 10, rowY + 6, COLOR_TEXT_WHITE);
                int valW = this.font.width(row.value);
                int valColor = row.type == RowType.STATUS
                        ? (row.value.contains("已激活") ? COLOR_TEXT_GREEN : COLOR_TEXT_RED)
                        : COLOR_TEXT_GRAY;
                graphics.drawString(this.font, row.value, guiLeft + GUI_WIDTH - valW - 10, rowY + 6, valColor);
            }
        }

        graphics.pose().popPose();
        graphics.disableScissor();

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
            Minecraft.getInstance().setScreen(new DataTerminalGui());
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int contentHeight = rows.size() * ROW_HEIGHT + 8;
        int visibleHeight = GUI_HEIGHT - TOP_BAR - 1;
        int maxScroll = Math.max(0, contentHeight - visibleHeight);
        scrollOffset = (int) Math.clamp(scrollOffset - scrollY * 10, 0, maxScroll);
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private enum RowType { HEADER, INFO, STATUS, SEPARATOR }

    private record SettingRow(String label, String value, RowType type) {
    }
}
