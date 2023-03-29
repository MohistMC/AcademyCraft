package com.mohistmc.academy.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

public class RenderUtils {
    public static void loadTexture(ResourceLocation layer) {
        Minecraft.getInstance().getTextureManager().bindForSetup(layer);
    }
}
