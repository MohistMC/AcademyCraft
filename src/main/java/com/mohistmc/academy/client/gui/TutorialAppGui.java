package com.mohistmc.academy.client.gui;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.client.gui.tutorial.RecipeViews;
import com.mohistmc.academy.skill.AcademyAttachments;
import com.mohistmc.academy.client.gui.tutorial.RecipeViews.PreviewView;
import com.mohistmc.academy.tutorial.ACTutorial;
import com.mohistmc.academy.tutorial.TutorialInit;
import com.mohistmc.academy.tutorial.TutorialRegistry;
import com.mohistmc.academy.tutorial.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 云终端教程 GUI —— 布局对齐 Return GuiTutorial（左列表 + 右 Logo + 内容区 + 预览窗口 + 简介窗口）。
 */
@OnlyIn(Dist.CLIENT)
public class TutorialAppGui extends AcademyScreen {

    private static final double REF_WIDTH = 480;

    private static final ResourceLocation TEX_WINDOW = tex("guis/window_tutorial_left");
    private static final ResourceLocation LOGO0 = tex("guis/tutorial/logo0");
    private static final ResourceLocation LOGO1 = tex("guis/tutorial/logo1");
    private static final ResourceLocation LOGO2 = tex("guis/tutorial/logo2");
    private static final ResourceLocation LOGO3 = tex("guis/tutorial/logo3");
    private static final ResourceLocation BTN_LEFT = tex("guis/button/button_left_2");
    private static final ResourceLocation BTN_RIGHT = tex("guis/button/button_right_2");

    private static ResourceLocation tex(String path) {
        return ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/" + path + ".png");
    }

    // 帧布局(Return 坐标)
    private static final float FRAME_W = 427, FRAME_H = 240;
    private static final float LIST_W = 85, LIST_H = 220.5f;
    private static final float LIST_ITEM_W = 72, LIST_ITEM_H = 12;
    private static final float RIGHT_X = 92, RIGHT_W = 332;
    private static final float CENTER_W = 172, CENTER_H = 220.5f;
    private static final float SHOW_W = 158.5f, SHOW_H = 136;
    private static final float RIGHT_WIN_W = 158.5f, RIGHT_WIN_H = 82;
    private static final float TAG_H = 18;

    private final boolean fromTerminal;
    private final List<ACTutorial> learned = new ArrayList<>();
    private final List<ACTutorial> unlearned = new ArrayList<>();

    private ACTutorial currentTut;
    private int previewIndex;
    private int viewIndex;
    private List<PreviewView> currentViews = List.of();
    private int contentScroll;
    private int maxContentScroll;
    /** 客户端平滑滚动位置(插值过渡,避免逐格跳动) */
    private float smoothScroll = 0;

    // 帧屏幕坐标
    private float fx, fy, fscale;

    public TutorialAppGui() {
        this(false);
    }

    public TutorialAppGui(boolean fromTerminal) {
        super(Component.translatable("item.academy.app_tutorial"));
        this.fromTerminal = fromTerminal;
        // 懒注册教程(客户端首次打开时)
        if (TutorialRegistry.enumeration().isEmpty()) {
            TutorialInit.init();
        }
        List<ACTutorial>[] groups = TutorialRegistry.groupByLearned(Minecraft.getInstance().player);
        learned.addAll(groups[0]);
        unlearned.addAll(groups[1]);
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        super.render(g, mx, my, pt);
        // 暗色全屏透明背景
        g.fill(0, 0, width, height, 0x66000000);
        pushZ(g);

        fscale = (float) (width / REF_WIDTH);
        fx = (width - FRAME_W * fscale) / 2f;
        fy = (height - FRAME_H * fscale) / 2f;

        g.pose().pushPose();
        g.pose().translate(fx, fy, 0);
        g.pose().scale(fscale, fscale, 1);

        // 纹理带透明度,必须开启混合
        com.mojang.blaze3d.systems.RenderSystem.enableBlend();
        com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();

        drawBackButton(g, mx, my);
        drawLeftList(g, mx, my);
        drawRightPart(g, mx, my);

        com.mojang.blaze3d.systems.RenderSystem.disableBlend();
        g.pose().popPose();
        popZ(g);

        // 原版物品 tooltip(无变换 pose + 补 z 深度,避免被帧内容遮挡)
        if (!RecipeViews.lastHoverStack.isEmpty()) {
            g.pose().pushPose();
            g.pose().translate(0, 0, 300);
            g.renderTooltip(font, RecipeViews.lastHoverStack, RecipeViews.lastHoverX, RecipeViews.lastHoverY);
            g.pose().popPose();
            RecipeViews.lastHoverStack = ItemStack.EMPTY;
        }
    }

