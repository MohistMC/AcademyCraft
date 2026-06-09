package com.mohistmc.academy.world.entity;

import com.mohistmc.academy.AcademyCraft;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * 单个矿物高亮实体 —— 固定在矿石方块位置，渲染一个带颜色着色的纹理立方体后自动消失。
 *
 * @author Mgazul
 * @date 2026/6/9
 */
public class OreHighlightEntity extends Entity {

    private static final EntityDataAccessor<Integer> DATA_HARVEST_LEVEL =
            SynchedEntityData.defineId(OreHighlightEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_RANGE =
            SynchedEntityData.defineId(OreHighlightEntity.class, EntityDataSerializers.INT);

    private static final int LIFETIME = 100;
    private int age = 0;

    public OreHighlightEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
        this.noCulling = true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_HARVEST_LEVEL, 0);
        builder.define(DATA_RANGE, 15);
    }

    public void setData(int harvestLevel, int range) {
        this.entityData.set(DATA_HARVEST_LEVEL, harvestLevel);
        this.entityData.set(DATA_RANGE, range);
    }

    public int getHarvestLevel() {
        return this.entityData.get(DATA_HARVEST_LEVEL);
    }

    public int getRange() {
        return this.entityData.get(DATA_RANGE);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distanceSqr) {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        age++;
        if (age >= LIFETIME) {
            discard();
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {}

    // ==================== Renderer ====================

    public static class Renderer extends EntityRenderer<OreHighlightEntity> {

        private static final ResourceLocation TEXTURE =
                ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/effects/mineview.png");

        private static final RenderType MINE_HIGHLIGHT_TYPE = RenderType.create(
                "academy_mine_highlight",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,
                1536,
                false,
                true,
                RenderType.CompositeState.builder()
                        .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
                        .setTextureState(new RenderStateShard.TextureStateShard(TEXTURE, false, false))
                        .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                        .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
                        .setCullState(RenderStateShard.NO_CULL)
                        .setLightmapState(RenderStateShard.LIGHTMAP)
                        .setOverlayState(RenderStateShard.OVERLAY)
                        .createCompositeState(false)
        );

        private static final int[][] COLORS = {
                {115, 200, 227},  // level 0: 青色 — 默认
                {161, 181, 188},  // level 1: 灰蓝
                {87,  231, 248},  // level 2: 浅蓝
                {97,  204, 94},   // level 3: 绿色
                {235, 109, 84}    // level 4: 橙红
        };

        public Renderer(EntityRendererProvider.Context context) {
            super(context);
        }

        @Override
        public void render(OreHighlightEntity entity, float yaw, float partialTick,
                           PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {

            // 根据与本地玩家的距离计算透明度
            float alpha;
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                double dx = entity.getX() - player.getX();
                double dy = entity.getY() - player.getY();
                double dz = entity.getZ() - player.getZ();
                double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                double jdg = 1.0 - dist / entity.getRange() * 2.2;
                alpha = (float) Math.clamp(0.3f + jdg * 0.7f, 0.0, 1.0);
            } else {
                alpha = 0.8f;
            }
            if (alpha <= 0.01f) return;

            int colorIdx = Math.min(COLORS.length - 1, entity.getHarvestLevel());
            int[] c = COLORS[colorIdx];
            float r = c[0] / 255f;
            float g = c[1] / 255f;
            float b = c[2] / 255f;

            VertexConsumer vc = bufferSource.getBuffer(MINE_HIGHLIGHT_TYPE);
            PoseStack.Pose pose = poseStack.last();

            poseStack.pushPose();
            renderFullCube(vc, pose, r, g, b, alpha);
            poseStack.popPose();
        }

        private void renderFullCube(VertexConsumer vc, PoseStack.Pose pose,
                                    float r, float g, float b, float a) {
            // 底面 (y = 0)
            v(vc, pose, 0, 0, 1, 0, 1, r, g, b, a);
            v(vc, pose, 1, 0, 1, 1, 1, r, g, b, a);
            v(vc, pose, 1, 0, 0, 1, 0, r, g, b, a);
            v(vc, pose, 0, 0, 0, 0, 0, r, g, b, a);

            // 顶面 (y = 1)
            v(vc, pose, 0, 1, 0, 0, 0, r, g, b, a);
            v(vc, pose, 1, 1, 0, 1, 0, r, g, b, a);
            v(vc, pose, 1, 1, 1, 1, 1, r, g, b, a);
            v(vc, pose, 0, 1, 1, 0, 1, r, g, b, a);

            // 前面 (z = 1)
            v(vc, pose, 0, 1, 1, 0, 0, r, g, b, a);
            v(vc, pose, 1, 1, 1, 1, 0, r, g, b, a);
            v(vc, pose, 1, 0, 1, 1, 1, r, g, b, a);
            v(vc, pose, 0, 0, 1, 0, 1, r, g, b, a);

            // 后面 (z = 0)
            v(vc, pose, 0, 0, 0, 0, 1, r, g, b, a);
            v(vc, pose, 1, 0, 0, 1, 1, r, g, b, a);
            v(vc, pose, 1, 1, 0, 1, 0, r, g, b, a);
            v(vc, pose, 0, 1, 0, 0, 0, r, g, b, a);

            // 左面 (x = 0)
            v(vc, pose, 0, 0, 0, 0, 1, r, g, b, a);
            v(vc, pose, 0, 1, 0, 0, 0, r, g, b, a);
            v(vc, pose, 0, 1, 1, 1, 0, r, g, b, a);
            v(vc, pose, 0, 0, 1, 1, 1, r, g, b, a);

            // 右面 (x = 1)
            v(vc, pose, 1, 0, 1, 1, 1, r, g, b, a);
            v(vc, pose, 1, 1, 1, 1, 0, r, g, b, a);
            v(vc, pose, 1, 1, 0, 0, 0, r, g, b, a);
            v(vc, pose, 1, 0, 0, 0, 1, r, g, b, a);
        }

        private void v(VertexConsumer vc, PoseStack.Pose pose,
                       float x, float y, float z, float u, float uv,
                       float r, float gr, float b, float a) {
            vc.addVertex(pose, x, y, z)
                    .setColor(r, gr, b, a)
                    .setUv(u, uv)
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(LightTexture.FULL_BRIGHT)
                    .setNormal(pose, 0, 1, 0);
        }

        @Override
        public ResourceLocation getTextureLocation(OreHighlightEntity entity) {
            return TEXTURE;
        }
    }
}
