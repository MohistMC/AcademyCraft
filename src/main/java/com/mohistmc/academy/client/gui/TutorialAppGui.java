package com.mohistmc.academy.client.gui;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.skill.AcademyAttachments;
import com.mohistmc.academy.skill.PlayerAbilityData;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * GitHub 风格教程 GUI。
 * 支持完整 Markdown 渲染：标题、粗体/斜体/删除线、代码块、列表、引用、图片、表格。
 */
@OnlyIn(Dist.CLIENT)
public class TutorialAppGui extends AcademyScreen {

    // Layout
    private static final int TOP_BAR = 32;
    private static final int LIST_W = 140;
    private static final int PAD = 12;
    private static final int LINE = 10;

    // Code block
    private static final int CODE_BG = 0xE01A2230;
    private static final int CODE_BORDER = 0xFF34495e;
    private static final int CODE_PAD = 6;

    // Quote
    private static final int QUOTE_BAR = 0xFF00bcd4;
    private static final int QUOTE_BG = 0x4000bcd4;
    private static final int QUOTE_PAD = 10;

    // HR
    private static final int HR_COLOR = 0xFF34495e;

    // Table
    private static final int TABLE_BORDER = 0xFF34495e;
    private static final int TABLE_HEAD_BG = 0xFF1a2a3e;
    private static final int TABLE_ROW_BG = 0xFF101828;
    private static final int TABLE_ROW_ALT = 0xFF141e30;

    // Image defaults
    private static final ResourceLocation IMG_PLACEHOLDER =
            ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/tutorials/placeholder.png");

    private boolean hoveredBack;
    private int hoveredEntry = -1;
    private int selectedEntry;
    private int listScroll;
    private int contentScroll;
    private int maxContentScroll;
    private final boolean fromTerminal;

    private static final List<String> ORDERED = List.of(
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
        for (String id : ORDERED) tutorials.add(TutorialMdParser.parse(id));
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        super.render(g, mx, my, pt);
        pushZ(g);

        // 纯黑实色全屏背景
        g.fill(0, 0, width, height, 0xFF080c1a);

        // Top bar
        g.fill(0, 0, width, TOP_BAR, AcademyColors.BG_PANEL);
        g.fill(0, TOP_BAR, width, TOP_BAR + 1, AcademyColors.ACCENT);

        // Back button
        if (fromTerminal) {
            int bx = 6, by = 5, bs = 18;
            hoveredBack = isHovered(bx, by, bs, bs, mx, my);
            g.fill(bx, by, bx + bs, by + bs, hoveredBack ? AcademyColors.BACK_HOVER : AcademyColors.BACK_BG);
            drawBorder(g, bx, by, bs, bs, AcademyColors.ACCENT);
            String arrow = "<-";
            int aw = font.width(arrow);
            g.drawString(font, arrow, bx + (bs - aw) / 2, by + 5, AcademyColors.TEXT);
        }
        String title = Component.translatable("item.academy.app_tutorial").getString();
        int titleX = fromTerminal ? 30 : 6;
        g.drawString(font, title, titleX, 9, AcademyColors.TEXT_ACCENT);

        // Left panel
        g.fill(0, TOP_BAR, LIST_W, height, AcademyColors.BG_LIST);
        g.fill(LIST_W, TOP_BAR, LIST_W + 1, height, AcademyColors.SEPARATOR);
        renderList(g, mx, my);

        // Content area
        int cx = LIST_W + PAD, cy = TOP_BAR + PAD;
        int cw = Math.max(100, width - LIST_W - PAD * 4);
        int ch = height - TOP_BAR - PAD * 2;
        if (selectedEntry >= 0 && selectedEntry < tutorials.size()) {
            renderContent(g, cx, cy, cw, ch);
        }
        popZ(g);
    }

    // ==================== List ====================

    private void renderList(GuiGraphics g, int mx, int my) {
        int eh = 24;
        int visH = height - TOP_BAR - 8;
        int maxScroll = Math.max(0, tutorials.size() * eh - visH);
        listScroll = (int) Math.clamp(listScroll, 0, maxScroll);

        hoveredEntry = -1;
        g.enableScissor(2, TOP_BAR + 2, LIST_W - 2, height - 2);
        g.pose().pushPose();
        g.pose().translate(0, -listScroll, 0);

        for (int i = 0; i < tutorials.size(); i++) {
            var d = tutorials.get(i);
            int y = TOP_BAR + 4 + i * eh;
            if (y + eh < TOP_BAR || y > height + listScroll) continue;

            boolean sel = i == selectedEntry;
            boolean hov = isHovered(4, y, LIST_W - 8, eh, mx, my + listScroll);
            if (hov) hoveredEntry = i;

            g.fill(4, y, LIST_W - 4, y + eh - 1,
                    sel ? AcademyColors.SELECTED_BG : (hov ? AcademyColors.HOVER_BG : AcademyColors.BG_LIST));
            if (sel) g.fill(4, y, 6, y + eh - 1, AcademyColors.ACCENT);

            g.drawString(font, d.title().isEmpty() ? d.id() : d.title(), 14, y + 6,
                    sel ? AcademyColors.TEXT_ACCENT : AcademyColors.TEXT_SECONDARY);
        }
        g.pose().popPose();
        g.disableScissor();

        if (maxScroll > 0) renderScrollbar(g, LIST_W - 6, TOP_BAR + 4, visH, tutorials.size() * eh,
                listScroll, maxScroll);
    }

