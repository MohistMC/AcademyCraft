package com.mohistmc.academy.utils;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class RenderUtils {
    /**
     * 来自newbing：
     *
     * @param strack    PoseStack类型，用于存储渲染状态的矩阵堆栈。
     * @param consumer  VertexConsumer类型，用于渲染顶点数据。
     * @param p_112158_ float类型，表示第一个四边形的左上角x坐标。
     * @param p_112159_ float类型，表示第一个四边形的左上角y坐标。
     * @param p_112160_ float类型，表示第一个四边形的左上角z坐标。
     * @param p_112161_ float类型，表示第一个四边形的右下角x坐标。
     * @param p_112162_ int类型，表示第一个四边形的纹理左上角u坐标。
     * @param p_112163_ int类型，表示第一个四边形的纹理左上角v坐标。
     * @param p_112164_ float类型，表示第二个四边形的左上角x坐标。
     * @param p_112165_ float类型，表示第二个四边形的左上角y坐标。
     * @param p_112166_ float类型，表示第二个四边形的左上角z坐标。
     * @param p_112167_ float类型，表示第二个四边形的右下角x坐标。
     * @param p_112168_ int类型，表示第二个四边形的纹理左上角u坐标。
     * @param p_112169_ int类型，表示第二个四边形的纹理左上角v坐标。
     * @param p_112170_ float类型，表示第三个四边形的左上角x坐标。
     * @param p_112171_ float类型，表示第三个四边形的左上角y坐标。
     * @param p_112172_ float类型，表示第三个四边形的左上角z坐标。
     * @param p_112173_ float类型，表示第三个四边形的右下角x坐标。
     * @param p_112174_ int类型，表示第三个四边形的纹理左上角u坐标。
     * @param p_112175_ int类型，表示第三个四边形的纹理左上角v坐标。
     */
    public static void renderPart(PoseStack strack, VertexConsumer consumer, float p_112158_, float p_112159_, float p_112160_, float p_112161_, int p_112162_, int p_112163_, float p_112164_, float p_112165_, float p_112166_, float p_112167_, float p_112168_, float p_112169_, float p_112170_, float p_112171_, float p_112172_, float p_112173_, float p_112174_, float p_112175_) {
        PoseStack.Pose pose = strack.last();
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();
        renderQuad(matrix4f, matrix3f, consumer, p_112158_, p_112159_, p_112160_, p_112161_, p_112162_, p_112163_, p_112164_, p_112165_, p_112166_, p_112167_, p_112172_, p_112173_, p_112174_, p_112175_);
        renderQuad(matrix4f, matrix3f, consumer, p_112158_, p_112159_, p_112160_, p_112161_, p_112162_, p_112163_, p_112170_, p_112171_, p_112168_, p_112169_, p_112172_, p_112173_, p_112174_, p_112175_);
        renderQuad(matrix4f, matrix3f, consumer, p_112158_, p_112159_, p_112160_, p_112161_, p_112162_, p_112163_, p_112166_, p_112167_, p_112170_, p_112171_, p_112172_, p_112173_, p_112174_, p_112175_);
        renderQuad(matrix4f, matrix3f, consumer, p_112158_, p_112159_, p_112160_, p_112161_, p_112162_, p_112163_, p_112168_, p_112169_, p_112164_, p_112165_, p_112172_, p_112173_, p_112174_, p_112175_);
    }

    public static void renderQuad(Matrix4f p_253960_, Matrix3f p_254005_, VertexConsumer p_112122_, float p_112123_, float p_112124_, float p_112125_, float p_112126_, int p_112127_, int p_112128_, float p_112129_, float p_112130_, float p_112131_, float p_112132_, float p_112133_, float p_112134_, float p_112135_, float p_112136_) {
        addVertex(p_253960_, p_254005_, p_112122_, p_112123_, p_112124_, p_112125_, p_112126_, p_112128_, p_112129_, p_112130_, p_112134_, p_112135_);
        addVertex(p_253960_, p_254005_, p_112122_, p_112123_, p_112124_, p_112125_, p_112126_, p_112127_, p_112129_, p_112130_, p_112134_, p_112136_);
        addVertex(p_253960_, p_254005_, p_112122_, p_112123_, p_112124_, p_112125_, p_112126_, p_112127_, p_112131_, p_112132_, p_112133_, p_112136_);
        addVertex(p_253960_, p_254005_, p_112122_, p_112123_, p_112124_, p_112125_, p_112126_, p_112128_, p_112131_, p_112132_, p_112133_, p_112135_);
    }

    public static void addVertex(Matrix4f p_253955_, Matrix3f p_253713_, VertexConsumer p_253894_, float p_253871_, float p_253841_, float p_254568_, float p_254361_, int p_254357_, float p_254451_, float p_254240_, float p_254117_, float p_253698_) {
        p_253894_.vertex(p_253955_, p_254451_, (float) p_254357_, p_254240_).color(p_253871_, p_253841_, p_254568_, p_254361_).uv(p_254117_, p_253698_).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(p_253713_, 0.0F, 1.0F, 0.0F).endVertex();
    }

    public static void render(int imageWidth, int imageHeight, int x, int y, int width, int height, PoseStack stack, ResourceLocation resource) {

        /*
        int x = (width - drawWidth) / 2;
        int y = (height - drawHeight) / 2;
        RenderSystem.setShaderColor(255.0f, 255.0f, 255.0f, 255.0f);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderTexture(0, resource);


        GuiComponent.blit(stack, x, y, 0, 0, drawWidth, drawHeight);
          */
    }

    public static void renderCenter(int drawWidth, int drawHeight, int width, int height, PoseStack poseStack, ResourceLocation resource) {
        renderCenter(0, 0, drawWidth, drawHeight, width, height, poseStack, resource);
    }

    public static void renderCenter(int x, int y, int drawWidth, int drawHeight, int width, int height, PoseStack poseStack, ResourceLocation resource) {
        int left = (width - drawWidth) / 2;
        int top = (height - drawHeight) / 2;
        render(drawWidth, drawHeight, left + x, top + y, poseStack, resource);
    }

    public static void renderCenterTop(int x, int y, int drawWidth, int drawHeight, int width, int top, PoseStack poseStack, ResourceLocation resource) {
        int left = (width - drawWidth) / 2;
        render(drawWidth, drawHeight, left + x, top + y, poseStack, resource);
    }

    public static void render(int drawWidth, int drawHeight, int left, int top, PoseStack poseStack, ResourceLocation resource) {
        RenderSystem.setShaderTexture(0, resource);
        RenderSystem.enableDepthTest();
        GuiComponent.blit(poseStack, left, top, 0, 0, 0, drawWidth, drawHeight, drawWidth, drawHeight);
    }

}
