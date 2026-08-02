package com.mohistmc.academy.client.block.entity.render;

import com.mohistmc.academy.utils.Resources;
import com.mohistmc.academy.world.block.entity.PhaseLiquidBlockEntity;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

public class PhaseLiquidRender implements BlockEntityRenderer<PhaseLiquidBlockEntity> {

    /** 液面贴图序列(帧动画) */
    private static final ResourceLocation[] LIQUID_TEXTURES = Resources.getEffectSeq("imag_proj_liquid", 3);

    /** 顶点颜色不透明度 */
    private static final float ALPHA = 0.7f;
    /** 单个液面四边形在贴图上的 UV 跨度 */
    private static final float UV_SPAN = 0.7f;
    /** 距离过远时不再渲染的透明度下限 */
    private static final float MIN_ALPHA = 0.1f;
    /** 液面视觉高度放大系数 */
    private static final float HEIGHT_SCALE = 1.2f;
    /** 距离衰减系数:alpha = 1 / (1 + scale * sqrt(distance)) */
    private static final double FADE_DISTANCE_SCALE = 0.2;
    /** 液体较深时才绘制最上层液面的深度阈值 */
    private static final float TOP_LAYER_THRESHOLD = 0.5f;

    /** 各层渲染状态(含各自纹理),创建一次避免每帧分配 */
    private static final RenderType[] LIQUID_RENDER_TYPES = createRenderTypes();

    public PhaseLiquidRender(BlockEntityRendererProvider.Context context) {
    }

    private static RenderType[] createRenderTypes() {
        RenderType[] renderTypes = new RenderType[LIQUID_TEXTURES.length];
        for (int i = 0; i < LIQUID_TEXTURES.length; i++) {
            renderTypes[i] = renderType(LIQUID_TEXTURES[i]);
        }
        return renderTypes;
    }

    private static RenderType renderType(ResourceLocation texture) {
        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setCullState(RenderStateShard.NO_CULL)
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
                .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                .createCompositeState(false);
        return RenderType.create("imag_proj_liquid",
                DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
                VertexFormat.Mode.QUADS, 256, false, true, state);
    }

    @Override
    public void render(PhaseLiquidBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Level level = blockEntity.getLevel();
        if (level == null) return;

        BlockPos pos = blockEntity.getBlockPos();
        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        double distanceSquared = cameraPos.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        float alpha = (float) (1.0 / (1.0 + FADE_DISTANCE_SCALE * Math.sqrt(distanceSquared)));
        if (alpha < MIN_ALPHA) return;

        FluidState fluidState = level.getFluidState(pos);
        if (fluidState.isEmpty()) return;
        float liquidHeight = HEIGHT_SCALE * (float) Math.sqrt(fluidState.getHeight(level, pos));

        // 三层液面,高度偏移为相对液体深度的比例,速度控制贴图滚动方向与快慢
        drawLayer(poseStack, bufferSource, 0, -0.30f * liquidHeight, 0.30f, 0.20f);
        drawLayer(poseStack, bufferSource, 1, 0.35f * liquidHeight, 0.30f, 0.05f);
        if (liquidHeight > TOP_LAYER_THRESHOLD) {
            drawLayer(poseStack, bufferSource, 2, 0.70f * liquidHeight, 0.10f, 0.25f);
        }
    }

    private void drawLayer(PoseStack poseStack, MultiBufferSource bufferSource, int layer,
                           float height, float speedU, float speedV) {
        double elapsedSeconds = Util.getMillis() / 1000.0;
        float uOffset = (float) ((elapsedSeconds * speedU) % 1);
        float vOffset = (float) ((elapsedSeconds * speedV) % 1);

        VertexConsumer vertexConsumer = bufferSource.getBuffer(LIQUID_RENDER_TYPES[layer]);
        PoseStack.Pose pose = poseStack.last();

        addVertex(vertexConsumer, pose, 0.0F, height, 0.0F, uOffset, vOffset);
        addVertex(vertexConsumer, pose, 1.0F, height, 0.0F, uOffset + UV_SPAN, vOffset);
        addVertex(vertexConsumer, pose, 1.0F, height, 1.0F, uOffset + UV_SPAN, vOffset + UV_SPAN);
        addVertex(vertexConsumer, pose, 0.0F, height, 1.0F, uOffset, vOffset + UV_SPAN);
    }

    private void addVertex(VertexConsumer vertexConsumer, PoseStack.Pose pose,
                           float x, float y, float z, float u, float v) {
        vertexConsumer.addVertex(pose, x, y, z)
                .setColor(1f, 1f, 1f, ALPHA)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(0f, 1f, 0f);
    }
}
