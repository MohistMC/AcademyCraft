package com.mohistmc.academy.client.block.gui;

import com.mohistmc.academy.gui.AcademyBaseUI;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WindBaseGui extends AcademyBaseUI {
    public WindBaseGui(Component p_96550_) {
        super(p_96550_);
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(PoseStack stack, int p_96563_, int p_96564_, float p_96565_) {
        super.renderBackground(stack);
        super.render(stack, p_96563_, p_96564_, p_96565_);
    }

}
