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
 * 波纹标记实体 —— 地面上的脉冲波纹标记。
 * 替代旧代码 EntityRippleMark + RippleMarkRender。
 * <p>
 * 由 ThunderClap 使用，显示蓄力目标位置。
 *
 * @author Mgazul
 */
public class RippleMarkEntity extends Entity {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/effects/glow_circle.png");

    private int life = 60;
    private int color = 0xCCCCCCCC; // ARGB

    public RippleMarkEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.noCulling = true;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    public void tick() {
        super.tick();
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

    // ==================== Renderer ====================

    public static class Renderer extends EntityRenderer<RippleMarkEntity> {

        public Renderer(EntityRendererProvider.Context context) {
            super(context);
        }

        @Override
        public void render(RippleMarkEntity entity, float yaw, float partialTick,
                           PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
            float progress = (entity.tickCount + partialTick) / (float) entity.life;
            if (progress >= 1.0f) return;

            float alpha = 1.0f;
            if (progress < 0.1f) alpha = progress / 0.1f;
            else if (progress > 0.7f) alpha = (1.0f - progress) / 0.3f;

            int a = (entity.color >> 24) & 0xFF;
            int r = (entity.color >> 16) & 0xFF;
            int g = (entity.color >> 8) & 0xFF;
            int b = entity.color & 0xFF;

            float finalAlpha = alpha * (a / 255f);

            // Expanding rings
            RenderType renderType = RenderType.entityTranslucentEmissive(TEXTURE);
            VertexConsumer vc = buffer.getBuffer(renderType);
            Matrix4f matrix = poseStack.last().pose();

            poseStack.pushPose();
            poseStack.translate(0, 0.01, 0); // Just above ground

            // Render expanding ring
            float ringOuter = progress * 3.0f;
            float ringThickness = 0.15f;
            float ringAlpha = finalAlpha * (1.0f - progress);

            // Simple billboarded ring quad
            float halfSize = ringOuter;
            vc.addVertex(matrix, -halfSize, -halfSize, 0).setColor(r / 255f, g / 255f, b / 255f, ringAlpha)
                    .setUv(0, 1).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(0xF0, 0xF0).setNormal(0, 0, 1);
            vc.addVertex(matrix, -halfSize, halfSize, 0).setColor(r / 255f, g / 255f, b / 255f, ringAlpha)
                    .setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(0xF0, 0xF0).setNormal(0, 0, 1);
            vc.addVertex(matrix, halfSize, halfSize, 0).setColor(r / 255f, g / 255f, b / 255f, ringAlpha)
                    .setUv(1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(0xF0, 0xF0).setNormal(0, 0, 1);
            vc.addVertex(matrix, halfSize, -halfSize, 0).setColor(r / 255f, g / 255f, b / 255f, ringAlpha)
                    .setUv(1, 1).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(0xF0, 0xF0).setNormal(0, 0, 1);

            poseStack.popPose();
        }

        @Override
        public ResourceLocation getTextureLocation(RippleMarkEntity entity) {
            return TEXTURE;
        }
    }
}