    // ==================== 返回按钮 ====================

    private void drawBackButton(GuiGraphics g, int mx, int my) {
        if (!fromTerminal) return;
        int bx = -20, by = -20, bs = 18;
        boolean hovered = mx >= fx + bx && mx <= fx + bx + bs && my >= fy + by && my <= fy + by + bs;
        g.fill(bx, by, bx + bs, by + bs, hovered ? 0xFF3a4a5e : 0xFF2a3a4e);
        String arrow = "<-";
        int aw = font.width(arrow);
        g.drawString(font, arrow, bx + (bs - aw) / 2, by + 5, 0xFFBFC6CC);
    }

    // ==================== 左侧列表 ====================

    private void drawLeftList(GuiGraphics g, int mx, int my) {
        com.mojang.blaze3d.systems.RenderSystem.enableBlend();
        com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();
        GuiRenderHelper.blitTranslucent(g, TEX_WINDOW, 0, 0, (int) LIST_W, (int) LIST_H);

        // 已学在上、未学在下,顺序排列不重叠
        float y = 7;
        y = drawListGroup(g, mx, my, learned, true, y, 0);
        drawListGroup(g, mx, my, unlearned, false, y, learned.size());
    }

    private float drawListGroup(GuiGraphics g, int mx, int my, List<ACTutorial> list, boolean isLearned, float startY, int startIdx) {
        float x = 6.6f, y = startY;
        for (ACTutorial t : list) {
            boolean sel = currentTut == t;
            boolean hov = isHoveredInFrame(mx, my, x, y, LIST_ITEM_W, LIST_ITEM_H);
            g.fill((int) x, (int) y, (int) (x + LIST_ITEM_W), (int) (y + LIST_ITEM_H),
                    sel ? 0x4DFFFFFF : (hov ? 0x26FFFFFF : 0x00000000));
            drawText(g, t.getTitle(), x, y + 2, isLearned ? 0xFFFFFFFF : 0x99FFFFFF, 10);
            y += LIST_ITEM_H;
            startIdx++;
        }
        return y;
    }

    // ==================== 右侧部分 ====================

    private void drawRightPart(GuiGraphics g, int mx, int my) {
        // 未选中教程:只显示 Logo 背景(Return onSelect 前仅 logo)
        if (currentTut == null) {
            drawLogo(g, LOGO1, 899, 236, 0, 59);
            drawLogo(g, LOGO0, 899, 548, 0, -32.5f);
            drawLogo(g, LOGO3, 149, 149, 0, -36);
            drawLogo(g, LOGO2, 899, 236, 0, 59);
            return;
        }

        // 右部分(332 宽):内容区 + 预览窗口(右上) + 简介窗口(右下)
        g.pose().pushPose();
        g.pose().translate(RIGHT_X, 0, 0);
        drawContent(g, mx, my);
        drawShowWindow(g, mx, my);
        drawRightWindow(g);
        g.pose().popPose();
    }

