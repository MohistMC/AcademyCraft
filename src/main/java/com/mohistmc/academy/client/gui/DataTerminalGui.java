package com.mohistmc.academy.client.gui;

import com.mohistmc.academy.skill.AcademyAttachments;
import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.terminal.AppRegistry;
import com.mohistmc.academy.terminal.TerminalApp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DataTerminalGui extends Screen {

    private static final int GUI_WIDTH = 320;
    private static final int GUI_HEIGHT = 220;
    private static final int TOP_BAR_HEIGHT = 32;
    private static final int BOTTOM_BAR_HEIGHT = 18;
    private static final int APP_ICON_SIZE = 48;
    private static final int APP_GAP = 16;
    private static final int APP_COLS = 4;

    private static final int COLOR_BG = 0xE0080818;
    private static final int COLOR_TOP_BAR = 0xFF0a1628;
    private static final int COLOR_BOTTOM_BAR = 0xFF0a1628;
    private static final int COLOR_ACCENT = 0xFF00bcd4;
    private static final int COLOR_APP_BG = 0xFF162040;
    private static final int COLOR_APP_BORDER = 0xFF1e3a5f;
    private static final int COLOR_APP_HOVER = 0xFF1a4a6e;
    private static final int COLOR_APP_HOVER_BORDER = 0xFF00bcd4;
    private static final int COLOR_TEXT_WHITE = 0xFFFFFFFF;
    private static final int COLOR_TEXT_CYAN = 0xFF00e5ff;
    private static final int COLOR_TEXT_GRAY = 0xFF888899;
    private static final int COLOR_TEXT_DIM = 0xFF555566;
    private static final int COLOR_SCANLINE = 0x08FFFFFF;
    private static final int COLOR_SEPARATOR = 0xFF004d5a;

    private static final String[][] OPENING_MESSAGES = {
            {"正在连接学园都市数据中心...", "连接成功。"},
            {"初始化数据终端协议...", "协议就绪。"},
            {"载入用户配置文件...", "配置完成。"},
            {"同步虚能网络状态...", "同步完毕。"},
            {"校验终端安全证书...", "验证通过。"},
    };

    private int guiLeft;
    private int guiTop;
    private int hoveredApp = -1;
    private int openAnimTicks = 0;
    private boolean animDone = false;
    private int messageIndex;
    private final List<AppEntry> appEntries = new ArrayList<>();

    public DataTerminalGui() {
        super(Component.translatable("gui.academy.data_terminal"));
        this.messageIndex = new Random().nextInt(OPENING_MESSAGES.length);
    }

    public DataTerminalGui(boolean skipAnim) {
        super(Component.translatable("gui.academy.data_terminal"));
        this.messageIndex = 0;
        if (skipAnim) {
            this.animDone = true;
        }
    }

    @Override
    protected void init() {
        super.init();
        this.guiLeft = (this.width - GUI_WIDTH) / 2;
        this.guiTop = (this.height - GUI_HEIGHT) / 2;
        bindBuiltinRoutes();
        buildAppEntries();
    }

    private static boolean routesBound = false;

    private static void bindBuiltinRoutes() {
        if (routesBound) return;
        routesBound = true;
        AppRegistry.bindOpenAction(AppRegistry.SKILL_TREE, mc -> mc.setScreen(new DevNormalGui(true)));
        AppRegistry.bindOpenAction(AppRegistry.SETTINGS, mc -> mc.setScreen(new SettingsAppGui()));
        AppRegistry.bindOpenAction(AppRegistry.TUTORIAL, mc -> mc.setScreen(new TutorialAppGui(true)));
        AppRegistry.bindOpenAction(AppRegistry.MEDIA_PLAYER, mc -> mc.setScreen(new MediaPlayerAppGui()));
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);
        this.guiLeft = (this.width - GUI_WIDTH) / 2;
        this.guiTop = (this.height - GUI_HEIGHT) / 2;
    }

    private void buildAppEntries() {
        appEntries.clear();
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        PlayerAbilityData data = mc.player.getData(AcademyAttachments.PLAYER_ABILITY);

        for (TerminalApp app : AppRegistry.getAllApps()) {
            if (data.hasApp(app.getAppId())) {
                appEntries.add(new AppEntry(app.getAppId(), app.getNameKey(), app.getIcon()));
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!animDone) {
            openAnimTicks++;
            if (openAnimTicks >= 30) {
                animDone = true;
            }
        }
        if (appEntries.isEmpty()) {
            buildAppEntries();
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 300);

        graphics.fill(guiLeft, guiTop, guiLeft + GUI_WIDTH, guiTop + GUI_HEIGHT, COLOR_BG);

        if (!animDone) {
            drawOpeningAnimation(graphics);
        } else {
            drawTopBar(graphics);
            drawAppGrid(graphics, mouseX, mouseY);
            drawBottomBar(graphics);
            drawDecorations(graphics);
        }

        graphics.pose().popPose();
    }

    private void drawOpeningAnimation(GuiGraphics graphics) {
        float progress = openAnimTicks / 30f;

        int barWidth = (int) ((GUI_WIDTH - 40) * progress);
        int barX = guiLeft + 20;
        int barY = guiTop + GUI_HEIGHT / 2 - 4;

        graphics.fill(guiLeft, guiTop + TOP_BAR_HEIGHT, guiLeft + GUI_WIDTH, guiTop + TOP_BAR_HEIGHT + 1, COLOR_ACCENT);
        graphics.fill(guiLeft, guiTop + GUI_HEIGHT - BOTTOM_BAR_HEIGHT - 1, guiLeft + GUI_WIDTH, guiTop + GUI_HEIGHT - BOTTOM_BAR_HEIGHT, COLOR_ACCENT);

        graphics.fill(barX, barY, barX + (GUI_WIDTH - 40), barY + 8, 0xFF1a1a2e);
        graphics.fill(barX, barY, barX + barWidth, barY + 8, COLOR_ACCENT);

        String[] msgs = OPENING_MESSAGES[messageIndex];
        String msg = progress < 0.5f ? msgs[0] : msgs[1];
        int tw = this.font.width(msg);
        graphics.drawString(this.font, msg, guiLeft + (GUI_WIDTH - tw) / 2, barY - 16, COLOR_TEXT_CYAN);

        String percent = String.format("%.0f%%", progress * 100);
        int pw = this.font.width(percent);
        graphics.drawString(this.font, percent, guiLeft + (GUI_WIDTH - pw) / 2, barY + 14, COLOR_TEXT_GRAY);

        for (int i = 0; i < 3; i++) {
            int dotX = guiLeft + GUI_WIDTH / 2 - 12 + i * 12;
            int dotY = barY + 28;
            float dotAlpha = (float) (0.3 + 0.7 * Math.abs(Math.sin((openAnimTicks + i * 5) * 0.2)));
            int dotColor = (int) (dotAlpha * 255) << 24 | 0x00bcd4;
            graphics.fill(dotX, dotY, dotX + 4, dotY + 4, dotColor);
        }
    }

    private void drawTopBar(GuiGraphics graphics) {
        graphics.fill(guiLeft, guiTop, guiLeft + GUI_WIDTH, guiTop + TOP_BAR_HEIGHT, COLOR_TOP_BAR);
        graphics.fill(guiLeft, guiTop + TOP_BAR_HEIGHT, guiLeft + GUI_WIDTH, guiTop + TOP_BAR_HEIGHT + 1, COLOR_ACCENT);

        String title = "§lDATA TERMINAL";
        graphics.drawString(this.font, title, guiLeft + 8, guiTop + 6, COLOR_TEXT_CYAN);

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            PlayerAbilityData data = mc.player.getData(AcademyAttachments.PLAYER_ABILITY);
            String playerName = mc.player.getName().getString();
            String levelStr = data.hasAbility()
                    ? " Lv." + data.getPlayerLevel()
                    : " N/A";
            String info = playerName + levelStr;
            int infoW = this.font.width(info);
            graphics.drawString(this.font, info, guiLeft + GUI_WIDTH - infoW - 8, guiTop + 6, COLOR_TEXT_GRAY);

            String abilityStr = data.hasAbility()
                    ? Component.translatable("item.academy.factor_" + data.getCurrentAbility().id()).getString()
                    : "无能力";
            int abW = this.font.width(abilityStr);
            graphics.drawString(this.font, abilityStr, guiLeft + GUI_WIDTH - abW - 8, guiTop + 18, COLOR_TEXT_DIM);
        }

        String version = "v1.0.0";
        int vw = this.font.width(version);
        graphics.drawString(this.font, version, guiLeft + (GUI_WIDTH - vw) / 2, guiTop + 18, COLOR_TEXT_DIM);
    }

    private void drawAppGrid(GuiGraphics graphics, int mouseX, int mouseY) {
        int totalCols = Math.min(APP_COLS, appEntries.size());
        int totalW = totalCols * APP_ICON_SIZE + (totalCols - 1) * APP_GAP;
        int startX = guiLeft + (GUI_WIDTH - totalW) / 2;
        int startY = guiTop + TOP_BAR_HEIGHT + 20;

        hoveredApp = -1;

        for (int i = 0; i < appEntries.size(); i++) {
            AppEntry entry = appEntries.get(i);
            int col = i % APP_COLS;
            int row = i / APP_COLS;
            int x = startX + col * (APP_ICON_SIZE + APP_GAP);
            int y = startY + row * (APP_ICON_SIZE + APP_GAP + 14);

            boolean isHovered = mouseX >= x && mouseX < x + APP_ICON_SIZE
                    && mouseY >= y && mouseY < y + APP_ICON_SIZE;
            if (isHovered) hoveredApp = i;

            int bgColor = isHovered ? COLOR_APP_HOVER : COLOR_APP_BG;
            int borderColor = isHovered ? COLOR_APP_HOVER_BORDER : COLOR_APP_BORDER;
            graphics.fill(x, y, x + APP_ICON_SIZE, y + APP_ICON_SIZE, bgColor);
            drawBorder(graphics, x, y, APP_ICON_SIZE, APP_ICON_SIZE, borderColor);

            if (isHovered) {
                graphics.fill(x + 2, y + 2, x + APP_ICON_SIZE - 2, y + APP_ICON_SIZE - 2, 0x2200bcd4);
            }

            ResourceLocation icon = entry.icon;
            if (icon != null) {
                int drawSize = 32;
                int iconX = x + (APP_ICON_SIZE - drawSize) / 2;
                int iconY = y + (APP_ICON_SIZE - drawSize) / 2;
                graphics.blit(icon, iconX, iconY, 0, 0, drawSize, drawSize, drawSize, drawSize);
            }

            String name = Component.translatable(entry.nameKey).getString();
            int nameW = this.font.width(name);
            int nameColor = isHovered ? COLOR_TEXT_CYAN : COLOR_TEXT_WHITE;
            graphics.drawString(this.font, name, x + (APP_ICON_SIZE - nameW) / 2, y + APP_ICON_SIZE + 4, nameColor);
        }
    }

    private void drawBottomBar(GuiGraphics graphics) {
        graphics.fill(guiLeft, guiTop + GUI_HEIGHT - BOTTOM_BAR_HEIGHT, guiLeft + GUI_WIDTH, guiTop + GUI_HEIGHT, COLOR_BOTTOM_BAR);
        graphics.fill(guiLeft, guiTop + GUI_HEIGHT - BOTTOM_BAR_HEIGHT, guiLeft + GUI_WIDTH, guiTop + GUI_HEIGHT - BOTTOM_BAR_HEIGHT + 1, COLOR_SEPARATOR);

        PlayerAbilityData data = Minecraft.getInstance().player != null
                ? Minecraft.getInstance().player.getData(AcademyAttachments.PLAYER_ABILITY) : null;
        int appCount = data != null ? data.getInstalledApps().size() : 0;
        String status = "APP: " + appCount + " 已安装";
        graphics.drawString(this.font, status, guiLeft + 8, guiTop + GUI_HEIGHT - BOTTOM_BAR_HEIGHT + 5, COLOR_TEXT_DIM);

        String hint = "点击APP打开  |  ESC关闭终端";
        int hw = this.font.width(hint);
        graphics.drawString(this.font, hint, guiLeft + GUI_WIDTH - hw - 8, guiTop + GUI_HEIGHT - BOTTOM_BAR_HEIGHT + 5, COLOR_TEXT_DIM);
    }

    private void drawDecorations(GuiGraphics graphics) {
        for (int i = 0; i < GUI_HEIGHT; i += 4) {
            if ((i / 4) % 2 == 0) {
                graphics.fill(guiLeft, guiTop + i, guiLeft + GUI_WIDTH, guiTop + i + 1, COLOR_SCANLINE);
            }
        }

        int cornerLen = 6;
        graphics.fill(guiLeft, guiTop, guiLeft + cornerLen, guiTop + 1, COLOR_ACCENT);
        graphics.fill(guiLeft, guiTop, guiLeft + 1, guiTop + cornerLen, COLOR_ACCENT);

        graphics.fill(guiLeft + GUI_WIDTH - cornerLen, guiTop, guiLeft + GUI_WIDTH, guiTop + 1, COLOR_ACCENT);
        graphics.fill(guiLeft + GUI_WIDTH - 1, guiTop, guiLeft + GUI_WIDTH, guiTop + cornerLen, COLOR_ACCENT);

        graphics.fill(guiLeft, guiTop + GUI_HEIGHT - 1, guiLeft + cornerLen, guiTop + GUI_HEIGHT, COLOR_ACCENT);
        graphics.fill(guiLeft, guiTop + GUI_HEIGHT - cornerLen, guiLeft + 1, guiTop + GUI_HEIGHT, COLOR_ACCENT);

        graphics.fill(guiLeft + GUI_WIDTH - cornerLen, guiTop + GUI_HEIGHT - 1, guiLeft + GUI_WIDTH, guiTop + GUI_HEIGHT, COLOR_ACCENT);
        graphics.fill(guiLeft + GUI_WIDTH - 1, guiTop + GUI_HEIGHT - cornerLen, guiLeft + GUI_WIDTH, guiTop + GUI_HEIGHT, COLOR_ACCENT);

        graphics.fill(guiLeft, guiTop, guiLeft + 1, guiTop + GUI_HEIGHT, 0x2200bcd4);
        graphics.fill(guiLeft + GUI_WIDTH - 1, guiTop, guiLeft + GUI_WIDTH, guiTop + GUI_HEIGHT, 0x2200bcd4);
    }

    private void drawBorder(GuiGraphics graphics, int x, int y, int w, int h, int color) {
        graphics.fill(x, y, x + w, y + 1, color);
        graphics.fill(x, y + h - 1, x + w, y + h, color);
        graphics.fill(x, y, x + 1, y + h, color);
        graphics.fill(x + w - 1, y, x + w, y + h, color);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!animDone) {
            animDone = true;
            return true;
        }

        if (hoveredApp >= 0 && button == 0) {
            openApp(appEntries.get(hoveredApp).appId);
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void openApp(String appId) {
        Minecraft mc = Minecraft.getInstance();
        TerminalApp app = AppRegistry.getApp(appId);
        if (app != null) {
            app.open(mc);
        } else if (mc.player != null) {
            mc.player.displayClientMessage(
                    Component.literal("§7[数据终端] §c该APP暂未实现: " + appId), true);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private record AppEntry(String appId, String nameKey, ResourceLocation icon) {
    }
}
