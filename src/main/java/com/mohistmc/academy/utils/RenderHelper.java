package com.mohistmc.academy.utils;

import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.nio.FloatBuffer;

@OnlyIn(Dist.CLIENT)
public class RenderHelper {

    private static final FloatBuffer COLOR_BUFFER = GLAllocation.createDirectFloatBuffer(4);
    private static final Vec3 LIGHT1_POS = (new Vec3(-0.20000000298023224, 1.0, 0.699999988079071)).normalize();
    private static final Vec3 LIGHT0_POS = (new Vec3(0.20000000298023224, 1.0, -0.699999988079071)).normalize();

    public static void disableStandardItemLighting() {
        GlStateManager.disableLighting();
        GlStateManager.disableLight(0);
        GlStateManager.disableLight(1);
        GlStateManager.disableColorMaterial();
    }

    public static void enableStandardItemLighting() {
        GlStateManager.enableLighting();
        GlStateManager.enableLight(0);
        GlStateManager.enableLight(1);
        GlStateManager.enableColorMaterial();
        GlStateManager.colorMaterial(1032, 5634);
        GlStateManager.glLight(16384, 4611, setColorBuffer(LIGHT0_POS.x, LIGHT0_POS.y, LIGHT0_POS.z, 0.0));
        float lvt_0_1_ = 0.6F;
        GlStateManager.glLight(16384, 4609, setColorBuffer(0.6F, 0.6F, 0.6F, 1.0F));
        GlStateManager.glLight(16384, 4608, setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        GlStateManager.glLight(16384, 4610, setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        GlStateManager.glLight(16385, 4611, setColorBuffer(LIGHT1_POS.x, LIGHT1_POS.y, LIGHT1_POS.z, 0.0));
        GlStateManager.glLight(16385, 4609, setColorBuffer(0.6F, 0.6F, 0.6F, 1.0F));
        GlStateManager.glLight(16385, 4608, setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        GlStateManager.glLight(16385, 4610, setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        GlStateManager.shadeModel(7424);
        float lvt_1_1_ = 0.4F;
        GlStateManager.glLightModel(2899, setColorBuffer(0.4F, 0.4F, 0.4F, 1.0F));
    }

    private static FloatBuffer setColorBuffer(double p_74517_0_, double p_74517_2_, double p_74517_4_, double p_74517_6_) {
        return setColorBuffer((float) p_74517_0_, (float) p_74517_2_, (float) p_74517_4_, (float) p_74517_6_);
    }

    public static FloatBuffer setColorBuffer(float p_74521_0_, float p_74521_1_, float p_74521_2_, float p_74521_3_) {
        COLOR_BUFFER.clear();
        COLOR_BUFFER.put(p_74521_0_).put(p_74521_1_).put(p_74521_2_).put(p_74521_3_);
        COLOR_BUFFER.flip();
        return COLOR_BUFFER;
    }


}
