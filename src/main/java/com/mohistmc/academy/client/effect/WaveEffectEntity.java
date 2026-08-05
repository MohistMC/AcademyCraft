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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;

/**
 * 波环特效实体 —— 冲击波扩散环。
 */
public class WaveEffectEntity extends Entity {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/effects/glow_circle.png");

    private int rings;
    private float maxSize;
    private int life = 15;
    private int bornTick = 0;

    public WaveEffectEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.noCulling = true;
    }

    public void setData(int rings, float size) {
        this.rings = rings;
        this.maxSize = size;
    }

    @Override
    public void tick() {
        super.tick();
        if (tickCount >= life) {
            discard();
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double dist) {
        return true;
    }

    public int getLife() {
        return life;
    }

    public int getRings() {
        return rings;
    }

    public float getMaxSize() {
        return maxSize;
    }

    // ==================== Renderer ====================

    public static class Renderer extends EntityRenderer<WaveEffectEntity> {

        private static final RenderType RENDER_TYPE = RenderType.entityTranslucentEmissive(TEXTURE);

        public Renderer(EntityRendererProvider.Context context) {
            super(context);
        }

        @Override
        public void render(WaveEffectEntity entity, float yaw, float partialTick,
                           PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
            float progress = (entity.tickCount + partialTick) / (float) entity.getLife();
            if (progress >= 1.0f) return;

            // Alpha curve: fade in, hold, fade out
            float alpha = 1.0f;
            if (progress < 0.2f) alpha = progress / 0.2f;
            else if (progress > 0.8f) alpha = (1.0f - progress) / 0.2f;

            float sizeScale = (float) (0.4 + progress * 1.1); // 0.4 → 1.5

            poseStack.pushPose();
            poseStack.translate(0, 0, entity.tickCount / 40.0); // z-offset like old code

            VertexConsumer vc = buffer.getBuffer(RENDER_TYPE);
            Matrix4f matrix = poseStack.last().pose();

            for (int i = 0; i < entity.getRings(); i++) {
                float ringProgress = (progress * entity.getLife() - i * 2) / 10f;
                if (ringProgress < 0 || ringProgress > 1.0f) continue;

                float ringAlpha = alpha;
                if (ringProgress < 0.2f) ringAlpha = ringProgress / 0.2f * alpha;
                else if (ringProgress > 0.8f) ringAlpha = (1.0f - ringProgress) / 0.2f * alpha;
                if (ringAlpha <= 0.01f) continue;

                float size = entity.getMaxSize() * (0.8f + i * 0.2f) * sizeScale;
                float halfSize = size / 2;
                float z = i * 1.5f;

                // Billboard quad
                renderBillboardQuad(matrix, vc, halfSize, z,
                        1f, 1f, 1f, ringAlpha * 0.7f);
            }

            poseStack.popPose();
        }

        private void renderBillboardQuad(Matrix4f matrix, VertexConsumer vc,
                                          float halfSize, float z,
                                          float r, float g, float b, float alpha) {
            vc.addVertex(matrix, -halfSize, -halfSize, z)
                    .setColor(r, g, b, alpha).setUv(0, 1)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setUv2(0xF0, 0xF0).setNormal(0, 0, 1);
            vc.addVertex(matrix, -halfSize, halfSize, z)
                    .setColor(r, g, b, alpha).setUv(0, 0)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setUv2(0xF0, 0xF0).setNormal(0, 0, 1);
            vc.addVertex(matrix, halfSize, halfSize, z)
                    .setColor(r, g, b, alpha).setUv(1, 0)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setUv2(0xF0, 0xF0).setNormal(0, 0, 1);
            vc.addVertex(matrix, halfSize, -halfSize, z)
                    .setColor(r, g, b, alpha).setUv(1, 1)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setUv2(0xF0, 0xF0).setNormal(0, 0, 1);
        }

        @Override
        public ResourceLocation getTextureLocation(WaveEffectEntity entity) {
            return TEXTURE;
        }
    }
}
