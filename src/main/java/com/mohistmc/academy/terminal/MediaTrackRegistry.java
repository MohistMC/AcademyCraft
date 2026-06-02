package com.mohistmc.academy.terminal;

import com.mohistmc.academy.AcademyCraft;
import com.mojang.logging.LogUtils;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

/**
 * @author Mgazul
 * @date 2026/5/31 04:34
 */
public class MediaTrackRegistry {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<String, MediaTrack> TRACKS = new LinkedHashMap<>();
    private static boolean initialized = false;

    public static void init() {
        if (initialized) return;
        initialized = true;
        registerBuiltins();
        LOGGER.info("AcademyCraft MediaTrackRegistry initialized with {} tracks", TRACKS.size());
    }

    public static void register(MediaTrack track) {
        TRACKS.put(track.trackId(), track);
    }

    public static MediaTrack getTrack(String trackId) {
        return TRACKS.get(trackId);
    }

    public static List<MediaTrack> getAllTracks() {
        return List.copyOf(TRACKS.values());
    }

    public static boolean isRegistered(String trackId) {
        return TRACKS.containsKey(trackId);
    }

    private static void registerBuiltins() {

        register(new MediaTrack("only_my_railgun",
                "item.academy.media_only_my_railgun", "item.academy.media_only_my_railgun.desc", "OP1",
                ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/media/only_my_railgun.png"),
                ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "media.only_my_railgun"),
                99));

        register(new MediaTrack("level5_judgelight",
                "item.academy.media_level5_judgelight", "item.academy.media_level5_judgelight.desc", "OP2",
                ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/media/level5_judgelight.png"),
                ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "media.level5_judgelight"),
                102));

        register(new MediaTrack("sisters_noise",
                "item.academy.media_sisters_noise", "item.academy.media_sisters_noise.desc", "OPs",
                ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/media/sisters_noise.png"),
                ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "media.sisters_noise"),
                95));
    }
}
