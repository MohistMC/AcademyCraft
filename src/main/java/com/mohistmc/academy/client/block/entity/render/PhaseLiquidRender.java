package com.mohistmc.academy.client.block.entity.render;

import com.mohistmc.academy.client.block.entity.PhaseLiquidBlockEntity;
import com.mohistmc.academy.client.block.entity.model.CatEngineModel;
import com.mohistmc.academy.utils.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
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
    private Tessellator t = Tessellator.instance;

    public PhaseLiquidRender(BlockEntityRendererProvider.Context ctx) {
        this.model = new CatEngineModel(ctx.bakeLayer(CatEngineModel.LAYER_LOCATION));
    }

    @Override
    public void render(PhaseLiquidBlockEntity p_112307_, float p_112308_, PoseStack p_112309_, MultiBufferSource p_112310_, int p_112311_, int p_112312_) {
        BlockPos p = p_112307_.getBlockPos();
        double distSq = Minecraft.getInstance().player.distanceToSqr(p.getX() + .5, p.getY() + .5, p.getZ() + .5);
        double alpha = 1 / (1 + 0.2 * Math.pow(distSq, 0.5));

        if (alpha < 1E-1)
            return;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glPushMatrix();
        GL11.glTranslated(p.getX(), p.getY(), p.getZ());

        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);

        GL11.glColor4d(1, 1, 1, alpha);

        RenderHelper.disableStandardItemLighting();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.defaultTexUnit, 240f, 240f);

        double ht = 1.2 * Math.sqrt(
                1
        );

        GL11.glEnable(GL11.GL_BLEND);
        drawLayer(0, -0.3 * ht, 0.3, 0.2, 0.7);
        drawLayer(1, 0.35 * ht, 0.3, 0.05, 0.7);
        if (ht > 0.5)
            drawLayer(2, 0.7 * ht, 0.1, 0.25, 0.7);

        RenderHelper.enableStandardItemLighting();
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);

        GL11.glPopMatrix();
    }

    private void drawLayer(int layer, double height, double vx, double vz, double density) {
        double time = GameTimer.getTime();
        double du = (time * vx) % 1;
        double dv = (time * vz) % 1;
        RenderUtils.loadTexture(layers[layer]);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
        t.startDrawingQuads();
        t.addVertexWithUV(0, height, 0, du, dv);
        t.addVertexWithUV(1, height, 0, du + density, dv);
        t.addVertexWithUV(1, height, 1, du + density, dv + density);
        t.addVertexWithUV(0, height, 1, du, dv + density);
        t.draw();
    }
}
