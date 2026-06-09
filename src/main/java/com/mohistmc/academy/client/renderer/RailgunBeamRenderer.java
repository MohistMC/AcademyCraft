package com.mohistmc.academy.client.renderer;

import com.mohistmc.academy.AcademyCraft;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mohistmc.academy.entity.RailgunBeamEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RailgunBeamRenderer extends EntityRenderer<RailgunBeamEntity> {

    private static final ResourceLocation BEAM_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/effects/railgun.png");

    public RailgunBeamRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(RailgunBeamEntity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (entity.getBeamLength() <= 0) return;

        Vec3 startPos = entity.getStartPos();
        Vec3 dir = entity.getBeamDirection();
        double length = entity.getBeamLength();
        float lifeProgress = entity.getLifeProgress();

        float alpha = lifeProgress < 0.3f ? lifeProgress / 0.3f
                : lifeProgress > 0.7f ? (1.0f - lifeProgress) / 0.3f : 1.0f;
        alpha = Mth.clamp(alpha, 0.0f, 1.0f);

        poseStack.pushPose();

        double relX = startPos.x - entity.getX();
        double relY = startPos.y - entity.getY();
        double relZ = startPos.z - entity.getZ();
        poseStack.translate(relX, relY, relZ);

        Random random = new Random(entity.getId());
        List<Vector3f> points = generateArcPoints(dir, length, random, entity.tickCount + partialTick);

        Matrix4f matrix = poseStack.last().pose();

        VertexConsumer glowConsumer = bufferSource.getBuffer(RenderType.beaconBeam(BEAM_TEXTURE, true));
        renderGlowBeam(matrix, glowConsumer, points, dir, 0.3f * alpha,
                241 / 255f, 240 / 255f, 222 / 255f, alpha * 0.8f);

        VertexConsumer coreConsumer = bufferSource.getBuffer(RenderType.beaconBeam(BEAM_TEXTURE, true));
        renderGlowBeam(matrix, coreConsumer, points, dir, 0.1f * alpha,
                236 / 255f, 170 / 255f, 93 / 255f, alpha * 0.6f);

        VertexConsumer lineConsumer = bufferSource.getBuffer(RenderType.lines());
        renderArcLines(matrix, lineConsumer, points, alpha);

        poseStack.popPose();

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    private List<Vector3f> generateArcPoints(Vec3 dir, double length, Random random, float time) {
        List<Vector3f> points = new ArrayList<>();
        points.add(new Vector3f(0, 0, 0));

        Vec3 up = new Vec3(0, 1, 0);
        Vec3 right = dir.cross(up).normalize();
        if (right.length() < 0.01) {
            right = new Vec3(1, 0, 0);
        }
        Vec3 realUp = right.cross(dir).normalize();

        double cur = 1.0;
        while (cur <= length) {
            float theta = random.nextFloat() * Mth.TWO_PI;
            double r = 0.1 + random.nextDouble() * 0.15;
            double wiggleTime = time * 0.5 + cur * 0.3;
            r *= (0.8 + 0.2 * Mth.sin((float) wiggleTime));

            double offsetX = right.x * r * Math.cos(theta) + realUp.x * r * Math.sin(theta);
            double offsetY = right.y * r * Math.cos(theta) + realUp.y * r * Math.sin(theta);
            double offsetZ = right.z * r * Math.cos(theta) + realUp.z * r * Math.sin(theta);

            double px = dir.x * cur + offsetX;
            double py = dir.y * cur + offsetY;
            double pz = dir.z * cur + offsetZ;

            points.add(new Vector3f((float) px, (float) py, (float) pz));
            cur += 1.0 + random.nextDouble() * 1.0;
        }

        points.add(new Vector3f((float) (dir.x * length), (float) (dir.y * length), (float) (dir.z * length)));
        return points;
    }

    private void renderArcLines(Matrix4f matrix, VertexConsumer consumer, List<Vector3f> points, float alpha) {
        for (int i = 0; i < points.size() - 1; i++) {
            Vector3f p1 = points.get(i);
            Vector3f p2 = points.get(i + 1);

            float segAlpha = alpha * (1.0f - (float) i / points.size() * 0.5f);
            consumer.addVertex(matrix, p1.x, p1.y, p1.z)
                    .setColor(0.95f, 0.94f, 0.87f, segAlpha)
                    .setNormal(0, 1, 0);
            consumer.addVertex(matrix, p2.x, p2.y, p2.z)
                    .setColor(0.95f, 0.94f, 0.87f, segAlpha)
                    .setNormal(0, 1, 0);
        }
    }

    private void renderGlowBeam(Matrix4f matrix, VertexConsumer consumer, List<Vector3f> points,
                                Vec3 dir, float width, float r, float g, float b, float alpha) {
        if (points.size() < 2) return;

        Vec3 up = new Vec3(0, 1, 0);
        Vec3 right = dir.cross(up).normalize();
        if (right.length() < 0.01) {
            right = new Vec3(1, 0, 0);
        }
        Vec3 realUp = right.cross(dir).normalize();

        for (int i = 0; i < points.size() - 1; i++) {
            Vector3f p1 = points.get(i);
            Vector3f p2 = points.get(i + 1);

            float segAlpha = alpha * (1.0f - (float) i / points.size() * 0.3f);

            float dx1 = (float) (right.x * width);
            float dy1 = (float) (right.y * width);
            float dz1 = (float) (right.z * width);
            float ux1 = (float) (realUp.x * width);
            float uy1 = (float) (realUp.y * width);
            float uz1 = (float) (realUp.z * width);

            float uStart = (float) i / (points.size() - 1);
            float uEnd = (float) (i + 1) / (points.size() - 1);

            // Face 1
            consumer.addVertex(matrix, p1.x + dx1 + ux1, p1.y + dy1 + uy1, p1.z + dz1 + uz1)
                    .setColor(r, g, b, segAlpha)
                    .setUv(uStart, 0)
                    .setUv2(0x00F0, 0x00F0)
                    .setNormal(0, 1, 0);
            consumer.addVertex(matrix, p2.x + dx1 + ux1, p2.y + dy1 + uy1, p2.z + dz1 + uz1)
                    .setColor(r, g, b, segAlpha)
                    .setUv(uEnd, 0)
                    .setUv2(0x00F0, 0x00F0)
                    .setNormal(0, 1, 0);
            consumer.addVertex(matrix, p2.x - dx1 + ux1, p2.y - dy1 + uy1, p2.z - dz1 + uz1)
                    .setColor(r, g, b, segAlpha)
                    .setUv(uEnd, 1)
                    .setUv2(0x00F0, 0x00F0)
                    .setNormal(0, 1, 0);
            consumer.addVertex(matrix, p1.x - dx1 + ux1, p1.y - dy1 + uy1, p1.z - dz1 + uz1)
                    .setColor(r, g, b, segAlpha)
                    .setUv(uStart, 1)
                    .setUv2(0x00F0, 0x00F0)
                    .setNormal(0, 1, 0);

            // Face 2
            consumer.addVertex(matrix, p1.x - dx1 - ux1, p1.y - dy1 - uy1, p1.z - dz1 - uz1)
                    .setColor(r, g, b, segAlpha)
                    .setUv(uStart, 0)
                    .setUv2(0x00F0, 0x00F0)
                    .setNormal(0, 1, 0);
            consumer.addVertex(matrix, p2.x - dx1 - ux1, p2.y - dy1 - uy1, p2.z - dz1 - uz1)
                    .setColor(r, g, b, segAlpha)
                    .setUv(uEnd, 0)
                    .setUv2(0x00F0, 0x00F0)
                    .setNormal(0, 1, 0);
            consumer.addVertex(matrix, p2.x + dx1 - ux1, p2.y + dy1 - uy1, p2.z + dz1 - uz1)
                    .setColor(r, g, b, segAlpha)
                    .setUv(uEnd, 1)
                    .setUv2(0x00F0, 0x00F0)
                    .setNormal(0, 1, 0);
            consumer.addVertex(matrix, p1.x + dx1 - ux1, p1.y + dy1 - uy1, p1.z + dz1 - uz1)
                    .setColor(r, g, b, segAlpha)
                    .setUv(uStart, 1)
                    .setUv2(0x00F0, 0x00F0)
                    .setNormal(0, 1, 0);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(RailgunBeamEntity entity) {
        return BEAM_TEXTURE;
    }
}
