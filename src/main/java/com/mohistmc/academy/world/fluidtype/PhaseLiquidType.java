package com.mohistmc.academy.world.fluidtype;

import com.mohistmc.academy.AcademyCraft;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Consumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathType;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PhaseLiquidType extends FluidType {
    public PhaseLiquidType() {
        super(FluidType.Properties.create()
                .descriptionId("block.academy.phase")
                .lightLevel(8)
                .viscosity(6000)
                .temperature(0)
                .supportsBoating(false)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)
                .density(1)
        );
    }

    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(new IClientFluidTypeExtensions() {
            private static final ResourceLocation UNDERWATER_LOCATION = ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/block/black.png");

            @Override
            public @NotNull ResourceLocation getStillTexture() {
                return UNDERWATER_LOCATION;
            }

            @Override
            public @NotNull ResourceLocation getFlowingTexture() {
                return UNDERWATER_LOCATION;
            }

            @Override
            public ResourceLocation getRenderOverlayTexture(Minecraft mc) {
                return UNDERWATER_LOCATION;
            }

            @Override
            public int getTintColor() {
                return 0xFF000000;
            }

            @Override
            public int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
                return 0xFF000000;
            }

            @Override
            public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape) {

            }

            @Override
            public void renderOverlay(Minecraft mc, PoseStack poseStack) {
                IClientFluidTypeExtensions.super.renderOverlay(mc, poseStack);
            }

        });
    }
}
