package com.mohistmc.academy.client.renderer;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.entity.RailgunBeamEntity;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RailgunBeamRenderer extends EntityRenderer<RailgunBeamEntity> {

    private static final ResourceLocation BEAM_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/effects/railgun.png");

    private static final int ARC_SIZE = 15;
    private static final float ARC_CLEAR_TICK = 30;

    private static final ArcTemplate[] ARC_TEMPLATES = new ArcTemplate[ARC_SIZE];

    static {
        Random rand = new Random(42);
        for (int i = 0; i < ARC_SIZE; i++) {
            ARC_TEMPLATES[i] = ArcTemplate.generate(rand);
        }
    }

    public RailgunBeamRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(RailgunBeamEntity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (entity.getBeamLength() <= 0) return;

        Vec3 dir = entity.getBeamDirection().normalize();
        double length = entity.getBeamLength();
        float lifeProgress = entity.getLifeProgress();

        // Alpha: blend in → hold → blend out
        float alpha = lifeProgress < 0.3f ? lifeProgress / 0.3f
                : lifeProgress > 0.7f ? (1.0f - lifeProgress) / 0.3f : 1.0f;
        alpha = Mth.clamp(alpha, 0.0f, 1.0f);

        Vec3 startPos = entity.getStartPos();

        poseStack.pushPose();

        // Entity position is already in PoseStack, translate relative to beam start
        poseStack.translate(startPos.x - entity.getX(), startPos.y - entity.getY(), startPos.z - entity.getZ());

        Matrix4f matrix = poseStack.last().pose();

        // Compute camera-facing billboard right vector
        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        Vec3 toCamera = cameraPos.subtract(startPos).normalize();

        Vec3 billboardRight = dir.cross(toCamera).normalize();
        if (billboardRight.length() < 0.001) {
            billboardRight = dir.cross(new Vec3(0, 1, 0)).normalize();
            if (billboardRight.length() < 0.001) {
                billboardRight = dir.cross(new Vec3(1, 0, 0)).normalize();
            }
        }

        // Get or create arc data (cached per entity, regenerated only if length changes)
        ArcData arcData = getOrCreateArcData(entity, dir, length);
        arcData.tick(entity.tickCount);

        // === Render outer glow beam (wider, soft, warm white) ===
        RenderType glowType = RenderType.entityTranslucentEmissive(BEAM_TEXTURE);
        VertexConsumer glowConsumer = bufferSource.getBuffer(glowType);
        renderBillboardBeam(matrix, glowConsumer, dir, length, billboardRight,
                0.3f * alpha,
                241f / 255f, 240f / 255f, 222f / 255f, alpha * 0.8f, packedLight);

        // === Render inner core beam (narrower, bright orange) ===
        VertexConsumer coreConsumer = bufferSource.getBuffer(glowType);
        renderBillboardBeam(matrix, coreConsumer, dir, length, billboardRight,
                0.09f * alpha,
                236f / 255f, 170f / 255f, 93f / 255f, alpha * 0.6f, packedLight);

        // === Render electric arcs (initial burst, cleared at tick 30) ===
        if (!arcData.isCleared() && !arcData.segments.isEmpty()) {
            VertexConsumer arcConsumer = bufferSource.getBuffer(glowType);
            renderArcQuads(matrix, arcConsumer, arcData, billboardRight, alpha);
        }

        poseStack.popPose();

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    /**
     * Renders a camera-facing (billboarded) glow beam as a series of quads.
     * Each quad runs along the beam direction and faces the camera.
     */
    private void renderBillboardBeam(Matrix4f matrix, VertexConsumer consumer,
                                      Vec3 dir, double length, Vec3 billboardRight,
                                      float width, float r, float g, float b, float alpha, int packedLight) {
        int segments = Math.max(2, (int) (length / 0.5));
        float bx = (float) (billboardRight.x * width);
        float by = (float) (billboardRight.y * width);
        float bz = (float) (billboardRight.z * width);

        for (int i = 0; i < segments; i++) {
            float t0 = (float) i / segments;
            float t1 = (float) (i + 1) / segments;

            float s0 = (float) (dir.x * length * t0);
            float s1 = (float) (dir.y * length * t0);
            float s2 = (float) (dir.z * length * t0);
            float e0 = (float) (dir.x * length * t1);
            float e1 = (float) (dir.y * length * t1);
            float e2 = (float) (dir.z * length * t1);

            // Front face (toward camera): CCW winding
            consumer.addVertex(matrix, s0 + bx, s1 + by, s2 + bz)
                    .setColor(r, g, b, alpha)
                    .setUv(t0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, packedLight)
                    .setNormal(0, 1, 0);
            consumer.addVertex(matrix, s0 - bx, s1 - by, s2 - bz)
                    .setColor(r, g, b, alpha)
                    .setUv(t0, 1).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, packedLight)
                    .setNormal(0, 1, 0);
            consumer.addVertex(matrix, e0 - bx, e1 - by, e2 - bz)
                    .setColor(r, g, b, alpha)
                    .setUv(t1, 1).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, packedLight)
                    .setNormal(0, 1, 0);
            consumer.addVertex(matrix, e0 + bx, e1 + by, e2 + bz)
                    .setColor(r, g, b, alpha)
                    .setUv(t1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLight, packedLight)
                    .setNormal(0, 1, 0);
        }
    }

    /**
     * Renders electric arc segments as thin camera-facing quads.
     */
    private void renderArcQuads(Matrix4f matrix, VertexConsumer consumer,
                                 ArcData arcData, Vec3 billboardRight, float alpha) {
        float arcWidth = 0.02f;
        float bx = (float) (billboardRight.x * arcWidth);
        float by = (float) (billboardRight.y * arcWidth);
        float bz = (float) (billboardRight.z * arcWidth);

        for (ArcSegment seg : arcData.segments) {
            float segAlpha = alpha * seg.getAlpha();
            if (segAlpha <= 0.01f) continue;

            float sx = (float) seg.start.x;
            float sy = (float) seg.start.y;
            float sz = (float) seg.start.z;
            float ex = (float) seg.end.x;
            float ey = (float) seg.end.y;
            float ez = (float) seg.end.z;

            float r = 0.95f;
            float g = 0.94f;
            float b = 0.87f;

            consumer.addVertex(matrix, sx + bx, sy + by, sz + bz)
                    .setColor(r, g, b, segAlpha).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(0xF0, 0xF0)
                    .setNormal(0, 1, 0);
            consumer.addVertex(matrix, sx - bx, sy - by, sz - bz)
                    .setColor(r, g, b, segAlpha).setUv(0, 1).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(0xF0, 0xF0)
                    .setNormal(0, 1, 0);
            consumer.addVertex(matrix, ex - bx, ey - by, ez - bz)
                    .setColor(r, g, b, segAlpha).setUv(1, 1).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(0xF0, 0xF0)
                    .setNormal(0, 1, 0);
            consumer.addVertex(matrix, ex + bx, ey + by, ez + bz)
                    .setColor(r, g, b, segAlpha).setUv(1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(0xF0, 0xF0)
                    .setNormal(0, 1, 0);
        }
    }

    private ArcData getOrCreateArcData(RailgunBeamEntity entity, Vec3 dir, double length) {
        ArcData data = (ArcData) entity.arcData;
        if (data == null || data.length != length) {
            data = new ArcData(dir, length, entity.getId());
            entity.arcData = data;
        }
        return data;
    }

    @Override
    public ResourceLocation getTextureLocation(RailgunBeamEntity entity) {
        return BEAM_TEXTURE;
    }

    // ==================== Arc System ====================

    private static class ArcTemplate {
        final List<Vec3> points;

        ArcTemplate(List<Vec3> points) {
            this.points = points;
        }

        static ArcTemplate generate(Random rand) {
            int segments = 2 + rand.nextInt(2); // 2-3 segments
            List<Vec3> points = new ArrayList<>();
            points.add(Vec3.ZERO);

            for (int i = 0; i < segments; i++) {
                Vec3 prev = points.getLast();
                float t = (float) (i + 1) / segments;

                double offsetX = 0.3 + rand.nextDouble() * 0.5;
                double offsetY = (rand.nextDouble() - 0.5) * 0.8;
                double offsetZ = (rand.nextDouble() - 0.5) * 0.8;

                double shrink = 1.0 - t * 0.9;
                points.add(new Vec3(
                        prev.x + offsetX * shrink,
                        prev.y + offsetY * shrink,
                        prev.z + offsetZ * shrink
                ));
            }

            return new ArcTemplate(points);
        }
    }

    private static class ArcSegment {
        Vec3 start;
        Vec3 end;
        int maxLife;
        int life;

        ArcSegment(Vec3 start, Vec3 end) {
            this.start = start;
            this.end = end;
            this.maxLife = 15 + (int) (Math.random() * 15);
            this.life = this.maxLife;
        }

        float getAlpha() {
            float frac = (float) life / maxLife;
            if (frac > 0.8f) return 1.0f;
            return frac / 0.8f;
        }

        void tick() {
            if (life > 0) life--;
        }
    }

    public static class ArcData {
        final List<ArcSegment> segments = new ArrayList<>();
        final double length;
        private boolean cleared;

        ArcData(Vec3 dir, double length, int entityId) {
            this.length = length;
            Random rand = new Random(entityId);

            double cur = 1.0;
            while (cur <= length) {
                ArcTemplate template = ARC_TEMPLATES[rand.nextInt(ARC_SIZE)];

                float theta = rand.nextFloat() * Mth.TWO_PI;
                double r = 0.1 + rand.nextDouble() * 0.15;

                Vec3 up = new Vec3(0, 1, 0);
                Vec3 right = dir.cross(up).normalize();
                if (right.length() < 0.01) {
                    right = new Vec3(1, 0, 0);
                }
                Vec3 realUp = right.cross(dir).normalize();

                Vec3 basePos = dir.scale(cur);
                Vec3 offset = right.scale(r * Math.cos(theta))
                        .add(realUp.scale(r * Math.sin(theta)));

                for (int i = 0; i < template.points.size() - 1; i++) {
                    Vec3 p1 = basePos.add(offset).add(template.points.get(i));
                    Vec3 p2 = basePos.add(offset).add(template.points.get(i + 1));
                    segments.add(new ArcSegment(p1, p2));
                }

                cur += 1.0 + rand.nextDouble() * 2.0;
            }
        }

        void tick(int entityTick) {
            if (entityTick >= ARC_CLEAR_TICK && !cleared) {
                cleared = true;
            }

            if (!cleared) {
                for (ArcSegment seg : segments) {
                    seg.tick();
                }
            }
        }

        boolean isCleared() {
            return cleared;
        }
    }
}
