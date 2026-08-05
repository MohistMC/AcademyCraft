package com.mohistmc.academy.client.renderer;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.client.effect.ShieldEffectEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

/**
 * 护盾渲染器 — 多层半透明球面，带呼吸动画。
 */
public class ShieldEffectRenderer extends EntityRenderer<ShieldEffectEntity> {

    private static final ResourceLocation SHIELD_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/effects/shield_hex.png");

    private static final int RING_SEGMENTS = 32;
    private static final float VERTICAL_SCALE = 0.8f;

    public ShieldEffectRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(ShieldEffectEntity entity, float yaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        float lifeProgress = entity.getLifeProgress();
        float alpha = lifeProgress < 0.15f ? lifeProgress / 0.15f
                : lifeProgress > 0.8f ? (1.0f - lifeProgress) / 0.2f : 1.0f;
        if (alpha <= 0.01f) return;

        float radius = entity.getRadius();
        int color = entity.getColor();
        int layers = entity.getLayerCount();

        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;

        // Breathing effect
        float breathe = 1.0f + 0.05f * (float) Math.sin(entity.tickCount * 0.15);

        poseStack.pushPose();
        poseStack.translate(0, entity.getBbHeight() / 2, 0);

        // Billboard the shield to face the camera partially
        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        Vec3 entityPos = entity.position().add(0, entity.getBbHeight() / 2, 0);
        Vec3 lookVec = cameraPos.subtract(entityPos).normalize();

        RenderType renderType = RenderType.entityTranslucentEmissive(SHIELD_TEXTURE);
        VertexConsumer vc = buffer.getBuffer(renderType);
        Matrix4f matrix = poseStack.last().pose();

        // Render each layer as a set of billboard rings at different angles
        for (int layer = 0; layer < layers; layer++) {
            float layerProgress = lifeProgress * layers - layer;
            float layerAlpha = alpha;
            if (layerProgress < 0.2f) layerAlpha = layerProgress / 0.2f * alpha;
            else if (layerProgress > 0.8f) layerAlpha = (1.0f - layerProgress) / 0.2f * alpha;
            if (layerAlpha <= 0.01f) continue;

            float layerRadius = radius * (0.7f + layer * 0.15f) * breathe;
            float layerAlphaColor = layerAlpha * (0.4f + layer * 0.2f);

            // Render a ring of billboard quads forming a sphere-like shape
            for (int i = 0; i < RING_SEGMENTS; i++) {
                float angle = (float) i / RING_SEGMENTS * Mth.TWO_PI;
                float nextAngle = (float) (i + 1) / RING_SEGMENTS * Mth.TWO_PI;

                // Vertical ring
                float cosA = Mth.cos(angle) * layerRadius;
                float sinA = Mth.sin(angle) * layerRadius * VERTICAL_SCALE;
                float cosB = Mth.cos(nextAngle) * layerRadius;
                float sinB = Mth.sin(nextAngle) * layerRadius * VERTICAL_SCALE;

                vc.addVertex(matrix, cosA, sinA, 0)
                        .setColor(r, g, b, layerAlphaColor).setUv(angle / Mth.TWO_PI, 0f)
                        .setOverlay(OverlayTexture.NO_OVERLAY).setUv2(0xF0, 0xF0).setNormal(0, 1, 0);
                vc.addVertex(matrix, cosB, sinB, 0)
                        .setColor(r, g, b, layerAlphaColor).setUv(nextAngle / Mth.TWO_PI, 0f)
                        .setOverlay(OverlayTexture.NO_OVERLAY).setUv2(0xF0, 0xF0).setNormal(0, 1, 0);
                vc.addVertex(matrix, cosB, 0, sinB)
                        .setColor(r, g, b, layerAlphaColor).setUv(nextAngle / Mth.TWO_PI, 1f)
                        .setOverlay(OverlayTexture.NO_OVERLAY).setUv2(0xF0, 0xF0).setNormal(0, 1, 0);
                vc.addVertex(matrix, cosA, 0, sinA)
                        .setColor(r, g, b, layerAlphaColor).setUv(angle / Mth.TWO_PI, 1f)
                        .setOverlay(OverlayTexture.NO_OVERLAY).setUv2(0xF0, 0xF0).setNormal(0, 1, 0);
            }

            // Horizontal ring
            for (int i = 0; i < RING_SEGMENTS; i++) {
                float angle = (float) i / RING_SEGMENTS * Mth.TWO_PI;
                float nextAngle = (float) (i + 1) / RING_SEGMENTS * Mth.TWO_PI;

                float x1 = Mth.cos(angle) * layerRadius;
                float z1 = Mth.sin(angle) * layerRadius;
                float x2 = Mth.cos(nextAngle) * layerRadius;
                float z2 = Mth.sin(nextAngle) * layerRadius;

                vc.addVertex(matrix, x1, 0, z1)
                        .setColor(r, g, b, layerAlphaColor * 0.7f).setUv(0, 0)
                        .setOverlay(OverlayTexture.NO_OVERLAY).setUv2(0xF0, 0xF0).setNormal(0, 1, 0);
                vc.addVertex(matrix, x2, 0, z2)
                        .setColor(r, g, b, layerAlphaColor * 0.7f).setUv(1, 0)
                        .setOverlay(OverlayTexture.NO_OVERLAY).setUv2(0xF0, 0xF0).setNormal(0, 1, 0);
                vc.addVertex(matrix, x2, 0, z2)
                        .setColor(r, g, b, layerAlphaColor * 0.3f).setUv(1, 1)
                        .setOverlay(OverlayTexture.NO_OVERLAY).setUv2(0xF0, 0xF0).setNormal(0, 1, 0);
                vc.addVertex(matrix, x1, 0, z1)
                        .setColor(r, g, b, layerAlphaColor * 0.3f).setUv(0, 1)
                        .setOverlay(OverlayTexture.NO_OVERLAY).setUv2(0xF0, 0xF0).setNormal(0, 1, 0);
            }
        }

        poseStack.popPose();
        super.render(entity, yaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(ShieldEffectEntity entity) {
        return SHIELD_TEXTURE;
    }
}
