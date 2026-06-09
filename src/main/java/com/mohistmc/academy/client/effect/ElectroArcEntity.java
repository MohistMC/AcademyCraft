package com.mohistmc.academy.client.effect;

import com.mohistmc.academy.AcademyCraft;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 电弧特效实体 —— 渲染真实的闪电/电弧视觉效果。
 * <p>
 * 替代旧代码 EntityArc + EntitySurroundArc：
 * - MODE_BEAM: 沿方向发射电弧（EntityArc 替代）
 * - MODE_SURROUND: 环绕实体/位置生成电弧环（EntitySurroundArc 替代）
 * <p>
 * 使用现代 Minecraft 1.21.1 渲染管线（PoseStack + VertexConsumer）。
 *
 * @author Mgazul
 */
public class ElectroArcEntity extends Entity {

    private static final ResourceLocation ARC_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/effects/glow_line.png");

    public static final int MODE_BEAM = 0;
    public static final int MODE_SURROUND = 1;

    private int mode = MODE_BEAM;
    private double beamLength = 20.0;
    private int life = 20;
    private int arcCount = 3;
    private float texWiggle = 0.5f;
    private float showWiggle = 0.1f;
    private float hideWiggle = 0.4f;
    private boolean visible = true;
    private int entityTargetId = -1;

    // Arc templates (pre-generated jagged lines)
    private List<List<Vec3>> arcTemplates = new ArrayList<>();
    private int[] templateIndices;
    private final Random rand = new Random();

    // Surround mode parameters
    private double surroundWidth = 1.0;
    private double surroundHeight = 2.0;

