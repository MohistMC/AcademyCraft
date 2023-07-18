package com.mohistmc.academy.client.block.entity.render;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.client.block.entity.CatEngineBlockEntity;
import com.mohistmc.academy.client.block.entity.model.CatEngineModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class CatEngineRender implements BlockEntityRenderer<CatEngineBlockEntity> {
    private final CatEngineModel model;

    public CatEngineRender(BlockEntityRendererProvider.Context ctx) {
        this.model = new CatEngineModel(ctx.bakeLayer(CatEngineModel.LAYER_LOCATION));
    }

    @Override
    public void render(CatEngineBlockEntity p_112307_, float p_112308_, PoseStack p_112309_, MultiBufferSource p_112310_, int p_112311_, int p_112312_) {
        p_112309_.pushPose();
        if (p_112307_.rH >= 360) {
            p_112307_.rH = 0;
        }
        float f1;
        for (f1 = p_112307_.rot - p_112307_.oRot; f1 >= (float) Math.PI; f1 -= ((float) Math.PI * 2F)) {

        }
        while (f1 < -(float) Math.PI) {
            f1 += ((float) Math.PI * 2F);
        }
        float f2 = p_112307_.oRot + f1 * p_112308_;
        p_112309_.rotateAround(Axis.YN.rotation(f2), 0.5f, 0.5f, 0.5f);
        p_112309_.rotateAround(Axis.YN.rotation(90), 0.5f, 0.5f, 0.5f);
        if (p_112307_.enable) {
            p_112309_.rotateAround(Axis.XN.rotation(p_112307_.rH += 0.2), 0.5f, 0.5f, 0.5f);
        }
        ResourceLocation location = new ResourceLocation(AcademyCraft.MODID, "textures/block/cat_engine.png");
        VertexConsumer vertexconsumer = p_112310_.getBuffer(RenderType.entityTranslucentCull(location));
        this.model.renderToBuffer(p_112309_, vertexconsumer, p_112311_, p_112312_, 1.0F, 1.0F, 1.0F, 1.0F);
        p_112309_.popPose();
    }
}
