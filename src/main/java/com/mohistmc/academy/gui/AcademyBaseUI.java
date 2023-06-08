package com.mohistmc.academy.gui;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.utils.RenderUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public abstract class AcademyBaseUI extends Screen {

    private static final ResourceLocation PARENT_BACKGROUND = new ResourceLocation(AcademyCraft.MODID, "textures/guis/parent/parent_background.png");

    protected AcademyBaseUI(Component p_96550_) {
        super(p_96550_);
    }


    @Override
    public void renderBackground(PoseStack stack) {
        super.renderBackground(stack);
        RenderSystem.setShaderTexture(0, PARENT_BACKGROUND);
        RenderSystem.enableDepthTest();

        int imageWidth = 176;
        int imageHeight = 187;
        int x = (this.width - imageWidth) / 2;
        int y = (this.height - imageHeight) / 2;
        GuiComponent.blit(stack, x,y, 0, 0.0F, 0.0F, this.width, this.height, imageWidth, imageHeight);
    }
}
