package com.mohistmc.academy.client.block.entity.render;

import com.mohistmc.academy.client.block.entity.PhaseLiquidBlockEntity;
import com.mohistmc.academy.client.block.entity.model.CatEngineModel;
import com.mohistmc.academy.utils.Resources;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

public class PhaseLiquidRender implements BlockEntityRenderer<PhaseLiquidBlockEntity> {
    private final CatEngineModel model;
    private LiquidBlockRenderer _renderer = new LiquidBlockRenderer();
    private ResourceLocation[] layers = Resources.getEffectSeq("imag_proj_liquid", 3);

    public PhaseLiquidRender(BlockEntityRendererProvider.Context ctx) {
        this.model = new CatEngineModel(ctx.bakeLayer(CatEngineModel.LAYER_LOCATION));
    }

    @Override
    public void render(PhaseLiquidBlockEntity p_112307_, float p_112308_, PoseStack p_112309_, MultiBufferSource souce, int p_112311_, int p_112312_) {
        BlockPos p = p_112307_.getBlockPos();
        double distSq = Minecraft.getInstance().player.distanceToSqr(p.getX() + .5, p.getY() + .5, p.getZ() + .5);
        float alpha = (float) (1 / (1 + 0.2 * Math.pow(distSq, 0.5)));

        if (alpha < 1E-1)
            return;
        GlStateManager._enableBlend();
        GlStateManager._blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        p_112309_.pushPose();
        p_112309_.translate(p.getX(), p.getY(), p.getZ());
        GlStateManager._depthMask(false);
        GlStateManager._disableDepthTest();
        GlStateManager._disableCull();
        RenderSystem.setShaderColor(1, 1, 1, alpha);
        double ht = 1.2 * Math.sqrt(
                3
        );
        GlStateManager._enableBlend();
        drawLayer(p_112309_, p, 0, (float) (-0.3 * ht), 0.3f, 0.2f, 0.7f, alpha);
        drawLayer(p_112309_, p, 1, (float) (0.35 * ht), 0.3f, 0.05f, 0.7f, alpha);
        if (ht > 0.5)
            drawLayer(p_112309_, p, 2, (float) (0.7 * ht), 0.1f, 0.25f, 0.7f, alpha);
        GlStateManager._depthMask(true);
        GlStateManager._enableDepthTest();
        GlStateManager._enableCull();
        p_112309_.popPose();
    }

    private void drawLayer(PoseStack stack, BlockPos p, int layer, float height, float vx, float vz, float density, float alpha) {
        float time = System.currentTimeMillis();
        float du = (time * vx) % 1;
        float dv = (time * vz) % 1;
        ResourceLocation location = layers[layer];
        RenderSystem.setShaderTexture(0, location);
        Matrix4f pos = stack.last().pose();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder consumer = Tesselator.getInstance().getBuilder();
        int x = p.getX(), y = p.getY();
        consumer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        addVertex(pos, consumer, 1f, 68, 0f, du, dv);
        addVertex(pos, consumer, 1f, 68, 0f, du + density, dv);
        addVertex(pos, consumer, 1f, 68, 1f, du + density, dv + density);
        addVertex(pos, consumer, 0f, 68, 1f, du, dv + density);
        BufferUploader.drawWithShader(consumer.end());

    }

    private void addVertex(Matrix4f pos, BufferBuilder consumer, float x, float y, float z, float du, float dv) {
        Vector4f p = pos.transform(new Vector4f(x, y, z, 1.0f));
        consumer.vertex(p.x(), p.y(), p.z()).uv(du, dv).endVertex();
    }
}
