package com.mohistmc.academy.client.entity;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.world.AcademyItems;
import com.mohistmc.academy.world.entity.CoinEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CoinRenderer extends EntityRenderer<CoinEntity> {
    private static final ResourceLocation COIN_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/item/coin_front.png");

    public CoinRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(CoinEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        if (!entity.hasCustomName()) {
            // 应用旋转动画
            float rotationY = entity.getRotationY() + partialTicks * entity.getSpinSpeed();
            poseStack.mulPose(Axis.YP.rotationDegrees(rotationY));
        } else if (entity.isReturning()) {
            // Face the player when returning and has custom name
            Entity cameraEntity = entity.getThrower();
            if (cameraEntity != null) {
                double dx = cameraEntity.getX() - entity.getX();
                double dz = cameraEntity.getZ() - entity.getZ();
                float yaw = (float) (Mth.atan2(dz, dx) * (180.0F / Math.PI)) - 90.0F;
                poseStack.mulPose(Axis.YP.rotationDegrees(-yaw));
            }
        }

        // 如果正在返回，添加一些视觉效果
        if (entity.isReturning()) {
            float scale = 1.0F + Mth.sin((entity.tickCount + partialTicks) * 0.5F) * 0.1F;
            poseStack.scale(scale, scale, scale);
        }

        // 使用物品渲染器渲染硬币物品
        Minecraft.getInstance().getItemRenderer().renderStatic(
                new ItemStack(AcademyItems.COIN.get()),
                ItemDisplayContext.GROUND,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                bufferSource,
                entity.level(),
                0
        );

        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(CoinEntity entity) {
        return COIN_TEXTURE;
    }
}