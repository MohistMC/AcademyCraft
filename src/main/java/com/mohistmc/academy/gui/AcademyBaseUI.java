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

    public static final ResourceLocation PARENT_BACKGROUND = new ResourceLocation(AcademyCraft.MODID, "textures/guis/parent/parent_background.png");
    public static final ResourceLocation UI_INV = new ResourceLocation(AcademyCraft.MODID, "textures/guis/ui/ui_inventory.png");


    protected AcademyBaseUI(Component p_96550_) {
        super(p_96550_);
    }



}
