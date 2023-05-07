package com.mohistmc.academy.client.block.entity.render;

import com.mohistmc.academy.client.block.entity.PhaseLiquidBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.core.Direction;
import org.joml.Matrix4f;

public class PhaseLiquidRender extends TheEndPortalRenderer<PhaseLiquidBlockEntity> {

    public PhaseLiquidRender(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
        //this.render = new TheEndGatewayRenderer(ctx);
    }

    @Override
    public void render(PhaseLiquidBlockEntity entity, float p_112308_, PoseStack stack, MultiBufferSource source, int p_112311_, int p_112312_) {
        //参考: TheEndGatewayRenderer
        Matrix4f matrix4f = stack.last().pose();
        this.renderCube(matrix4f, source.getBuffer(this.renderType()));
    }

    private void renderCube(Matrix4f p_254024_, VertexConsumer p_173693_) {
        this.renderFace(p_254024_, p_173693_, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, Direction.SOUTH);
        this.renderFace(p_254024_, p_173693_, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, Direction.NORTH);
        this.renderFace(p_254024_, p_173693_, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.EAST);
        this.renderFace(p_254024_, p_173693_, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.WEST);
        this.renderFace(p_254024_, p_173693_, 0.0F, 1.0F, 0F, 0F, 0.0F, 0.0F, 1.0F, 1.0F, Direction.DOWN);
        this.renderFace(p_254024_, p_173693_, 0.0F, 1.0F, 1F, 1F, 1.0F, 1.0F, 0.0F, 0.0F, Direction.UP);
    }

    private void renderFace(Matrix4f p_254247_, VertexConsumer p_254390_, float p_254147_, float p_253639_, float p_254107_, float p_254109_, float p_254021_, float p_254458_, float p_254086_, float p_254310_, Direction p_253619_) {
        p_254390_.vertex(p_254247_, p_254147_, p_254107_, p_254021_).endVertex();
        p_254390_.vertex(p_254247_, p_253639_, p_254107_, p_254458_).endVertex();
        p_254390_.vertex(p_254247_, p_253639_, p_254109_, p_254086_).endVertex();
        p_254390_.vertex(p_254247_, p_254147_, p_254109_, p_254310_).endVertex();
    }
}