    private void drawLogo(GuiGraphics g, ResourceLocation tex, float w, float h, float dx, float dy) {
        // 中心在右部分中心(RIGHT_X + RIGHT_W/2),offset dx/dy
        float cx = RIGHT_X + RIGHT_W / 2 + dx;
        float cy = CENTER_H / 2 + dy;
        float lx = cx - w * 0.25f / 2;
        float ly = cy - h * 0.25f / 2;
        g.pose().pushPose();
        g.pose().translate(lx, ly, 0);
        g.pose().scale(0.25f, 0.25f, 1);
        GuiRenderHelper.blitTranslucent(g, tex, 0, 0, (int) w, (int) h);
        g.pose().popPose();
    }

    // ==================== 内容区(Markdown) ====================

    private void drawContent(GuiGraphics g, int mx, int my) {
        if (currentTut == null) return;

        var lines = TutorialMdParser.parse(currentTut.id).contentLines();

        // 平滑滚动插值
        smoothScroll += (contentScroll - smoothScroll) * 0.25f;
        if (Math.abs(smoothScroll - contentScroll) < 0.5f) smoothScroll = contentScroll;

        // 1.21.1 enableScissor(minX, minY, maxX, maxY) —— 最大坐标语义,不是宽高!
        int scissorX = (int) (fx + (RIGHT_X + 2) * fscale);
        int scissorY = (int) (fy + 2 * fscale);
        int scissorW = (int) ((CENTER_W - 4) * fscale);
        int scissorH = (int) ((CENTER_H - 4) * fscale);
        g.enableScissor(scissorX, scissorY, scissorX + scissorW, scissorY + scissorH);
        g.pose().pushPose();
        g.pose().translate(0, -smoothScroll, 0);

        float dy = 4;
        for (var line : lines) {
            dy += renderContentLine(g, line, dy);
        }
        maxContentScroll = Math.max(0, (int) dy - (int) CENTER_H);

        g.pose().popPose();
        g.disableScissor();

        // 滚动条(内容区右缘)
        drawContentScrollbar(g);
    }

    private void drawContentScrollbar(GuiGraphics g) {
        // 常驻显示(Return: scroll_1 轨道 + scroll_2 拖动块,DragBar 范围 2~165)
        com.mojang.blaze3d.systems.RenderSystem.enableBlend();
        com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();
        float trackX = CENTER_W - 9.5f;
        GuiRenderHelper.blitTranslucent(g, tex("guis/button/widget_scroll_1"), (int) trackX, 0, 9, (int) CENTER_H);
        float progress = maxContentScroll > 0 ? Math.min(1f, smoothScroll / maxContentScroll) : 0;
        // 拖动块贴轨道:上边缘从 0 到 (轨道高 - 块高)
        float thumbY = (CENTER_H - 53) * progress;
        GuiRenderHelper.blitTranslucent(g, tex("guis/button/widget_scroll_2"), (int) trackX, (int) thumbY, 9, 53);
        com.mojang.blaze3d.systems.RenderSystem.disableBlend();
    }

