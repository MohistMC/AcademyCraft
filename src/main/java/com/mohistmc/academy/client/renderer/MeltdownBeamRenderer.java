package com.mohistmc.academy.client.renderer;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.client.effect.MeltdownBeamEntity;
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
 * 熔毁光束渲染器 — 绿色能量射线，类似 Railgun 但颜色不同。
 *
 * @author Mgazul
 */
public class MeltdownBeamRenderer extends EntityRenderer<MeltdownBeamEntity> {

    private static final ResourceLocation BEAM_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/effects/meltdown_beam.png");

    public MeltdownBeamRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(MeltdownBeamEntity entity, float yaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.getBeamLength() <= 0) return;

        Vec3 dir = entity.getBeamDirection().normalize();
        double length = entity.getBeamLength();
        float lifeProgress = entity.getLifeProgress();

        float alpha = lifeProgress < 0.3f ? lifeProgress / 0.3f
                : lifeProgress > 0.7f ? (1.0f - lifeProgress) / 0.3f : 1.0f;
        alpha = Mth.clamp(alpha, 0.0f, 1.0f);

        Vec3 startPos = entity.getStartPos();
        poseStack.pushPose();
        poseStack.translate(startPos.x - entity.getX(), startPos.y - entity.getY(), startPos.z - entity.getZ());

        Matrix4f matrix = poseStack.last().pose();

        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        Vec3 toCamera = cameraPos.subtract(startPos).normalize();
        Vec3 billboardRight = dir.cross(toCamera).normalize();
        if (billboardRight.length() < 0.001) {
            billboardRight = dir.cross(new Vec3(0, 1, 0)).normalize();
            if (billboardRight.length() < 0.001) {
                billboardRight = dir.cross(new Vec3(1, 0, 0)).normalize();
            }
        }

        RenderType type = RenderType.entityTranslucentEmissive(BEAM_TEXTURE);
        VertexConsumer vc = buffer.getBuffer(type);

        // Outer glow — wide, greenish-white
        renderBillboardBeam(matrix, vc, dir, length, billboardRight,
                0.35f * alpha, 0.5f, 1.0f, 0.65f, alpha * 0.5f, packedLight);

        // Inner core — narrow, bright green
        renderBillboardBeam(matrix, vc, dir, length, billboardRight,
                0.12f * alpha, 0.3f, 1.0f, 0.4f, alpha * 0.85f, packedLight);

        // Central — thinnest, white-hot
        renderBillboardBeam(matrix, vc, dir, length, billboardRight,
                0.03f * alpha, 0.85f, 1.0f, 0.9f, alpha, packedLight);

        poseStack.popPose();
        super.render(entity, yaw, partialTick, poseStack, buffer, packedLight);
    }

    private void renderBillboardBeam(Matrix4f matrix, VertexConsumer vc,
                                      Vec3 dir, double length, Vec3 right,
                                      float width, float r, float g, float b, float alpha, int light) {
        int segments = Math.max(2, (int) (length / 0.5));
        float bx = (float)(right.x * width), by = (float)(right.y * width), bz = (float)(right.z * width);

        for (int i = 0; i < segments; i++) {
            float t0 = (float) i / segments, t1 = (float) (i + 1) / segments;
            float s0 = (float)(dir.x * length * t0), s1 = (float)(dir.y * length * t0), s2 = (float)(dir.z * length * t0);
            float e0 = (float)(dir.x * length * t1), e1 = (float)(dir.y * length * t1), e2 = (float)(dir.z * length * t1);

            vc.addVertex(matrix, s0 + bx, s1 + by, s2 + bz).setColor(r, g, b, alpha).setUv(t0, 0)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setUv2(light, light).setNormal(0, 1, 0);
            vc.addVertex(matrix, s0 - bx, s1 - by, s2 - bz).setColor(r, g, b, alpha).setUv(t0, 1)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setUv2(light, light).setNormal(0, 1, 0);
            vc.addVertex(matrix, e0 - bx, e1 - by, e2 - bz).setColor(r, g, b, alpha).setUv(t1, 1)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setUv2(light, light).setNormal(0, 1, 0);
            vc.addVertex(matrix, e0 + bx, e1 + by, e2 + bz).setColor(r, g, b, alpha).setUv(t1, 0)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setUv2(light, light).setNormal(0, 1, 0);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(MeltdownBeamEntity entity) {
        return BEAM_TEXTURE;
    }
}
