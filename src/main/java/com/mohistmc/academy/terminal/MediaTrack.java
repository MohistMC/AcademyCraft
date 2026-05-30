package com.mohistmc.academy.terminal;


import net.minecraft.resources.ResourceLocation;

/**
 * @author Mgazul
 * @date 2026/5/31 04:34
 */
public record MediaTrack(
        String trackId,
        String nameKey,
        String descKey,
        String tag,
        ResourceLocation texture,
        ResourceLocation soundId,
        int durationSeconds
) {
}