    private int renderContentLine(GuiGraphics g, TutorialMdParser.TutorialLine line, float y) {
        int x = 6;
        int w = (int) CENTER_W - 18;
        return switch (line.type()) {
            case EMPTY -> 4;
            case HR -> {
                g.fill(x, (int) y + 4, x + w, (int) y + 5, 0xFF34495e);
                yield 10;
            }
            case H1 -> renderHeading(g, line.text(), x, y, w, 1.5f, 0xFF00bcd4);
            case H2 -> renderHeading(g, line.text(), x, y, w, 1.25f, 0xFFFFFFFF);
            case H3 -> renderHeading(g, line.text(), x, y, w, 1.1f, 0xFFBFC6CC);
            case H4 -> renderHeading(g, line.text(), x, y, w, 1.0f, 0xFF8A9299);
            case TEXT -> {
                int dy = 0;
                for (var s : wrapScaled(resolveMisaka(TutorialMdParser.processInline(line.text())), w, 9)) {
                    drawText(g, s, x, y + dy, 0xFFBFC6CC, 9);
                    dy += 9;
                }
                yield dy + 2;
            }
            case LI -> {
                int dy = 0;
                drawText(g, "§7•", x, y + dy, 0xFF8A9299, 9);
                dy += 9;
                for (var s : wrapScaled(resolveMisaka(TutorialMdParser.processInline(line.text())), w - 8, 9)) {
                    drawText(g, s, x + 8, y + dy, 0xFFBFC6CC, 9);
                    dy += 9;
                }
                yield dy + 2;
            }
            case CODE_BLOCK -> {
                String[] codeLines = line.text().split("\n");
                int boxH = codeLines.length * 9 + 10;
                g.fill(x, (int) y, x + w, (int) y + boxH, 0xE01A2230);
                for (int i = 0; i < codeLines.length; i++) {
                    drawText(g, "§7" + codeLines[i], x + 4, y + 4 + i * 9, 0xFF8A9299, 9);
                }
                yield boxH + 4;
            }
            case QUOTE -> {
                g.fill(x, (int) y, x + 2, (int) y + 12, 0xFF00bcd4);
                g.fill(x + 2, (int) y, x + w, (int) y + 12, 0x4000bcd4);
                int dy = 0;
                for (var s : wrapScaled(resolveMisaka(TutorialMdParser.processInline(line.text())), w - 10, 9)) {
                    drawText(g, s, x + 8, y + dy + 2, 0xFFBFC6CC, 9);
                    dy += 9;
                }
                yield Math.max(14, dy + 4);
            }
            default -> 10;
        };
    }

    private int renderHeading(GuiGraphics g, String text, float x, float y, float w, float scale, int color) {
        int dy = 0;
        for (var s : wrapScaled(resolveMisaka(TutorialMdParser.processInline(text)), (int) w, 9 * scale)) {
            drawText(g, s, x, y + dy, color, 9 * scale);
            dy += 10;
        }
        return (int) (dy * scale) + 2;
    }

    private List<String> wrap(String text, int maxWidth) {
        return GuiUtils.wrapText(text, Math.max(20, maxWidth), font);
    }

    /** 按绘制字号缩放后的宽度换行(文字以 size/16 缩放绘制,换行宽度需按比例放大) */
    private List<String> wrapScaled(String text, int maxWidth, float size) {
        float s = size / 16f;
        return GuiUtils.wrapText(text, Math.max(20, (int) (maxWidth / s)), font);
    }

    /** 替换 {@MISAKANAME@} 为玩家的御坂号 */
    private String resolveMisaka(String text) {
        if (!text.contains("{@MISAKANAME@}")) return text;
        Minecraft mc = Minecraft.getInstance();
        int id = mc.player != null ? mc.player.getData(AcademyAttachments.PLAYER_ABILITY).getMisakaId() : -1;
        String name = id >= 0 ? Component.translatable("academy.tutorial.misaka", id).getString() : "misaka0000";
        return text.replace("{@MISAKANAME@}", "§l" + name + "§r");
    }

    // ==================== 预览窗口 ====================

