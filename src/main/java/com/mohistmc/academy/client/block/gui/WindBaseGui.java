package com.mohistmc.academy.client.block.gui;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.gui.AcademyBaseUI;
import com.mohistmc.academy.utils.RenderUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WindBaseGui extends AcademyBaseUI {

    private static final ResourceLocation UI_WIN_BASE = new ResourceLocation(AcademyCraft.MODID, "textures/guis/ui/ui_windbase.png");
    private static final ResourceLocation IC_WIN_BASE = new ResourceLocation(AcademyCraft.MODID, "textures/guis/icons/icon_wind_base.png");
    private static final ResourceLocation IC_WIN_MIDDLE = new ResourceLocation(AcademyCraft.MODID, "textures/guis/icons/icon_wind_middle.png");
    private static final ResourceLocation IC_WIN_MAIN = new ResourceLocation(AcademyCraft.MODID, "textures/guis/icons/icon_wind_main.png");


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

        RenderUtils.renderCenter(176, 187, this.width, this.height, stack, PARENT_BACKGROUND);
        RenderUtils.renderCenter(176, 187, this.width, this.height, stack, UI_INV);
        RenderUtils.renderCenter(176, 187, this.width, this.height, stack, UI_WIN_BASE);

        RenderUtils.renderCenterTop(0, 49, 24, 24, this.width, (this.height - 187) / 2, stack, IC_WIN_BASE);
        RenderUtils.renderCenterTop(0, 31, 24, 24, this.width, (this.height - 187) / 2, stack, IC_WIN_MIDDLE);
        RenderUtils.renderCenterTop(0, 13, 24, 24, this.width, (this.height - 187) / 2, stack, IC_WIN_MAIN);

        super.render(stack, p_96563_, p_96564_, p_96565_);
    }

}
