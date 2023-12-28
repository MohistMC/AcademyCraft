package com.mohistmc.academy.client.block.gui;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.gui.AcademyBaseUI;
import com.mohistmc.academy.utils.RenderUtils;
import com.mohistmc.academy.world.menu.WindGenMainMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WindMainGui extends AcademyBaseUI<WindGenMainMenu> {

    private static final ResourceLocation UI_WIN_MAIN = new ResourceLocation(AcademyCraft.MODID, "textures/guis/ui/ui_windmain.png");
    private final Inventory inv;

    public WindMainGui(WindGenMainMenu menu, Inventory inv, Component p_97743_) {
        super(menu, inv, p_97743_);
        this.inv = inv;
        setRenderWireless(false);
    }


    @Override
    protected void init() {
        super.init();
    }


    @Override
    public void render(GuiGraphics p_97795_, int p_97796_, int p_97797_, float p_97798_) {
        super.renderBackground(p_97795_, p_97796_, p_97797_, p_97798_);
        super.render(p_97795_, p_97796_, p_97797_, p_97798_);
        super.renderTooltip(p_97795_, p_97796_, p_97797_);
    }

    @Override
    protected void renderLabels(GuiGraphics p_97808_, int p_97809_, int p_97810_) {
        //TODO: nothing
    }

    @Override
    public void renderBackground(GuiGraphics p_300197_, int p_297538_, int p_300104_, float p_298759_) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderUtils.renderCenter(176, 187, this.width, this.height, p_300197_, UI_WIN_MAIN);
        RenderSystem.disableBlend();
    }
}
