package com.mohistmc.academy.client.block.entity.render;

import com.mohistmc.academy.client.block.entity.PhaseLiquidBlockEntity;
import com.mohistmc.academy.client.block.entity.model.CatEngineModel;
import com.mohistmc.academy.utils.Resources;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
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
        double ht = 1.2 * Math.sqrt(
                3
        );
        GlStateManager._enableBlend();
        drawLayer(souce, 0, -0.3 * ht, 0.3f, 0.2f, 0.7f, alpha);
        drawLayer(souce, 1, 0.35 * ht, 0.3f, 0.05f, 0.7f, alpha);
        if (ht > 0.5)
            drawLayer(souce, 2, 0.7 * ht, 0.1f, 0.25f, 0.7f, alpha);
        GlStateManager._depthMask(true);
        GlStateManager._enableDepthTest();
        GlStateManager._enableCull();
        p_112309_.popPose();
    }

    private void drawLayer(MultiBufferSource souce, int layer, double height, float vx, float vz, float density, float alpha) {
        float time = System.currentTimeMillis();
        float du = (time * vx) % 1;
        float dv = (time * vz) % 1;
        ResourceLocation location = layers[layer];
        BufferBuilder consumer = (BufferBuilder) souce.getBuffer(RenderType.entityTranslucent(location));
        consumer.vertex(0, height, 0).color(1f, 1f, 1f, alpha).uv(du, dv).uv2(15728880).endVertex();
        consumer.vertex(1, height, 0).color(1f, 1f, 1f, alpha).uv(du + density, dv).uv2(15728880).endVertex();
        consumer.vertex(1, height, 1).color(1f, 1f, 1f, alpha).uv(du + density, dv + density).uv2(15728880).endVertex();
        consumer.vertex(0, height, 1).color(1f, 1f, 1f, alpha).uv(du, dv + density).uv2(15728880).endVertex();
        BufferUploader.draw(consumer.end());

    }
}