    public ElectroArcEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.noCulling = true;
    }

    // ==================== Builder Methods ====================

    public ElectroArcEntity setBeam(double length) {
        this.mode = MODE_BEAM;
        this.beamLength = length;
        return this;
    }

    public ElectroArcEntity setSurround(double width, double height) {
        this.mode = MODE_SURROUND;
        this.surroundWidth = width;
        this.surroundHeight = height;
        return this;
    }

    public ElectroArcEntity setLife(int life) {
        this.life = life;
        return this;
    }

    public ElectroArcEntity setArcCount(int count) {
        this.arcCount = count;
        return this;
    }

    public ElectroArcEntity setWiggle(float texWiggle, float showWiggle, float hideWiggle) {
        this.texWiggle = texWiggle;
        this.showWiggle = showWiggle;
        this.hideWiggle = hideWiggle;
        return this;
    }

    public ElectroArcEntity followEntity(LivingEntity entity) {
        this.entityTargetId = entity.getId();
        return this;
    }

    // ==================== Entity Lifecycle ====================

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        generateTemplates();
        templateIndices = new int[arcCount];
        for (int i = 0; i < arcCount; i++) {
            templateIndices[i] = rand.nextInt(arcTemplates.size());
        }
    }

    @Override
    public void tick() {
        super.tick();

        // Follow entity if targeting one
        if (entityTargetId >= 0) {
            Entity target = level().getEntity(entityTargetId);
            if (target != null) {
                setPos(target.getX(), target.getY(), target.getZ());
                setYRot(target instanceof LivingEntity living ? living.yHeadRot : target.getYRot());
                setXRot(target.getXRot());
            }
        }

        // Arc wiggle animation
        for (int i = 0; i < templateIndices.length; i++) {
            if (rand.nextDouble() < texWiggle) {
                templateIndices[i] = rand.nextInt(arcTemplates.size());
            }
        }
        if (visible && rand.nextDouble() < showWiggle) {
            visible = false;
        } else if (!visible && rand.nextDouble() < hideWiggle) {
            visible = true;
        }

        if (tickCount >= life) {
            discard();
        }
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double dist) {
        return true;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
    }

    // ==================== Arc Generation ====================

    private void generateTemplates() {
        int templateCount = 20;
        for (int t = 0; t < templateCount; t++) {
            List<Vec3> points = new ArrayList<>();
            points.add(Vec3.ZERO);

            int segments = 2 + rand.nextInt(4); // 2-5 segments
            double width = 0.25;
            double maxOffset = 0.7;

            for (int i = 0; i < segments; i++) {
                Vec3 prev = points.getLast();
                float progress = (float) (i + 1) / segments;

                double offsetX = 0.3 + rand.nextDouble() * 0.6;
                double offsetY = (rand.nextDouble() - 0.5) * maxOffset * 2;
                double offsetZ = (rand.nextDouble() - 0.5) * maxOffset * 2;

                double shrink = 1.0 - progress * 0.85;
                points.add(new Vec3(
                        prev.x + offsetX * shrink,
                        prev.y + offsetY * shrink,
                        prev.z + offsetZ * shrink
                ));
            }
            arcTemplates.add(points);
        }
    }

    public List<List<Vec3>> getArcTemplates() {
        return arcTemplates;
    }

    public int[] getTemplateIndices() {
        return templateIndices;
    }

    public boolean isVisible() {
        return visible;
    }

    public int getMode() {
        return mode;
    }

    public double getBeamLength() {
        return beamLength;
    }

    public double getSurroundWidth() {
        return surroundWidth;
    }

    public double getSurroundHeight() {
        return surroundHeight;
    }

    public float getLifeProgress() {
        return Math.min((float) tickCount / life, 1.0f);
    }

    // ==================== Renderer ====================

    public static class Renderer extends EntityRenderer<ElectroArcEntity> {

        private static final RenderType ARC_TYPE = RenderType.lightning();

        public Renderer(EntityRendererProvider.Context context) {
            super(context);
        }

        @Override
        public void render(ElectroArcEntity entity, float yaw, float partialTick,
                           PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
            if (!entity.isVisible()) return;

            float progress = entity.getLifeProgress();
            float alpha = 1.0f;
            if (progress < 0.2f) alpha = progress / 0.2f;
            else if (progress > 0.8f) alpha = (1.0f - progress) / 0.2f;
            if (alpha <= 0.01f) return;

            poseStack.pushPose();

            var templates = entity.getArcTemplates();
            if (templates.isEmpty()) {
                poseStack.popPose();
                return;
            }

            VertexConsumer vc = buffer.getBuffer(RenderType.lightning());
            Matrix4f matrix = poseStack.last().pose();

            int[] idx = entity.getTemplateIndices();
            float r = 0.95f, g = 0.96f, b = 1.0f;

            if (entity.getMode() == MODE_BEAM) {
                renderBeamArcs(matrix, vc, entity, templates, idx, r, g, b, alpha);
            } else {
                renderSurroundArcs(matrix, vc, entity, templates, idx, r, g, b, alpha);
            }

            poseStack.popPose();
        }

        private void renderBeamArcs(Matrix4f matrix, VertexConsumer vc, ElectroArcEntity entity,
                                     List<List<Vec3>> templates, int[] idx,
                                     float r, float g, float b, float alpha) {
            double length = entity.getBeamLength();
            double maxLen = 0;
            // Find max length of all templates to scale
            for (List<Vec3> t : templates) {
                if (!t.isEmpty()) {
                    Vec3 last = t.getLast();
                    maxLen = Math.max(maxLen, last.x);
                }
            }
            if (maxLen <= 0) maxLen = 1;
            double scale = length / maxLen;

            for (int i = 0; i < idx.length; i++) {
                List<Vec3> points = templates.get(idx[i]);
                for (int j = 0; j < points.size() - 1; j++) {
                    Vec3 p1 = points.get(j).scale(scale);
                    Vec3 p2 = points.get(j + 1).scale(scale);
                    float segAlpha = alpha * (1.0f - (float) j / points.size() * 0.5f);

                    vc.addVertex(matrix, (float) p1.x, (float) p1.y, (float) p1.z)
                            .setColor(r, g, b, segAlpha).setUv2(0xF0, 0xF0);
                    vc.addVertex(matrix, (float) p2.x, (float) p2.y, (float) p2.z)
                            .setColor(r, g, b, segAlpha).setUv2(0xF0, 0xF0);
                }
            }
        }

        private void renderSurroundArcs(Matrix4f matrix, VertexConsumer vc, ElectroArcEntity entity,
                                         List<List<Vec3>> templates, int[] idx,
                                         float r, float g, float b, float alpha) {
            double hw = entity.getSurroundWidth() / 2;
            double hh = entity.getSurroundHeight();
            double hd = entity.getSurroundWidth() / 2;
            Random rand = new Random(entity.getId());

            for (int i = 0; i < idx.length; i++) {
                // Position arc on the surface of the bounding box
                double yaw = rand.nextDouble() * Mth.TWO_PI;
                double pitch = rand.nextDouble() * Math.PI;

                double y = Math.sin(pitch) * hh;
                double r2 = Math.sqrt(1 - y * y / (hh * hh));
                r2 = r2 > 1 ? 1 : r2;
                double x = r2 * Math.sin(yaw) * hw;
                double z = r2 * Math.cos(yaw) * hd;

                List<Vec3> points = templates.get(idx[i]);
                for (int j = 0; j < points.size() - 1; j++) {
                    Vec3 p1 = points.get(j).add(x, y, z);
                    Vec3 p2 = points.get(j + 1).add(x, y, z);
                    float segAlpha = alpha * (1.0f - (float) j / points.size() * 0.5f);

                    vc.addVertex(matrix, (float) p1.x, (float) p1.y, (float) p1.z)
                            .setColor(r, g, b, segAlpha).setUv2(0xF0, 0xF0);
                    vc.addVertex(matrix, (float) p2.x, (float) p2.y, (float) p2.z)
                            .setColor(r, g, b, segAlpha).setUv2(0xF0, 0xF0);
                }
            }
        }

        @Override
        public ResourceLocation getTextureLocation(ElectroArcEntity entity) {
            return ARC_TEXTURE;
        }
    }
}
