package com.mohistmc.academy.utils;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GLCapabilities;

@OnlyIn(Dist.CLIENT)
public class OpenGlHelper {

    public static int defaultTexUnit;
    public static int lightmapTexUnit;
    private static boolean arbMultitexture;
    /* Stores the last values sent into setLightmapTextureCoords */
    public static float lastBrightnessX = 0.0f;
    public static float lastBrightnessY = 0.0f;

    public static void init() {

        //GLCapabilities contextcapabilities = GL.getCapabilities();
        GLCapabilities contextcapabilities = GL.createCapabilities();
        arbMultitexture = contextcapabilities.GL_ARB_multitexture && !contextcapabilities.OpenGL13;
    }

    public static void setLightmapTextureCoords(int target, float p_77475_1_, float t) {

        if (arbMultitexture) {
            ARBMultitexture.glMultiTexCoord2fARB(target, p_77475_1_, t);
        } else {
            GL13.glMultiTexCoord2f(target, p_77475_1_, t);
        }

        if (target == lightmapTexUnit) {
            lastBrightnessX = p_77475_1_;
            lastBrightnessY = t;
        }
    }
}