    // ==================== Content ====================

    private String resolveMisaka(String text) {
        if (!text.contains("{@MISAKANAME@}")) return text;
        Minecraft mc = Minecraft.getInstance();
        int id = mc.player != null ? mc.player.getData(AcademyAttachments.PLAYER_ABILITY).getMisakaId() : -1;
        String name = id >= 0 ? Component.translatable("academy.tutorial.misaka", id).getString() : "misaka0000";
        return text.replace("{@MISAKANAME@}", "§l" + name + "§r");
    }

    private void renderContent(GuiGraphics g, int x, int y, int w, int h) {
        var data = tutorials.get(selectedEntry);
        var lines = data.contentLines();

        g.enableScissor(x, y, x + w, y + h);
        g.pose().pushPose();
        g.pose().translate(0, -contentScroll, 0);

        int dy = y;
        for (var line : lines) {
            dy += renderLine(g, line, x, dy, w);
        }

        maxContentScroll = Math.max(0, dy - y - h);
        g.pose().popPose();
        g.disableScissor();

        if (maxContentScroll > 0) renderScrollbar(g, x + w - 4, y, h, dy - y, contentScroll, maxContentScroll);
    }

    /** Render a single line, return its height. */
    private int renderLine(GuiGraphics g, TutorialMdParser.TutorialLine line, int x, int y, int w) {
        return switch (line.type()) {
            case EMPTY -> LINE + 2;
            case HR -> {
                g.fill(x, y + 4, x + w, y + 5, HR_COLOR);
                yield 12;
            }
            case H1 -> renderHeading(g, resolveMisaka(TutorialMdParser.processInline(line.text())), x, y, w, 1.8f, AcademyColors.TEXT_ACCENT);
            case H2 -> renderHeading(g, resolveMisaka(TutorialMdParser.processInline(line.text())), x, y, w, 1.4f, AcademyColors.TEXT);
            case H3 -> renderHeading(g, resolveMisaka(TutorialMdParser.processInline(line.text())), x, y, w, 1.15f, AcademyColors.TEXT_SECONDARY);
            case H4 -> renderHeading(g, resolveMisaka(TutorialMdParser.processInline(line.text())), x, y, w, 1.0f, AcademyColors.TEXT_MUTED);
            case TEXT -> {
                var ws = GuiUtils.wrapText(resolveMisaka(TutorialMdParser.processInline(line.text())), w, font);
                for (var s : ws) { g.drawString(font, s, x, y, AcademyColors.TEXT_SECONDARY); y += LINE; }
                yield ws.size() * LINE + 2;
            }
            case IMAGE -> renderImage(g, line, x, y, w);
            case CODE_BLOCK -> renderCodeBlock(g, line.text(), x, y, w);
            case LI -> {
                g.drawString(font, "§7•§r", x, y + 2, AcademyColors.TEXT_MUTED);
                var ws = GuiUtils.wrapText(resolveMisaka(TutorialMdParser.processInline(line.text())), w - 12, font);
                for (int i = 0; i < ws.size(); i++) {
                    g.drawString(font, ws.get(i), x + 12, y + (i == 0 ? 0 : (LINE + 1)), AcademyColors.TEXT_SECONDARY);
                }
                int h = Math.max(LINE + 2, ws.size() * (LINE + 1));
                yield h;
            }
            case OL -> {
                g.drawString(font, "§71.§r", x, y + 2, AcademyColors.TEXT_MUTED);
                var ws = GuiUtils.wrapText(resolveMisaka(TutorialMdParser.processInline(line.text())), w - 12, font);
                for (int i = 0; i < ws.size(); i++) {
                    g.drawString(font, ws.get(i), x + 12, y + (i == 0 ? 0 : (LINE + 1)), AcademyColors.TEXT_SECONDARY);
                }
                int h = Math.max(LINE + 2, ws.size() * (LINE + 1));
                yield h;
            }
            case QUOTE -> {
                g.fill(x, y, x + 2, y + LINE + 4, QUOTE_BAR);
                g.fill(x + 2, y, x + w, y + LINE + 4, QUOTE_BG);
                var ws = GuiUtils.wrapText(resolveMisaka(TutorialMdParser.processInline(line.text())), w - QUOTE_PAD, font);
                for (var s : ws) g.drawString(font, s, x + QUOTE_PAD, y + 2, AcademyColors.TEXT_SECONDARY);
                yield Math.max(LINE + 6, ws.size() * (LINE + 1) + 4);
            }
            case TABLE_ROW -> renderTableRow(g, line, x, y, w);
            case TABLE_SEP -> 10;
            default -> 0;
        };
    }

