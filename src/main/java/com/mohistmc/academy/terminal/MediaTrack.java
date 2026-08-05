package com.mohistmc.academy.terminal;


import net.minecraft.resources.ResourceLocation;

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
