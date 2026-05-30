package com.mohistmc.academy.client;

import com.mohistmc.academy.world.AcademySounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * @author Mgazul
 * @date 2026/5/31 03:36
 */
@OnlyIn(Dist.CLIENT)
public class MediaPlayerManager {

    private static String currentTrack = null;
    private static boolean isPlaying = false;
    private static SoundInstance currentSoundInstance = null;
    private static int playStartTick = 0;
    private static int currentDurationTicks = 0;

    public static final int DURATION_RAILGUN = 99;
    public static final int DURATION_JUDGELIGHT = 102;
    public static final int DURATION_NOISE = 95;

    public static void play(String trackId) {
        stop();

        Minecraft mc = Minecraft.getInstance();
        if (mc.getSoundManager() == null) return;

        mc.getMusicManager().stopPlaying();

        SoundEvent soundEvent = getSoundEvent(trackId);
        if (soundEvent == null) return;

        currentSoundInstance = new SimpleSoundInstance(
                soundEvent.getLocation(),
                SoundSource.MASTER,
                1.0f,
                1.0f,
                RandomSource.create(),
                false,
                0,
                SoundInstance.Attenuation.NONE,
                0.0, 0.0, 0.0,
                true
        );
        mc.getSoundManager().play(currentSoundInstance);

        currentTrack = trackId;
        isPlaying = true;
        playStartTick = mc.player != null ? mc.player.tickCount : 0;
        currentDurationTicks = getDurationTicks(trackId);
    }

    public static void stop() {
        if (currentSoundInstance != null) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.getSoundManager() != null) {
                mc.getSoundManager().stop(currentSoundInstance);
            }
        }
        currentSoundInstance = null;
        currentTrack = null;
        isPlaying = false;
        playStartTick = 0;
        currentDurationTicks = 0;
    }

    public static String getCurrentTrack() {
        return currentTrack;
    }

    public static boolean isPlaying() {
        if (isPlaying && currentSoundInstance != null) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.getSoundManager() != null && !mc.getSoundManager().isActive(currentSoundInstance)) {
                isPlaying = false;
                currentTrack = null;
                currentSoundInstance = null;
            }
        }
        return isPlaying;
    }

    public static float getProgress() {
        if (!isPlaying() || currentDurationTicks <= 0) return 0f;
        Minecraft mc = Minecraft.getInstance();
        int elapsed = (mc.player != null ? mc.player.tickCount : 0) - playStartTick;
        return Math.min(1.0f, (float) elapsed / currentDurationTicks);
    }

    public static int getElapsedSeconds() {
        if (!isPlaying()) return 0;
        Minecraft mc = Minecraft.getInstance();
        int elapsed = (mc.player != null ? mc.player.tickCount : 0) - playStartTick;
        return elapsed / 20;
    }

    public static boolean isTrackPlaying(String trackId) {
        return isPlaying() && trackId.equals(currentTrack);
    }

    private static SoundEvent getSoundEvent(String trackId) {
        return switch (trackId) {
            case "only_my_railgun" -> AcademySounds.MEDIA_RAILGUN.value();
            case "level5_judgelight" -> AcademySounds.MEDIA_JUDGELIGHT.value();
            case "sisters_noise" -> AcademySounds.MEDIA_NOISE.value();
            default -> null;
        };
    }

    private static int getDurationTicks(String trackId) {
        return switch (trackId) {
            case "only_my_railgun" -> DURATION_RAILGUN * 20;
            case "level5_judgelight" -> DURATION_JUDGELIGHT * 20;
            case "sisters_noise" -> DURATION_NOISE * 20;
            default -> 60 * 20;
        };
    }

    public static String formatTime(int totalSeconds) {
        int min = totalSeconds / 60;
        int sec = totalSeconds % 60;
        return String.format("%d:%02d", min, sec);
    }
}
