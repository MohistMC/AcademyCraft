package com.mohistmc.academy.utils;

import net.minecraft.resources.ResourceLocation;

/**
 * 资源工具类
 */
public class Resources {

    public static ResourceLocation[] getEffectSeq(String effectName, int n) {
        ResourceLocation[] layers = new ResourceLocation[n];
        String baseName = "academy:textures/effects/" + effectName + "/";
        for(int i = 0; i < n; ++i) {
            layers[i] = ResourceLocation.tryParse(baseName + i + ".png");
        }
        return layers;
    }
}
