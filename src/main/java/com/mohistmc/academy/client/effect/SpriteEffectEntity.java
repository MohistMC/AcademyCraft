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
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;

/**
 * 通用粒子精灵特效实体 —— 使用自定义纹理渲染单个 billboard 粒子。
 * <p>
 * 替代所有原版 ParticleTypes 调用，可使用项目内的所有自定义纹理：
 * <ul>
 *   <li>effects/glow_circle.png — 光环/爆炸</li>
 *   <li>effects/glow_line.png — 光线/电弧</li>
 *   <li>effects/md_particle.png — 熔毁粒子</li>
 *   <li>effects/blood_splash/*.png — 血液飞溅</li>
 *   <li>effects/tp_particle.png — 传送粒子</li>
 *   <li>effects/smokes.png — 烟雾</li>
 *   <li>effects/ripple.png — 波纹</li>
 *   <li>effect 动画序列 (arc_burst, arcs, arcw, formula 等)</li>
 * </ul>
 *
 * @author Mgazul
 */
public class SpriteEffectEntity extends Entity {

    private static final EntityDataAccessor<String> TEXTURE_PATH =
            SynchedEntityData.defineId(SpriteEffectEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Float> SPRITE_SIZE =
            SynchedEntityData.defineId(SpriteEffectEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> SPRITE_COLOR =
            SynchedEntityData.defineId(SpriteEffectEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> LIFE_TICKS =
            SynchedEntityData.defineId(SpriteEffectEntity.class, EntityDataSerializers.INT);

    private static final String TEXTURE_PREFIX = "textures/effects/";

    private double motionX, motionY, motionZ;
    private float gravity = 0f;
    private float friction = 1f;
    private boolean glow = true;

    public SpriteEffectEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.noCulling = true;
    }

    // ==================== Builder ====================

    public SpriteEffectEntity setTexture(String path) {
        this.entityData.set(TEXTURE_PATH, path);
        return this;
    }

    public SpriteEffectEntity setSize(float size) {
        this.entityData.set(SPRITE_SIZE, size);
        return this;
    }

    public SpriteEffectEntity setColor(int r, int g, int b, int a) {
        this.entityData.set(SPRITE_COLOR, (a << 24) | (r << 16) | (g << 8) | b);
        return this;
    }

    public SpriteEffectEntity setColor(float r, float g, float b, float a) {
        return setColor((int)(r * 255), (int)(g * 255), (int)(b * 255), (int)(a * 255));
    }

    public SpriteEffectEntity setLife(int ticks) {
        this.entityData.set(LIFE_TICKS, ticks);
        return this;
    }

    public SpriteEffectEntity setMotion(double mx, double my, double mz) {
        this.motionX = mx;
        this.motionY = my;
        this.motionZ = mz;
        return this;
    }

    public SpriteEffectEntity setGravity(float g) {
        this.gravity = g;
        return this;
    }

    public SpriteEffectEntity setFriction(float f) {
        this.friction = f;
        return this;
    }

    public SpriteEffectEntity setGlow(boolean glow) {
        this.glow = glow;
        return this;
    }

    // ==================== Accessors ====================

    public String getTexPath() { return this.entityData.get(TEXTURE_PATH); }
    public float getSpriteSize() { return this.entityData.get(SPRITE_SIZE); }
    public int getSpriteColor() { return this.entityData.get(SPRITE_COLOR); }
    public int getLifeTicks() { return this.entityData.get(LIFE_TICKS); }
    public boolean isGlow() { return glow; }

    public float getLifeProgress() {
        int life = getLifeTicks();
        return life > 0 ? Math.min((float) tickCount / life, 1.0f) : 0f;
    }

    // ==================== Lifecycle ====================

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(TEXTURE_PATH, "glow_circle.png");
        builder.define(SPRITE_SIZE, 0.5f);
        builder.define(SPRITE_COLOR, 0xFFFFFFFF);
        builder.define(LIFE_TICKS, 20);
    }

    @Override
    public void tick() {
        super.tick();

        setPos(getX() + motionX, getY() + motionY, getZ() + motionZ);
        motionY -= gravity;
        motionX *= friction;
        motionY *= friction;
        motionZ *= friction;

        if (tickCount >= getLifeTicks()) {
            discard();
        }
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double dist) {
        return true;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {}

    // ==================== Static Helpers ====================

    /**
     * 在世界中生成一个粒子爆发效果。
     */
    public static void spawnBurst(Level level, EntityType<SpriteEffectEntity> type,
                                   double x, double y, double z, String texture,
                                   int count, float size, int color, int life,
                                   float spread, float gravity) {
        for (int i = 0; i < count; i++) {
            SpriteEffectEntity e = new SpriteEffectEntity(type, level);
            e.setPos(x, y, z);
            e.setTexture(texture).setSize(size).setColor(
                    (color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, (color >> 24) & 0xFF
            ).setLife(life + level.random.nextInt(life / 2))
                    .setMotion(
                            (level.random.nextDouble() - 0.5) * spread,
                            (level.random.nextDouble() - 0.5) * spread,
                            (level.random.nextDouble() - 0.5) * spread
                    ).setGravity(gravity).setGlow(true);
            level.addFreshEntity(e);
        }
    }

    // ==================== Renderer ====================

    public static class Renderer extends EntityRenderer<SpriteEffectEntity> {

        public Renderer(EntityRendererProvider.Context context) {
            super(context);
        }

        @Override
        public void render(SpriteEffectEntity entity, float yaw, float partialTick,
                           PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
            float progress = entity.getLifeProgress();
            if (progress >= 1.0f) return;

            // Alpha: fade in, hold, fade out
            int baseColor = entity.getSpriteColor();
            int a = (baseColor >> 24) & 0xFF;
            float alpha = a / 255f;
            if (progress < 0.15f) alpha *= progress / 0.15f;
            else if (progress > 0.7f) alpha *= (1.0f - progress) / 0.3f;
            if (alpha <= 0.01f) return;

            float r = ((baseColor >> 16) & 0xFF) / 255f;
            float g = ((baseColor >> 8) & 0xFF) / 255f;
            float b = (baseColor & 0xFF) / 255f;

            String texPath = entity.getTexPath();
            ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(
                    AcademyCraft.MODID, "textures/effects/" + texPath);

            RenderType renderType = entity.isGlow()
                    ? RenderType.entityTranslucentEmissive(texture)
                    : RenderType.entityTranslucent(texture);

            VertexConsumer vc = buffer.getBuffer(renderType);
            Matrix4f matrix = poseStack.last().pose();

            poseStack.pushPose();
            float halfSize = entity.getSpriteSize() / 2;

            // Billboard quad: always faces camera
            vc.addVertex(matrix, -halfSize, -halfSize, 0)
                    .setColor(r, g, b, alpha).setUv(0, 1)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setUv2(0xF0, 0xF0).setNormal(0, 0, 1);
            vc.addVertex(matrix, -halfSize, halfSize, 0)
                    .setColor(r, g, b, alpha).setUv(0, 0)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setUv2(0xF0, 0xF0).setNormal(0, 0, 1);
            vc.addVertex(matrix, halfSize, halfSize, 0)
                    .setColor(r, g, b, alpha).setUv(1, 0)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setUv2(0xF0, 0xF0).setNormal(0, 0, 1);
            vc.addVertex(matrix, halfSize, -halfSize, 0)
                    .setColor(r, g, b, alpha).setUv(1, 1)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setUv2(0xF0, 0xF0).setNormal(0, 0, 1);

            poseStack.popPose();
        }

        @Override
        public ResourceLocation getTextureLocation(SpriteEffectEntity entity) {
            return ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID,
                    "textures/effects/" + entity.getTexPath());
        }
    }
}
