package com.mohistmc.academy.client.block.entity.render;

import com.mohistmc.academy.world.block.WindGenFan;
import com.mohistmc.academy.world.block.entity.WindGenFanBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;

public class WindGenFanRender implements BlockEntityRenderer<WindGenFanBlockEntity> {

    public WindGenFanRender(BlockEntityRendererProvider.Context ctx) {
    }

    @Override
    public void render(WindGenFanBlockEntity p_112307_, float p_112308_, PoseStack p_112309_, MultiBufferSource p_112310_, int p_112311_, int p_112312_) {
        var blockState = p_112307_.getBlockState();
        Direction facing = blockState.getValue(WindGenFan.FACING);

        p_112309_.pushPose();

        // 根据朝向选择旋转轴：
        // EAST/WEST -> 圆盘在YZ平面，绕X轴
        // NORTH/SOUTH -> 圆盘在XY平面，绕Z轴
        if (facing == Direction.EAST || facing == Direction.WEST) {
            p_112309_.rotateAround(Axis.XN.rotation(p_112307_.rH), 0.5f, 0.5f, 0.5f);
        } else {
            p_112309_.rotateAround(Axis.ZP.rotation(p_112307_.rH), 0.5f, 0.5f, 0.5f);
        }

        // 手动渲染BakedModel，避免与默认渲染重叠
        BakedModel bakedModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(blockState);
        RandomSource random = RandomSource.create();
        List<BakedQuad> quads = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            quads.addAll(bakedModel.getQuads(blockState, direction, random));
        }
        quads.addAll(bakedModel.getQuads(blockState, null, random));

        var consumer = p_112310_.getBuffer(RenderType.cutout());
        for (BakedQuad quad : quads) {
            consumer.putBulkData(p_112309_.last(), quad, 1.0f, 1.0f, 1.0f, 1.0f, p_112311_, p_112312_);
        }

        p_112309_.popPose();

        if (p_112307_.isRunning) {
            // 更新旋转角度（调慢）
            p_112307_.rH += 0.02F;
        }
    }
}
