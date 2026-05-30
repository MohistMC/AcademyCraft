package com.mohistmc.academy.client.gui;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.client.MediaPlayerManager;
import com.mohistmc.academy.skill.AcademyAttachments;
import com.mohistmc.academy.skill.PlayerAbilityData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Mgazul
 * @date 2026/5/31 03:37
 */
@OnlyIn(Dist.CLIENT)
public class MediaPlayerAppGui extends Screen {

    private static final int GUI_WIDTH = 300;
    private static final int GUI_HEIGHT = 210;
    private static final int TOP_BAR = 28;
    private static final int BACK_BTN_SIZE = 18;
    private static final int TRACK_HEIGHT = 40;
    private static final int TRACK_GAP = 6;
    private static final int CONTROL_BAR_HEIGHT = 32;
    private static final int IMG_SIZE = 32;

    private static final int COLOR_BG = 0xE0080818;
    private static final int COLOR_TOP_BAR = 0xFF0a1628;
    private static final int COLOR_ACCENT = 0xFF00bcd4;
    private static final int COLOR_TRACK_BG_EVEN = 0xFF101828;
    private static final int COLOR_TRACK_BG_ODD = 0xFF141e30;
    private static final int COLOR_TRACK_PLAYING = 0xFF0a2a3a;
    private static final int COLOR_TRACK_BORDER = 0xFF1e3a5f;
    private static final int COLOR_TRACK_BORDER_PLAYING = 0xFF00bcd4;
    private static final int COLOR_TRACK_HOVER = 0x2200bcd4;
    private static final int COLOR_TEXT_WHITE = 0xFFFFFFFF;
    private static final int COLOR_TEXT_CYAN = 0xFF00e5ff;
    private static final int COLOR_TEXT_GRAY = 0xFF888899;
    private static final int COLOR_TEXT_DIM = 0xFF555566;
    private static final int COLOR_TEXT_GREEN = 0xFF2ecc71;
    private static final int COLOR_BACK_BG = 0xFF162040;
    private static final int COLOR_BACK_HOVER = 0xFF00bcd4;
    private static final int COLOR_PROGRESS_BG = 0xFF1a2a3a;
    private static final int COLOR_PROGRESS_BAR = 0xFF00bcd4;
    private static final int COLOR_CONTROL_BG = 0xFF0a1628;
    private static final int COLOR_STOP_BTN = 0xFFe74c3c;
    private static final int COLOR_STOP_BTN_HOVER = 0xFFff6b6b;
    private static final int COLOR_SEPARATOR = 0xFF004d5a;

    private static final ResourceLocation MEDIA_RAILGUN_TEX =
            ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/media/only_my_railgun.png");
    private static final ResourceLocation MEDIA_JUDGELIGHT_TEX =
            ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/media/level5_judgelight.png");
    private static final ResourceLocation MEDIA_NOISE_TEX =
            ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/media/sisters_noise.png");

    private static final String[][] ALL_TRACKS = {
            {"only_my_railgun", "item.academy.media_only_my_railgun", "item.academy.media_only_my_railgun.desc", "OP1"},
            {"level5_judgelight", "item.academy.media_level5_judgelight", "item.academy.media_level5_judgelight.desc", "OP2"},
            {"sisters_noise", "item.academy.media_sisters_noise", "item.academy.media_sisters_noise.desc", "OPs"},
    };

    private int guiLeft;
    private int guiTop;
    private int hoveredTrack = -1;
    private boolean hoveredBack = false;
    private boolean hoveredStop = false;
    private int animTick = 0;

    private List<String[]> visibleTracks = new ArrayList<>();

    public MediaPlayerAppGui() {
        super(Component.translatable("item.academy.app_media_player"));
    }

    @Override
    protected void init() {
        super.init();
        this.guiLeft = (this.width - GUI_WIDTH) / 2;
        this.guiTop = (this.height - GUI_HEIGHT) / 2;
        refreshVisibleTracks();
    }

    @Override
    public void tick() {
        super.tick();
        animTick++;
        if (animTick % 20 == 0) {
            refreshVisibleTracks();
        }
    }

    private void refreshVisibleTracks() {
        visibleTracks.clear();
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        PlayerAbilityData data = player.getData(AcademyAttachments.PLAYER_ABILITY);
        Set<String> loaded = data.getLoadedMedia();
        for (String[] track : ALL_TRACKS) {
            if (loaded.contains(track[0])) {
                visibleTracks.add(track);
            }
        }
    }

