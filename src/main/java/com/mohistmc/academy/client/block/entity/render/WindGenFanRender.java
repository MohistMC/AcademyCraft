package com.mohistmc.academy.client.block.entity.render;

import com.mohistmc.academy.client.block.entity.WindGenFanBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class WindGenFanRender implements BlockEntityRenderer<WindGenFanBlockEntity> {

    public WindGenFanRender(BlockEntityRendererProvider.Context ctx) {
    }

    @Override
    public void render(WindGenFanBlockEntity p_112307_, float p_112308_, PoseStack p_112309_, MultiBufferSource p_112310_, int p_112311_, int p_112312_) {
        //TODO: 风扇转不起来
    }
}
