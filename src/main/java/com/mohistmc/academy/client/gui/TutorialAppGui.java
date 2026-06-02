package com.mohistmc.academy.client.gui;

import com.mohistmc.academy.skill.AcademyAttachments;
import com.mohistmc.academy.skill.PlayerAbilityData;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TutorialAppGui extends AcademyScreen {

    private static final int TOP_BAR = 32;
    private static final int BACK_BTN_SIZE = 18;
    private static final int LIST_WIDTH = 140;
    private static final int PADDING = 12;
    private static final int LINE_HEIGHT = 12;
    private static final int PARAGRAPH_SPACING = 4;
    private static final int HEADING1_SPACING = 8;
    private static final int HEADING2_SPACING = 6;

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

    public TutorialAppGui() { this(false); }
    public TutorialAppGui(boolean fromTerminal) {
        super(Component.translatable("item.academy.app_tutorial"));
        this.fromTerminal = fromTerminal;
    }

    @Override protected void init() { super.init(); loadTutorials(); }

    private void loadTutorials() {
        tutorials.clear();
        for (String id : orderedIds) tutorials.add(TutorialMdParser.parse(id));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        pushZ(graphics);

        graphics.fill(0, 0, this.width, this.height, AcademyColors.BG_DARK);
        graphics.fill(0, 0, this.width, TOP_BAR, AcademyColors.BG_PANEL);
        graphics.fill(0, TOP_BAR, this.width, TOP_BAR + 1, AcademyColors.ACCENT);

        int backX = 6, backY = 5;
        hoveredBack = isHovered(backX, backY, BACK_BTN_SIZE, BACK_BTN_SIZE, mouseX, mouseY);
        if (fromTerminal) {
            graphics.fill(backX, backY, backX + BACK_BTN_SIZE, backY + BACK_BTN_SIZE,
                    hoveredBack ? AcademyColors.BACK_HOVER : AcademyColors.BACK_BG);
            drawBorder(graphics, backX, backY, BACK_BTN_SIZE, BACK_BTN_SIZE, AcademyColors.ACCENT);
            String arrow = "<-";
            int aw = this.font.width(arrow);
            graphics.drawString(this.font, arrow, backX + (BACK_BTN_SIZE - aw) / 2, backY + 5, AcademyColors.TEXT);
        }

        String title = Component.translatable("item.academy.app_tutorial").getString();
        graphics.drawString(this.font, title, fromTerminal ? backX + BACK_BTN_SIZE + 6 : backX, 9, AcademyColors.TEXT_ACCENT);

        graphics.fill(0, TOP_BAR, LIST_WIDTH, this.height, AcademyColors.BG_LIST);
        graphics.fill(LIST_WIDTH, TOP_BAR, LIST_WIDTH + 1, this.height, AcademyColors.SEPARATOR);

        renderList(graphics, mouseX, mouseY);

        int contentX = LIST_WIDTH + PADDING;
        int contentY = TOP_BAR + PADDING;
        int contentW = Math.max(100, this.width - LIST_WIDTH - PADDING * 4);
        int contentH = this.height - TOP_BAR - PADDING * 2;
        if (selectedEntry >= 0 && selectedEntry < tutorials.size()) {
            renderContent(graphics, contentX, contentY, contentW, contentH);
        }
        popZ(graphics);
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
            boolean isHovered = isHovered(4, y, LIST_WIDTH - 8, entryHeight, mouseX, mouseY + listScrollOffset);
            if (isHovered) hoveredEntry = i;

            int bgColor = isSelected ? AcademyColors.SELECTED_BG : (isHovered ? AcademyColors.HOVER_BG : AcademyColors.BG_LIST);
            graphics.fill(4, y, LIST_WIDTH - 4, y + entryHeight - 1, bgColor);
            if (isSelected) graphics.fill(4, y, 6, y + entryHeight - 1, AcademyColors.ACCENT);

            String entryTitle = data.title().isEmpty() ? data.id() : data.title();
            graphics.drawString(this.font, entryTitle, 14, y + 6,
                    isSelected ? AcademyColors.TEXT_ACCENT : AcademyColors.TEXT_SECONDARY);
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
            graphics.fill(scrollBarX, thumbY, scrollBarX + 2, thumbY + thumbH, AcademyColors.ACCENT);
        }
    }

    private String resolveMisakaPlaceholder(String text) {
        if (!text.contains("{@MISAKANAME@}")) return text;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return text.replace("{@MISAKANAME@}", "misaka0000");
        PlayerAbilityData data = mc.player.getData(AcademyAttachments.PLAYER_ABILITY);
        int id = data.getMisakaId();
        if (id < 0) return text.replace("{@MISAKANAME@}", "misaka0000");
        return text.replace("{@MISAKANAME@}", "§l" + Component.translatable("academy.tutorial.misaka", id).getString() + "§r");
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
                case EMPTY -> drawY += LINE_HEIGHT;
                case H1 -> {
                    drawY += HEADING1_SPACING;
                    List<String> wrapped = GuiUtils.wrapText("§b§l" + resolveMisakaPlaceholder(line.text()), w, this.font);
                    for (String s : wrapped) {
                        graphics.drawString(this.font, s, x, drawY, AcademyColors.TEXT_ACCENT);
                        drawY += LINE_HEIGHT + 2;
                    }
                    drawY += HEADING1_SPACING;
                }
                case H2 -> {
                    drawY += HEADING2_SPACING;
                    List<String> wrapped = GuiUtils.wrapText("§l" + resolveMisakaPlaceholder(line.text()), w, this.font);
                    for (String s : wrapped) {
                        graphics.drawString(this.font, s, x, drawY, AcademyColors.TEXT);
                        drawY += LINE_HEIGHT + 2;
                    }
                    drawY += HEADING2_SPACING;
                }
                case TEXT -> {
                    if (line.text().isEmpty()) { drawY += LINE_HEIGHT; break; }
                    List<String> wrapped = GuiUtils.wrapText(resolveMisakaPlaceholder(line.text()), w, this.font);
                    for (String s : wrapped) {
                        graphics.drawString(this.font, s, x, drawY, AcademyColors.TEXT_SECONDARY);
                        drawY += LINE_HEIGHT;
                    }
                    drawY += PARAGRAPH_SPACING;
                }
                case IMAGE -> {
                    int imgH = 64;
                    graphics.fill(x + (w - 128) / 2, drawY, x + (w + 128) / 2, drawY + imgH, 0xFF2A3445);
                    drawBorder(graphics, x + (w - 128) / 2, drawY, 128, imgH, AcademyColors.SEPARATOR);
                    String alt = line.alt().isEmpty() ? "Image" : line.alt();
                    int tw = this.font.width(alt);
                    graphics.drawString(this.font, alt, x + (w - tw) / 2, drawY + imgH / 2 - 4, AcademyColors.TEXT_MUTED);
                    drawY += imgH + PARAGRAPH_SPACING;
                }
            }
        }

        maxContentScroll = Math.max(0, drawY - y - h);
        graphics.pose().popPose();
        graphics.disableScissor();

        if (maxContentScroll > 0) {
            int scrollBarX = x + w - 4;
            int thumbH = Math.max(10, h * h / Math.max(1, drawY - y));
            int thumbY = y + (h - thumbH) * contentScrollOffset / maxContentScroll;
            graphics.fill(scrollBarX, y, scrollBarX + 2, y + h, 0x44506070);
            graphics.fill(scrollBarX, thumbY, scrollBarX + 2, thumbY + thumbH, AcademyColors.ACCENT);
        }
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
            int maxScroll = Math.max(0, tutorials.size() * 24 - (this.height - TOP_BAR - 8));
            listScrollOffset = (int) Math.clamp(listScrollOffset - scrollY * 10, 0, maxScroll);
        } else {
            contentScrollOffset = (int) Math.clamp(contentScrollOffset - scrollY * 20, 0, maxContentScroll);
        }
        return true;
    }
}
