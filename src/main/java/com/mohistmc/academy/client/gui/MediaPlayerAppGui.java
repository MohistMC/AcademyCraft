package com.mohistmc.academy.client.gui;

import com.mohistmc.academy.client.MediaPlayerManager;
import com.mohistmc.academy.skill.AcademyAttachments;
import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.terminal.MediaTrack;
import com.mohistmc.academy.terminal.MediaTrackRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MediaPlayerAppGui extends AcademyScreen {

    private static final int GUI_WIDTH = 300;
    private static final int GUI_HEIGHT = 210;
    private static final int TOP_BAR = 28;
    private static final int BACK_BTN_SIZE = 18;
    private static final int TRACK_HEIGHT = 40;
    private static final int TRACK_GAP = 6;
    private static final int CONTROL_BAR_HEIGHT = 32;
    private static final int IMG_SIZE = 32;

    private static final int COLOR_TRACK_PLAYING = 0xFF0a2a3a;
    private static final int COLOR_TRACK_BORDER = 0xFF1e3a5f;
    private static final int COLOR_TRACK_BORDER_PLAYING = 0xFF00bcd4;
    private static final int COLOR_TRACK_HOVER = 0x2200bcd4;
    private static final int COLOR_STOP_BTN = 0xFFe74c3c;
    private static final int COLOR_STOP_BTN_HOVER = 0xFFff6b6b;
    private static final int COLOR_PROGRESS_BG = 0xFF1a2a3a;
    private static final int COLOR_PROGRESS_BAR = 0xFF00bcd4;
    private static final int COLOR_CONTROL_BG = 0xFF0a1628;

    private boolean hoveredBack = false;
    private boolean hoveredStop = false;
    private int hoveredTrack = -1;
    private int animTick = 0;
    private int scrollOffset = 0;

    private List<MediaTrack> visibleTracks = new ArrayList<>();

    public MediaPlayerAppGui() {
        super(Component.translatable("item.academy.app_media_player"));
    }

    @Override
    protected void init() {
        super.init();
        centerGui(GUI_WIDTH, GUI_HEIGHT);
        refreshVisibleTracks();
    }

    @Override
    public void tick() {
        super.tick();
        if (++animTick % 20 == 0) refreshVisibleTracks();
    }

    private void refreshVisibleTracks() {
        visibleTracks.clear();
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        PlayerAbilityData data = player.getData(AcademyAttachments.PLAYER_ABILITY);
        Set<String> loaded = data.getLoadedMedia();
        for (MediaTrack track : MediaTrackRegistry.getAllTracks()) {
            if (loaded.contains(track.trackId())) visibleTracks.add(track);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        pushZ(graphics);

        drawBackground(graphics, AcademyColors.BG);
        graphics.fill(guiLeft, guiTop, guiLeft + GUI_WIDTH, guiTop + TOP_BAR, AcademyColors.BG_PANEL);
        graphics.fill(guiLeft, guiTop + TOP_BAR, guiLeft + GUI_WIDTH, guiTop + TOP_BAR + 1, AcademyColors.ACCENT);

        int backX = guiLeft + 6, backY = guiTop + 5;
        hoveredBack = drawBackButton(graphics, backX, backY, mouseX, mouseY);

        String title = Component.translatable("item.academy.app_media_player").getString();
        graphics.drawString(this.font, title, backX + BACK_BTN_SIZE + 6, guiTop + 9, AcademyColors.TEXT_ACCENT);

        boolean playing = MediaPlayerManager.isPlaying();
        if (playing) {
            float pulse = (float) (0.6 + 0.4 * Math.sin(animTick * 0.1));
            int dotColor = ((int) (pulse * 255) << 24) | 0x00e5ff;
            graphics.fill(guiLeft + GUI_WIDTH - 16, guiTop + 10, guiLeft + GUI_WIDTH - 12, guiTop + 14, dotColor);
        }

        int tracksStartY = guiTop + TOP_BAR + 8;
        int tracksEndY = guiTop + GUI_HEIGHT - CONTROL_BAR_HEIGHT - 4;
        hoveredTrack = -1;
        int textX = guiLeft + 12 + IMG_SIZE + 6;

        graphics.enableScissor(guiLeft + 4, tracksStartY, guiLeft + GUI_WIDTH - 4, tracksEndY);
        graphics.pose().pushPose();
        graphics.pose().translate(0, -scrollOffset, 0);

        if (visibleTracks.isEmpty()) {
            String emptyMsg = "暂无曲目，使用媒体物品加载";
            graphics.drawString(this.font, emptyMsg, guiLeft + (GUI_WIDTH - this.font.width(emptyMsg)) / 2,
                    tracksStartY + (tracksEndY - tracksStartY) / 2 + scrollOffset, AcademyColors.TEXT_MUTED);
        }

        for (int i = 0; i < visibleTracks.size(); i++) {
            MediaTrack track = visibleTracks.get(i);
            int trackY = tracksStartY + i * (TRACK_HEIGHT + TRACK_GAP);

            boolean isHovered = isHovered(guiLeft + 8, trackY, GUI_WIDTH - 16, TRACK_HEIGHT, mouseX, mouseY);
            if (isHovered) hoveredTrack = i;

            boolean isThisPlaying = MediaPlayerManager.isTrackPlaying(track.trackId());
            int bgColor = isThisPlaying ? COLOR_TRACK_PLAYING : (i % 2 == 0 ? AcademyColors.BG_CARD : AcademyColors.BG_CARD_ALT);
            int borderColor = isThisPlaying ? COLOR_TRACK_BORDER_PLAYING : COLOR_TRACK_BORDER;

            graphics.fill(guiLeft + 8, trackY, guiLeft + GUI_WIDTH - 8, trackY + TRACK_HEIGHT, bgColor);
            drawBorder(graphics, guiLeft + 8, trackY, GUI_WIDTH - 16, TRACK_HEIGHT, borderColor);

            if (isHovered && !isThisPlaying)
                graphics.fill(guiLeft + 9, trackY + 1, guiLeft + GUI_WIDTH - 9, trackY + TRACK_HEIGHT - 1, COLOR_TRACK_HOVER);

            int imgX = guiLeft + 12, imgY = trackY + (TRACK_HEIGHT - IMG_SIZE) / 2;
            graphics.blit(track.texture(), imgX, imgY, 0, 0, IMG_SIZE, IMG_SIZE, IMG_SIZE, IMG_SIZE);

            if (isThisPlaying)
                drawBorder(graphics, imgX - 1, imgY - 1, IMG_SIZE + 2, IMG_SIZE + 2, COLOR_TRACK_BORDER_PLAYING);

            String trackName = Component.translatable(track.nameKey()).getString();
            graphics.drawString(this.font, trackName, textX, trackY + 6, isThisPlaying ? AcademyColors.TEXT_ACCENT : AcademyColors.TEXT);

            graphics.drawString(this.font, Component.translatable(track.descKey()).getString(), textX, trackY + 18, AcademyColors.TEXT_SECONDARY);

            String tagLabel = track.tag();
            int tagW = this.font.width(tagLabel) + 6, tagX = guiLeft + GUI_WIDTH - 8 - tagW - 4;
            graphics.fill(tagX, trackY + 4, tagX + tagW, trackY + 16, 0xFF1a3050);
            drawBorder(graphics, tagX, trackY + 4, tagW, 12, AcademyColors.SEPARATOR);
            graphics.drawString(this.font, tagLabel, tagX + 3, trackY + 6, AcademyColors.TEXT_ACCENT);

            String durStr = MediaPlayerManager.formatTime(track.durationSeconds());
            graphics.drawString(this.font, durStr, guiLeft + GUI_WIDTH - this.font.width(durStr) - 16,
                    trackY + TRACK_HEIGHT - 14, AcademyColors.TEXT_MUTED);

            if (isThisPlaying) {
                int barX = textX, barY = trackY + TRACK_HEIGHT - 6, barW = guiLeft + GUI_WIDTH - 16 - textX;
                graphics.fill(barX, barY, barX + barW, barY + 3, COLOR_PROGRESS_BG);
                graphics.fill(barX, barY, barX + (int) (barW * MediaPlayerManager.getProgress()), barY + 3, COLOR_PROGRESS_BAR);

                String timeStr = MediaPlayerManager.formatTime(MediaPlayerManager.getElapsedSeconds()) + " / " + durStr;
                graphics.drawString(this.font, timeStr, guiLeft + GUI_WIDTH - this.font.width(timeStr) - 16, barY - 8, AcademyColors.TEXT_MUTED);
            } else if (isHovered) {
                graphics.drawString(this.font, "点击播放", textX, trackY + TRACK_HEIGHT - 14, AcademyColors.TEXT_MUTED);
            }
        }

        graphics.pose().popPose();
        graphics.disableScissor();

        int maxScroll = Math.max(0, visibleTracks.size() * (TRACK_HEIGHT + TRACK_GAP) - (tracksEndY - tracksStartY));
        if (maxScroll > 0) {
            int scrollBarH = tracksEndY - tracksStartY - 4;
            int thumbH = Math.max(10, scrollBarH * scrollBarH / (visibleTracks.size() * (TRACK_HEIGHT + TRACK_GAP)));
            int thumbY = tracksStartY + 2 + (scrollBarH - thumbH) * scrollOffset / maxScroll;
            graphics.fill(guiLeft + GUI_WIDTH - 5, tracksStartY + 2, guiLeft + GUI_WIDTH - 3, tracksStartY + 2 + scrollBarH, 0x44FFFFFF);
            graphics.fill(guiLeft + GUI_WIDTH - 5, thumbY, guiLeft + GUI_WIDTH - 3, thumbY + thumbH, 0x8800bcd4);
        }

        int controlY = guiTop + GUI_HEIGHT - CONTROL_BAR_HEIGHT;
        graphics.fill(guiLeft, controlY, guiLeft + GUI_WIDTH, guiTop + GUI_HEIGHT, COLOR_CONTROL_BG);
        graphics.fill(guiLeft, controlY, guiLeft + GUI_WIDTH, controlY + 1, AcademyColors.SEPARATOR);

        if (playing) {
            String currentTrackName = "";
            MediaTrack current = MediaTrackRegistry.getTrack(MediaPlayerManager.getCurrentTrack());
            if (current != null) currentTrackName = Component.translatable(current.nameKey()).getString();
            graphics.drawString(this.font, "正在播放: " + currentTrackName, guiLeft + 10, controlY + 6, AcademyColors.TEXT_ACCENT);
            graphics.drawString(this.font, MediaPlayerManager.formatTime(MediaPlayerManager.getElapsedSeconds()),
                    guiLeft + 10, controlY + 18, AcademyColors.TEXT_MUTED);

            int stopBtnX = guiLeft + GUI_WIDTH - 50, stopBtnY = controlY + 6;
            hoveredStop = isHovered(stopBtnX, stopBtnY, 40, 20, mouseX, mouseY);
            graphics.fill(stopBtnX, stopBtnY, stopBtnX + 40, stopBtnY + 20,
                    hoveredStop ? COLOR_STOP_BTN_HOVER : COLOR_STOP_BTN);
            drawBorder(graphics, stopBtnX, stopBtnY, 40, 20, AcademyColors.ACCENT);
            graphics.drawString(this.font, "停止", stopBtnX + (40 - this.font.width("停止")) / 2, stopBtnY + 6, AcademyColors.TEXT);
        } else {
            String hint = visibleTracks.isEmpty() ? "右键使用媒体物品以加载曲目" : "选择曲目开始播放";
            graphics.drawString(this.font, hint, guiLeft + (GUI_WIDTH - this.font.width(hint)) / 2, controlY + 12, AcademyColors.TEXT_MUTED);
            hoveredStop = false;
        }

        popZ(graphics);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (hoveredBack && button == 0) {
            Minecraft.getInstance().setScreen(new DataTerminalGui());
            return true;
        }
        if (hoveredStop && button == 0) {
            MediaPlayerManager.stop();
            return true;
        }
        if (hoveredTrack >= 0 && button == 0) {
            String trackId = visibleTracks.get(hoveredTrack).trackId();
            if (MediaPlayerManager.isTrackPlaying(trackId)) MediaPlayerManager.stop();
            else MediaPlayerManager.play(trackId);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int tracksStartY = guiTop + TOP_BAR + 8;
        int tracksEndY = guiTop + GUI_HEIGHT - CONTROL_BAR_HEIGHT - 4;
        int maxScroll = Math.max(0, visibleTracks.size() * (TRACK_HEIGHT + TRACK_GAP) - (tracksEndY - tracksStartY));
        scrollOffset = (int) Math.clamp(scrollOffset - scrollY * 10, 0, maxScroll);
        return true;
    }
}
