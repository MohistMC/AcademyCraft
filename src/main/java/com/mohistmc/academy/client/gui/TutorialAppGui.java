package com.mohistmc.academy.client.gui;

import com.mohistmc.academy.skill.AcademyAttachments;
import com.mohistmc.academy.skill.PlayerAbilityData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class TutorialAppGui extends Screen {

    private static final int TOP_BAR = 32;
    private static final int BACK_BTN_SIZE = 18;
    private static final int LIST_WIDTH = 140;
    private static final int PADDING = 12;
    private static final int LINE_HEIGHT = 12;
    private static final int PARAGRAPH_SPACING = 4;
    private static final int HEADING1_SPACING = 8;
    private static final int HEADING2_SPACING = 6;

    private static final int COLOR_BG = 0xE8222B38;
    private static final int COLOR_TOP_BAR = 0xFF1E2835;
    private static final int COLOR_ACCENT = 0xFF4AA8FF;
    private static final int COLOR_TEXT_CYAN = 0xFF7EC8E8;
    private static final int COLOR_TEXT_WHITE = 0xFFD8E2F0;
    private static final int COLOR_TEXT_GRAY = 0xFF90A8C0;
    private static final int COLOR_TEXT_DIM = 0xFF607080;
    private static final int COLOR_BACK_BG = 0xFF2A3848;
    private static final int COLOR_BACK_HOVER = 0xFF4AA8FF;
    private static final int COLOR_LIST_BG = 0xE81A2230;
    private static final int COLOR_LIST_SELECTED = 0xFF2A3A4C;
    private static final int COLOR_LIST_HOVER = 0xFF202C3A;
    private static final int COLOR_SEPARATOR = 0xFF3A4A5C;

    private boolean hoveredBack = false;
    private int hoveredEntry = -1;
    private int selectedEntry = 0;
    private int listScrollOffset = 0;
    private int contentScrollOffset = 0;
    private int maxContentScroll = 0;
    private final boolean fromTerminal;

    private final List<String> orderedIds = List.of(
            "welcome", "ores", "phase_generator", "solar_generator", "wind_generator",
            "metal_former", "imag_fusor", "terminal", "ability_developer", "ability_basis",
            "misc", "develop_ability", "wireless_network"
    );

    private final List<TutorialMdParser.TutorialData> tutorials = new ArrayList<>();

    public TutorialAppGui() {
        super(Component.translatable("item.academy.app_tutorial"));
        this.fromTerminal = false;
    }

    public TutorialAppGui(boolean fromTerminal) {
        super(Component.translatable("item.academy.app_tutorial"));
        this.fromTerminal = fromTerminal;
    }

    @Override
    protected void init() {
        super.init();
        loadTutorials();
    }

    private void loadTutorials() {
        tutorials.clear();
        for (String id : orderedIds) {
            TutorialMdParser.TutorialData data = TutorialMdParser.parse(id);
            tutorials.add(data);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 300);

        graphics.fill(0, 0, this.width, this.height, COLOR_BG);

        graphics.fill(0, 0, this.width, TOP_BAR, COLOR_TOP_BAR);
        graphics.fill(0, TOP_BAR, this.width, TOP_BAR + 1, COLOR_ACCENT);

        int backX = 6;
        int backY = 5;
        hoveredBack = mouseX >= backX && mouseX < backX + BACK_BTN_SIZE
                && mouseY >= backY && mouseY < backY + BACK_BTN_SIZE;

        if (fromTerminal) {
            graphics.fill(backX, backY, backX + BACK_BTN_SIZE, backY + BACK_BTN_SIZE,
                    hoveredBack ? COLOR_BACK_HOVER : COLOR_BACK_BG);
            drawBorder(graphics, backX, backY, BACK_BTN_SIZE, BACK_BTN_SIZE, COLOR_ACCENT);
            String arrow = "<-";
            int aw = this.font.width(arrow);
            graphics.drawString(this.font, arrow, backX + (BACK_BTN_SIZE - aw) / 2, backY + 5, COLOR_TEXT_WHITE);
        }

        String title = Component.translatable("item.academy.app_tutorial").getString();
        int titleX = fromTerminal ? backX + BACK_BTN_SIZE + 6 : backX;
        graphics.drawString(this.font, title, titleX, 9, COLOR_TEXT_CYAN);

        graphics.fill(0, TOP_BAR, LIST_WIDTH, this.height, COLOR_LIST_BG);
        graphics.fill(LIST_WIDTH, TOP_BAR, LIST_WIDTH + 1, this.height, COLOR_SEPARATOR);

        renderList(graphics, mouseX, mouseY);

        int contentX = LIST_WIDTH + PADDING;
        int contentY = TOP_BAR + PADDING;
        int contentW = Math.max(100, this.width - LIST_WIDTH - PADDING * 4);
        int contentH = this.height - TOP_BAR - PADDING * 2;

        if (selectedEntry >= 0 && selectedEntry < tutorials.size()) {
            renderContent(graphics, contentX, contentY, contentW, contentH);
        }

        graphics.pose().popPose();
    }

    private void renderList(GuiGraphics graphics, int mouseX, int mouseY) {
        int entryY = TOP_BAR + 4;
        int entryHeight = 24;
        int visibleListHeight = this.height - TOP_BAR - 8;
        int maxListScroll = Math.max(0, tutorials.size() * entryHeight - visibleListHeight);
        listScrollOffset = (int) Math.clamp(listScrollOffset, 0, maxListScroll);

        hoveredEntry = -1;
        graphics.enableScissor(2, TOP_BAR + 2, LIST_WIDTH - 2, this.height - 2);
        graphics.pose().pushPose();
        graphics.pose().translate(0, -listScrollOffset, 0);

        for (int i = 0; i < tutorials.size(); i++) {
            TutorialMdParser.TutorialData data = tutorials.get(i);
            int y = entryY + i * entryHeight;
            if (y + entryHeight < TOP_BAR + 2 || y > this.height + listScrollOffset - 2) continue;

            boolean isSelected = i == selectedEntry;
            boolean isHovered = mouseX >= 4 && mouseX < LIST_WIDTH - 4
                    && mouseY + listScrollOffset >= y && mouseY + listScrollOffset < y + entryHeight;
            if (isHovered) hoveredEntry = i;

            int bgColor = isSelected ? COLOR_LIST_SELECTED : (isHovered ? COLOR_LIST_HOVER : COLOR_LIST_BG);
            graphics.fill(4, y, LIST_WIDTH - 4, y + entryHeight - 1, bgColor);

            if (isSelected) {
                graphics.fill(4, y, 6, y + entryHeight - 1, COLOR_ACCENT);
            }

            String entryTitle = data.title().isEmpty() ? data.id() : data.title();
            int textColor = isSelected ? COLOR_TEXT_CYAN : COLOR_TEXT_GRAY;
            graphics.drawString(this.font, entryTitle, 14, y + 6, textColor);
        }

        graphics.pose().popPose();
        graphics.disableScissor();

        if (maxListScroll > 0) {
            int scrollBarX = LIST_WIDTH - 6;
            int scrollBarTop = TOP_BAR + 4;
            int scrollBarH = this.height - TOP_BAR - 8;
            int thumbH = Math.max(10, scrollBarH * scrollBarH / (tutorials.size() * entryHeight));
            int thumbY = scrollBarTop + (scrollBarH - thumbH) * listScrollOffset / maxListScroll;
            graphics.fill(scrollBarX, scrollBarTop, scrollBarX + 2, scrollBarTop + scrollBarH, 0x44506070);
            graphics.fill(scrollBarX, thumbY, scrollBarX + 2, thumbY + thumbH, 0xFF4AA8FF);
        }
    }

    private String resolveMisakaPlaceholder(String text) {
        if (!text.contains("{@MISAKANAME@}")) return text;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return text.replace("{@MISAKANAME@}", "misaka0000");
        PlayerAbilityData data = mc.player.getData(AcademyAttachments.PLAYER_ABILITY);
        int id = data.getMisakaId();
        if (id < 0) {
            return text.replace("{@MISAKANAME@}", "misaka0000");
        }
        String name = Component.translatable("academy.tutorial.misaka", id).getString();
        return text.replace("{@MISAKANAME@}", "§l" + name + "§r");
    }

    private void renderContent(GuiGraphics graphics, int x, int y, int w, int h) {
        TutorialMdParser.TutorialData data = tutorials.get(selectedEntry);
        List<TutorialMdParser.TutorialLine> lines = data.contentLines();

        graphics.enableScissor(x, y, x + w, y + h);
        graphics.pose().pushPose();
        graphics.pose().translate(0, -contentScrollOffset, 0);

        int drawY = y;
        for (TutorialMdParser.TutorialLine line : lines) {
            switch (line.type()) {
                case EMPTY -> {
                    drawY += LINE_HEIGHT;
                }
                case H1 -> {
                    drawY += HEADING1_SPACING;
                    String processed = resolveMisakaPlaceholder(line.text());
                    List<String> wrapped = wrapText("§b§l" + processed, w);
                    for (String s : wrapped) {
                        graphics.drawString(this.font, s, x, drawY, COLOR_TEXT_CYAN);
                        drawY += LINE_HEIGHT + 2;
                    }
                    drawY += HEADING1_SPACING;
                }
                case H2 -> {
                    drawY += HEADING2_SPACING;
                    String processed = resolveMisakaPlaceholder(line.text());
                    List<String> wrapped = wrapText("§l" + processed, w);
                    for (String s : wrapped) {
                        graphics.drawString(this.font, s, x, drawY, COLOR_TEXT_WHITE);
                        drawY += LINE_HEIGHT + 2;
                    }
                    drawY += HEADING2_SPACING;
                }
                case TEXT -> {
                    if (line.text().isEmpty()) {
                        drawY += LINE_HEIGHT;
                        break;
                    }
                    String processed = resolveMisakaPlaceholder(line.text());
                    List<String> wrapped = wrapText(processed, w);
                    for (String s : wrapped) {
                        graphics.drawString(this.font, s, x, drawY, COLOR_TEXT_GRAY);
                        drawY += LINE_HEIGHT;
                    }
                    drawY += PARAGRAPH_SPACING;
                }
                case IMAGE -> {
                    int imgH = 64;
                    graphics.fill(x + (w - 128) / 2, drawY, x + (w + 128) / 2, drawY + imgH, 0xFF2A3445);
                    drawBorder(graphics, x + (w - 128) / 2, drawY, 128, imgH, COLOR_SEPARATOR);
                    String alt = line.alt().isEmpty() ? "Image" : line.alt();
                    alt = resolveMisakaPlaceholder(alt);
                    int tw = this.font.width(alt);
                    graphics.drawString(this.font, alt, x + (w - tw) / 2, drawY + imgH / 2 - 4, COLOR_TEXT_DIM);
                    drawY += imgH + PARAGRAPH_SPACING;
                }
            }
        }

        int totalContentHeight = drawY - y;
        maxContentScroll = Math.max(0, totalContentHeight - h);

        graphics.pose().popPose();
        graphics.disableScissor();

        if (maxContentScroll > 0) {
            int scrollBarX = x + w - 4;
            int scrollBarTop = y;
            int scrollBarH = h;
            int thumbH = Math.max(10, scrollBarH * scrollBarH / Math.max(1, totalContentHeight));
            int thumbY = scrollBarTop + (scrollBarH - thumbH) * contentScrollOffset / maxContentScroll;
            graphics.fill(scrollBarX, scrollBarTop, scrollBarX + 2, scrollBarTop + scrollBarH, 0x44506070);
            graphics.fill(scrollBarX, thumbY, scrollBarX + 2, thumbY + thumbH, 0xFF4AA8FF);
        }
    }

    private List<String> wrapText(String text, int maxWidth) {
        List<String> result = new ArrayList<>();
        if (text == null || text.isEmpty() || maxWidth <= 0) return result;

        int start = 0;
        while (start < text.length()) {
            int end = start + 1;
            while (end <= text.length()) {
                if (this.font.width(text.substring(start, end)) > maxWidth - 4) {
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

    private void drawBorder(GuiGraphics graphics, int x, int y, int w, int h, int color) {
        graphics.fill(x, y, x + w, y + 1, color);
        graphics.fill(x, y + h - 1, x + w, y + h, color);
        graphics.fill(x, y, x + 1, y + h, color);
        graphics.fill(x + w - 1, y, x + w, y + h, color);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (fromTerminal && hoveredBack && button == 0) {
            Minecraft.getInstance().setScreen(new DataTerminalGui());
            return true;
        }
        if (hoveredEntry >= 0 && button == 0) {
            selectedEntry = hoveredEntry;
            contentScrollOffset = 0;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (mouseX < LIST_WIDTH) {
            int entryHeight = 24;
            int visibleListHeight = this.height - TOP_BAR - 8;
            int maxScroll = Math.max(0, tutorials.size() * entryHeight - visibleListHeight);
            listScrollOffset = (int) Math.clamp(listScrollOffset - scrollY * 10, 0, maxScroll);
        } else {
            contentScrollOffset = (int) Math.clamp(contentScrollOffset - scrollY * 20, 0, maxContentScroll);
        }
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
