package com.mohistmc.academy.utils;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;
@OnlyIn(Dist.CLIENT)
public class GlStateManager {

    private static final BooleanState lightingState = new BooleanState(2896);
    private static final BooleanState[] lightState = new BooleanState[8];
    private static final ColorMaterialState colorMaterialState = new ColorMaterialState();
    private static int activeShadeModel = 7425;

    public static void enableLighting() {
        lightingState.setEnabled();
    }

    public static void disableLighting() {
        lightingState.setDisabled();
    }

    public static void enableLight(int p_179085_0_) {
        lightState[p_179085_0_].setEnabled();
    }

    public static void disableLight(int p_179122_0_) {
        lightState[p_179122_0_].setDisabled();
    }

    public static void enableColorMaterial() {
        colorMaterialState.colorMaterial.setEnabled();
    }

    public static void disableColorMaterial() {
        colorMaterialState.colorMaterial.setDisabled();
    }

    public static void colorMaterial(int p_179104_0_, int p_179104_1_) {
        if (p_179104_0_ != colorMaterialState.face || p_179104_1_ != colorMaterialState.mode) {
            colorMaterialState.face = p_179104_0_;
            colorMaterialState.mode = p_179104_1_;
            GL11.glColorMaterial(p_179104_0_, p_179104_1_);
        }

    }

    public static void glLight(int p_187438_0_, int p_187438_1_, FloatBuffer p_187438_2_) {
        GL11.glLightfv(p_187438_0_, p_187438_1_, p_187438_2_);
    }

    public static void glLightModel(int p_187424_0_, FloatBuffer p_187424_1_) {
        GL11.glLightModelfv(p_187424_0_, p_187424_1_);
    }

    public static void shadeModel(int p_179103_0_) {
        if (p_179103_0_ != activeShadeModel) {
            activeShadeModel = p_179103_0_;
            GL11.glShadeModel(p_179103_0_);
        }

    }

    static class BooleanState {
        private final int capability;
        private boolean currentState;

        public BooleanState(int p_i46267_1_) {
            this.capability = p_i46267_1_;
        }

        public void setDisabled() {
            this.setState(false);
        }

        public void setEnabled() {
            this.setState(true);
        }

        public void setState(boolean p_179199_1_) {
            if (p_179199_1_ != this.currentState) {
                this.currentState = p_179199_1_;
                if (p_179199_1_) {
                    GL11.glEnable(this.capability);
                } else {
                    GL11.glDisable(this.capability);
                }
            }

        }
    }

    static class ColorMaterialState {
        public BooleanState colorMaterial;
        public int face;
        public int mode;

        private ColorMaterialState() {
            this.colorMaterial = new BooleanState(2903);
            this.face = 1032;
            this.mode = 5634;
        }
    }
}
