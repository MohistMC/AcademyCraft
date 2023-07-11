package com.mohistmc.academy.gui;

import com.mohistmc.academy.AcademyCraft;
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