    private void drawShowWindow(GuiGraphics g, int mx, int my) {
        com.mojang.blaze3d.systems.RenderSystem.enableBlend();
        com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();
        // 右部分内右对齐(Return: setAlign(RIGHT, TOP))
        com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();
        float sx = RIGHT_W - SHOW_W, sy = 0;
        float areaW = 134, areaH = 134;
        float ax = sx + (SHOW_W - areaW) / 2, ay = sy - 2;


        // 预览内容
        if (!currentViews.isEmpty()) {
            int idx = Math.min(viewIndex, currentViews.size() - 1);
            g.pose().pushPose();
            g.pose().translate(ax, ay, 0);
            // 转换为 area 内坐标(hover 判定用),同时传 area 屏幕偏移与帧缩放(tip 绘制用)
            float areaScreenX = fx + (RIGHT_X + ax) * fscale;
            float areaScreenY = fy + ay * fscale;
            double localMx = (mx - areaScreenX) / fscale;
            double localMy = (my - areaScreenY) / fscale;
            currentViews.get(idx).render(g, areaW, areaH, localMx, localMy, true, areaScreenX, areaScreenY, fscale);
            g.pose().popPose();
            // renderItem 可能重置 blend,重新开启
            com.mojang.blaze3d.systems.RenderSystem.enableBlend();
            com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();
        }

        // 标签页(showWindow 内 x=12, Return setPos(12, 120.75))
        if (currentTut != null) {
            List<ViewGroup> pv = currentTut.getPreview();
            float step = TAG_H - 1;
            float x = sx;
            com.mojang.blaze3d.systems.RenderSystem.enableBlend();
            com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();
            for (int i = 0; i < pv.size(); i++) {
                ViewGroup vg = pv.get(i);
                boolean sel = i == previewIndex;
                float tx = x + i * step;
                boolean hov = mx >= fx + (RIGHT_X + tx) * fscale && mx <= fx + (RIGHT_X + tx + TAG_H) * fscale
                        && my >= fy + 120.75f * fscale && my <= fy + (120.75f + TAG_H) * fscale;
                // 标签图标(11 参 blit 固定采样 48x48,避免 9 参版本 uWidth=绘制宽度导致裁切)
                ResourceLocation icon = vg.getTag().icon;
                g.setColor(1, 1, 1, sel || hov ? 1 : 0.7f);
                GuiRenderHelper.blitTranslucent(g, icon, (int) tx, (int) 120.75f, (int) TAG_H, (int) TAG_H);
                g.setColor(1, 1, 1, 1);
            }
        }

        // 左右箭头(showWindow 内, Return btn pos)
        if (currentViews.size() >= 2) {
            drawArrow(g, BTN_LEFT, sx + 5, 41.75f, 30, 130, 0.4f);
            drawArrow(g, BTN_RIGHT, sx + 140, 41.75f, 30, 130, 0.4f);
        }
    }

    private void drawArrow(GuiGraphics g, ResourceLocation tex, float x, float y, float w, float h, float scale) {
        g.pose().pushPose();
        g.pose().translate(x, y, 0);
        g.pose().scale(scale, scale, 1);
        GuiRenderHelper.blitTranslucent(g, tex, 0, 0, (int) w, (int) h);
        g.pose().popPose();
    }

    // ==================== 简介窗口 ====================

    private void drawRightWindow(GuiGraphics g) {
        com.mojang.blaze3d.systems.RenderSystem.enableBlend();
        com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();
        float wx = RIGHT_W - RIGHT_WIN_W, wy = CENTER_H - RIGHT_WIN_H;

        GuiRenderHelper.blitTranslucent(g, TEX_WINDOW, (int) wx, (int) wy, (int) RIGHT_WIN_W, (int) RIGHT_WIN_H);

        if (currentTut == null) {
            return;
        }
        drawText(g, currentTut.getTitle(), wx + 12, wy + 3, 0xFFFFFFFF, 10);
        int dy = 15;
        for (var s : wrapScaled(currentTut.getBrief(), (int) RIGHT_WIN_W - 24, 9)) {
            drawText(g, s, wx + 12, wy + dy, 0xFFBFC6CC, 9);
            dy += 9;
            if (dy > RIGHT_WIN_H - 8) break;
        }
    }

    // ==================== 交互 ====================

    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        if (btn != 0) return super.mouseClicked(mx, my, btn);

        // 返回按钮
        if (fromTerminal) {
            int bx = (int) (fx - 20 * fscale), by = (int) (fy - 20 * fscale), bs = (int) (18 * fscale);
            if (mx >= bx && mx <= bx + bs && my >= by && my <= by + bs) {
                Minecraft.getInstance().setScreen(new DataTerminalGui());
                return true;
            }
        }

        // 列表项(实时判定,避免误用渲染缓存的 hover 状态)
        int hoveredIdx = findHoveredListIndex(mx, my);
        if (hoveredIdx >= 0) {
            ACTutorial t = hoveredIdx < learned.size() ? learned.get(hoveredIdx) : unlearned.get(hoveredIdx - learned.size());
            selectTutorial(t);
            return true;
        }