    private ResourceLocation getMediaTexture(String trackId) {
        return switch (trackId) {
            case "only_my_railgun" -> MEDIA_RAILGUN_TEX;
            case "level5_judgelight" -> MEDIA_JUDGELIGHT_TEX;
            case "sisters_noise" -> MEDIA_NOISE_TEX;
            default -> MEDIA_RAILGUN_TEX;
        };
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

        String title = Component.translatable("item.academy.app_media_player").getString();
        int titleX = backX + BACK_BTN_SIZE + 6;
        graphics.drawString(this.font, title, titleX, guiTop + 9, COLOR_TEXT_CYAN);

        boolean playing = MediaPlayerManager.isPlaying();
        if (playing) {
            float pulse = (float) (0.6 + 0.4 * Math.sin(animTick * 0.1));
            int pulseAlpha = (int) (pulse * 255);
            int dotColor = (pulseAlpha << 24) | 0x00e5ff;
            graphics.fill(guiLeft + GUI_WIDTH - 16, guiTop + 10, guiLeft + GUI_WIDTH - 12, guiTop + 14, dotColor);
        }

        int tracksStartY = guiTop + TOP_BAR + 8;
        hoveredTrack = -1;

        int textX = guiLeft + 12 + IMG_SIZE + 6;

        if (visibleTracks.isEmpty()) {
            String emptyMsg = "暂无曲目，使用媒体物品加载";
            int mw = this.font.width(emptyMsg);
            int emptyY = guiTop + (GUI_HEIGHT - CONTROL_BAR_HEIGHT) / 2;
            graphics.drawString(this.font, emptyMsg, guiLeft + (GUI_WIDTH - mw) / 2, emptyY, COLOR_TEXT_DIM);
        }

        for (int i = 0; i < visibleTracks.size(); i++) {
            String[] track = visibleTracks.get(i);
            String trackId = track[0];
            int trackY = tracksStartY + i * (TRACK_HEIGHT + TRACK_GAP);

            boolean isHovered = mouseX >= guiLeft + 8 && mouseX < guiLeft + GUI_WIDTH - 8
                    && mouseY >= trackY && mouseY < trackY + TRACK_HEIGHT;
            if (isHovered) hoveredTrack = i;

            boolean isThisPlaying = MediaPlayerManager.isTrackPlaying(trackId);

            int bgColor = isThisPlaying ? COLOR_TRACK_PLAYING
                    : (i % 2 == 0 ? COLOR_TRACK_BG_EVEN : COLOR_TRACK_BG_ODD);
            int borderColor = isThisPlaying ? COLOR_TRACK_BORDER_PLAYING : COLOR_TRACK_BORDER;

            graphics.fill(guiLeft + 8, trackY, guiLeft + GUI_WIDTH - 8, trackY + TRACK_HEIGHT, bgColor);
            drawBorder(graphics, guiLeft + 8, trackY, GUI_WIDTH - 16, TRACK_HEIGHT, borderColor);

            if (isHovered && !isThisPlaying) {
                graphics.fill(guiLeft + 9, trackY + 1, guiLeft + GUI_WIDTH - 9, trackY + TRACK_HEIGHT - 1, COLOR_TRACK_HOVER);
            }

            int imgX = guiLeft + 12;
            int imgY = trackY + (TRACK_HEIGHT - IMG_SIZE) / 2;
            ResourceLocation mediaTex = getMediaTexture(trackId);
            graphics.blit(mediaTex, imgX, imgY, 0, 0, IMG_SIZE, IMG_SIZE, IMG_SIZE, IMG_SIZE);

            if (isThisPlaying) {
                drawBorder(graphics, imgX - 1, imgY - 1, IMG_SIZE + 2, IMG_SIZE + 2, COLOR_TRACK_BORDER_PLAYING);
            }

            String trackName = Component.translatable(track[1]).getString();
            graphics.drawString(this.font, trackName, textX, trackY + 6,
                    isThisPlaying ? COLOR_TEXT_CYAN : COLOR_TEXT_WHITE);

            String trackDesc = Component.translatable(track[2]).getString();
            graphics.drawString(this.font, trackDesc, textX, trackY + 18, COLOR_TEXT_GRAY);

            String tagLabel = track[3];
            int tagW = this.font.width(tagLabel) + 6;
            int tagX = guiLeft + GUI_WIDTH - 8 - tagW - 4;
            graphics.fill(tagX, trackY + 4, tagX + tagW, trackY + 16, 0xFF1a3050);
            drawBorder(graphics, tagX, trackY + 4, tagW, 12, COLOR_SEPARATOR);
            graphics.drawString(this.font, tagLabel, tagX + 3, trackY + 6, COLOR_TEXT_CYAN);

            int duration = switch (trackId) {
                case "only_my_railgun" -> MediaPlayerManager.DURATION_RAILGUN;
                case "level5_judgelight" -> MediaPlayerManager.DURATION_JUDGELIGHT;
                case "sisters_noise" -> MediaPlayerManager.DURATION_NOISE;
                default -> 60;
            };
            String durStr = MediaPlayerManager.formatTime(duration);
            int durW = this.font.width(durStr);
            graphics.drawString(this.font, durStr, guiLeft + GUI_WIDTH - durW - 16, trackY + TRACK_HEIGHT - 14, COLOR_TEXT_DIM);

            if (isThisPlaying) {
                int barX = textX;
                int barY = trackY + TRACK_HEIGHT - 6;
                int barW = guiLeft + GUI_WIDTH - 16 - textX;
                int barH = 3;
                graphics.fill(barX, barY, barX + barW, barY + barH, COLOR_PROGRESS_BG);
                float progress = MediaPlayerManager.getProgress();
                int fillW = (int) (barW * progress);
                graphics.fill(barX, barY, barX + fillW, barY + barH, COLOR_PROGRESS_BAR);

                int elapsed = MediaPlayerManager.getElapsedSeconds();
                String timeStr = MediaPlayerManager.formatTime(elapsed) + " / " + durStr;
                graphics.drawString(this.font, timeStr, guiLeft + GUI_WIDTH - this.font.width(timeStr) - 16, barY - 8, COLOR_TEXT_DIM);
            } else if (isHovered) {
                String hint = "点击播放";
                graphics.drawString(this.font, hint, textX, trackY + TRACK_HEIGHT - 14, COLOR_TEXT_DIM);
            }
        }

        int controlY = guiTop + GUI_HEIGHT - CONTROL_BAR_HEIGHT;
        graphics.fill(guiLeft, controlY, guiLeft + GUI_WIDTH, guiTop + GUI_HEIGHT, COLOR_CONTROL_BG);
        graphics.fill(guiLeft, controlY, guiLeft + GUI_WIDTH, controlY + 1, COLOR_SEPARATOR);

        if (playing) {
            String currentTrackName = "";
            for (String[] track : ALL_TRACKS) {
                if (MediaPlayerManager.isTrackPlaying(track[0])) {
                    currentTrackName = Component.translatable(track[1]).getString();
                    break;
                }
            }
            String nowPlaying = "正在播放: " + currentTrackName;
            graphics.drawString(this.font, nowPlaying, guiLeft + 10, controlY + 6, COLOR_TEXT_CYAN);

            int elapsed = MediaPlayerManager.getElapsedSeconds();
            String elapsedStr = MediaPlayerManager.formatTime(elapsed);
            graphics.drawString(this.font, elapsedStr, guiLeft + 10, controlY + 18, COLOR_TEXT_DIM);

            int stopBtnX = guiLeft + GUI_WIDTH - 50;
            int stopBtnY = controlY + 6;
            int stopBtnW = 40;
            int stopBtnH = 20;
            hoveredStop = mouseX >= stopBtnX && mouseX < stopBtnX + stopBtnW
                    && mouseY >= stopBtnY && mouseY < stopBtnY + stopBtnH;

            graphics.fill(stopBtnX, stopBtnY, stopBtnX + stopBtnW, stopBtnY + stopBtnH,
                    hoveredStop ? COLOR_STOP_BTN_HOVER : COLOR_STOP_BTN);
            drawBorder(graphics, stopBtnX, stopBtnY, stopBtnW, stopBtnH, COLOR_ACCENT);
            String stopText = "停止";
            int stw = this.font.width(stopText);
            graphics.drawString(this.font, stopText, stopBtnX + (stopBtnW - stw) / 2, stopBtnY + 6, COLOR_TEXT_WHITE);
        } else {
            String hint = visibleTracks.isEmpty() ? "右键使用媒体物品以加载曲目" : "选择曲目开始播放";
            int hw = this.font.width(hint);
            graphics.drawString(this.font, hint, guiLeft + (GUI_WIDTH - hw) / 2, controlY + 12, COLOR_TEXT_DIM);
            hoveredStop = false;
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

        if (hoveredStop && button == 0) {
            MediaPlayerManager.stop();
            return true;
        }

        if (hoveredTrack >= 0 && hoveredTrack < visibleTracks.size() && button == 0) {
            String trackId = visibleTracks.get(hoveredTrack)[0];
            if (MediaPlayerManager.isTrackPlaying(trackId)) {
                MediaPlayerManager.stop();
            } else {
                MediaPlayerManager.play(trackId);
            }
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void removed() {
        super.removed();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