    private int renderHeading(GuiGraphics g, String text, int x, int y, int w, float scale, int color) {
        int topPad = (int)(4 * scale);
        int bottomPad = (int)(2 * scale);
        g.pose().pushPose();
        g.pose().translate(x, y + topPad, 0);
        g.pose().scale(scale, scale, 1f);
        var ws = GuiUtils.wrapText(TutorialMdParser.processInline(text), (int)(w / scale), font);
        for (var s : ws) {
            g.drawString(font, s, 0, 0, color);
            g.pose().translate(0, LINE + 2, 0);
        }
        g.pose().popPose();
        float lines = Math.max(1, ws.size());
        return (int)(lines * (LINE + 2) * scale) + topPad + bottomPad;
    }

    private int renderImage(GuiGraphics g, TutorialMdParser.TutorialLine line, int x, int y, int w) {
        int maxW = Math.min(w - 10, 220);
        int imgH = Math.min(120, maxW);
        int imgX = x + (w - maxW) / 2;
        ResourceLocation img = line.image() != null ? line.image() : IMG_PLACEHOLDER;

        // Border + background
        g.fill(imgX - 1, y - 1, imgX + maxW + 1, y + imgH + 1, AcademyColors.BORDER_ACTIVE);
        g.fill(imgX, y, imgX + maxW, y + imgH, 0xFF1a2a3e);

        // Try render texture
        try {
            g.blit(img, imgX + 2, y + 2, 0, 0, maxW - 4, imgH - 4, maxW - 4, imgH - 4);
        } catch (Exception e) {
            String alt = line.alt().isEmpty() ? line.image().toString() : line.alt();
            String shortAlt = alt.length() > 30 ? alt.substring(0, 28) + ".." : alt;
            int tw = font.width(shortAlt);
            g.drawString(font, shortAlt, imgX + (maxW - tw) / 2, y + imgH / 2 - 4, AcademyColors.TEXT_MUTED);
        }
        if (!line.alt().isEmpty()) {
            int aw = font.width(line.alt());
            g.drawString(font, "§7" + line.alt(), imgX + (maxW - aw) / 2, y + imgH + 2, AcademyColors.TEXT_MUTED);
            return imgH + LINE + 6;
        }
        return imgH + 6;
    }

    private int renderCodeBlock(GuiGraphics g, String code, int x, int y, int w) {
        String[] codeLines = code.split("\n");
        int boxH = codeLines.length * (LINE + 1) + CODE_PAD * 2;
        g.fill(x, y, x + w, y + boxH, CODE_BG);
        drawBorder(g, x, y, w, boxH, CODE_BORDER);

        for (int i = 0; i < codeLines.length; i++) {
            g.drawString(font, "§7" + codeLines[i], x + CODE_PAD, y + CODE_PAD + i * (LINE + 1),
                    AcademyColors.TEXT_MUTED);
        }
        return boxH + 4;
    }

    private int renderTableRow(GuiGraphics g, TutorialMdParser.TutorialLine line, int x, int y, int w) {
        List<String> cells = line.cells();
        if (cells.isEmpty()) return LINE + 2;
        int cols = cells.size();
        if (cols == 0) return LINE + 2;
        int colW = w / cols;
        for (int ci = 0; ci < cols; ci++) {
            int cx = x + ci * colW;
            g.fill(cx, y, cx + 1, y + LINE + 6, TABLE_BORDER);
            g.fill(cx + colW - 1, y, cx + colW, y + LINE + 6, TABLE_BORDER);
            String cell = cells.get(ci);
            int tw = font.width(cell);
            g.drawString(font, cell, cx + (colW - tw) / 2, y + 2, AcademyColors.TEXT_SECONDARY);
        }
        g.fill(x, y + LINE + 5, x + w, y + LINE + 6, TABLE_BORDER);
        return LINE + 6;
    }

    // ==================== Scroll ====================

    private void renderScrollbar(GuiGraphics g, int sx, int sy, int sh, int totalH, int offset, int max) {
        int thumbH = Math.max(12, sh * sh / Math.max(1, totalH));
        int thumbY = sy + (sh - thumbH) * offset / max;
        g.fill(sx, sy, sx + 2, sy + sh, 0x44506070);
        g.fill(sx, thumbY, sx + 2, thumbY + thumbH, AcademyColors.ACCENT);
    }

    // ==================== Input ====================

    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        if (fromTerminal && hoveredBack && btn == 0) { Minecraft.getInstance().setScreen(new DataTerminalGui()); return true; }
        if (hoveredEntry >= 0 && btn == 0) { selectedEntry = hoveredEntry; contentScroll = 0; return true; }
        return super.mouseClicked(mx, my, btn);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double sx, double sy) {
        if (mx < LIST_W) {
            int max = Math.max(0, tutorials.size() * 24 - (height - TOP_BAR - 8));
            listScroll = (int) Math.clamp(listScroll - sy * 10, 0, max);
        } else {
            contentScroll = (int) Math.clamp(contentScroll - sy * 20, 0, maxContentScroll);
        }
        return true;
    }
}