        // 标签页
        if (currentTut != null) {
            List<ViewGroup> pv = currentTut.getPreview();
            float step = TAG_H - 1;
            float sx = RIGHT_W - SHOW_W;
            for (int i = 0; i < pv.size(); i++) {
                float tx = sx + i * step;
                if (mx >= fx + (RIGHT_X + tx) * fscale && mx <= fx + (RIGHT_X + tx + TAG_H) * fscale
                        && my >= fy + 120.75f * fscale && my <= fy + (120.75f + TAG_H) * fscale) {
                    previewIndex = i;
                    rebuildViews();
                    return true;
                }
            }

            // 左右箭头
            if (currentViews.size() >= 2) {
                if (isArrowHovered(mx, my, sx + 5, 41.75f)) {
                    cycleView(-1);
                    return true;
                }
                if (isArrowHovered(mx, my, sx + 140, 41.75f)) {
                    cycleView(1);
                    return true;
                }
            }
        }

        return super.mouseClicked(mx, my, btn);
    }

    private boolean isArrowHovered(double mx, double my, float ax, float ay) {
        float w = 30 * 0.4f, h = 130 * 0.4f;
        float x = fx + (RIGHT_X + ax) * fscale, y = fy + ay * fscale;
        return mx >= x && mx <= x + w * fscale && my >= y && my <= y + h * fscale;
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double sx, double sy) {
        // 内容区滚动
        float contentX = fx + (RIGHT_X + 2) * fscale;
        float contentRight = fx + (RIGHT_X + CENTER_W - 2) * fscale;
        if (currentTut != null && mx >= contentX && mx <= contentRight) {
            contentScroll = (int) Math.clamp(contentScroll - sy * 20, 0, maxContentScroll);
            return true;
        }
        return super.mouseScrolled(mx, my, sx, sy);
    }

    /** 按虚拟字号绘制文字(mc.font 基础 16px,Return 字号 8~10) */
    private void drawText(GuiGraphics g, String text, float x, float y, int color, float size) {
        float s = size / 16f;
        g.pose().pushPose();
        g.pose().translate(x, y, 0);
        g.pose().scale(s, s, 1);
        g.drawString(font, text, 0, 0, color);
        g.pose().popPose();
    }

    private boolean isHoveredInFrame(double mx, double my, float x, float y, float w, float h) {
        float rx = fx + x * fscale, ry = fy + y * fscale;
        return mx >= rx && mx <= rx + w * fscale && my >= ry && my <= ry + h * fscale;
    }

    /** 用当前鼠标坐标实时查找命中的列表项索引(learned 在前,unlearned 在后),未命中返回 -1 */
    private int findHoveredListIndex(double mx, double my) {
        int idx = 0;
        for (ACTutorial t : learned) {
            if (isHoveredInFrame(mx, my, 6.6f, 7 + idx * LIST_ITEM_H, LIST_ITEM_W, LIST_ITEM_H)) return idx;
            idx++;
        }
        int base = idx;
        for (ACTutorial t : unlearned) {
            if (isHoveredInFrame(mx, my, 6.6f, 7 + idx * LIST_ITEM_H, LIST_ITEM_W, LIST_ITEM_H)) return idx;
            idx++;
        }
        return -1;
    }

    private void selectTutorial(ACTutorial t) {
        currentTut = t;
        previewIndex = 0;
        contentScroll = 0;
        smoothScroll = 0;
        rebuildViews();
    }

    private void rebuildViews() {
        viewIndex = 0;
        List<ViewGroup> pv = currentTut == null ? List.of() : currentTut.getPreview();
        currentViews = (pv.isEmpty() || previewIndex >= pv.size())
                ? List.of()
                : RecipeViews.buildFor(pv.get(previewIndex));
    }

    private void cycleView(int delta) {
        int n = currentViews.size();
        if (n < 2) return;
        viewIndex = ((viewIndex + delta) % n + n) % n;
    }
}
