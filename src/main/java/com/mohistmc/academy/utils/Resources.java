package com.mohistmc.academy.utils;

import com.mohistmc.academy.AcademyCraft;
import net.minecraft.resources.ResourceLocation;

public class Resources {

    public static ResourceLocation[] getEffectSeq(String effectName, int n) {
        ResourceLocation[] layers = new ResourceLocation[n];
        String baseName = "textures/effects/" + effectName + "/";
        for (int i = 0; i < n; ++i) {
            layers[i] = new ResourceLocation(AcademyCraft.MODID, baseName + i + ".png");
        }
        return layers;
    }
}
