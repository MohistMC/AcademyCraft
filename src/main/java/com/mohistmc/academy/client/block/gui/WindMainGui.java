package com.mohistmc.academy.client.block.gui;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.gui.AcademyBaseUI;
import com.mohistmc.academy.utils.RenderUtils;
import com.mohistmc.academy.world.menu.WindGenMainMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
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
    }


    @Override
    protected void init() {
        super.init();
    }


    @Override
    public void render(PoseStack p_97795_, int p_97796_, int p_97797_, float p_97798_) {
        super.renderBackground(p_97795_);
        super.render(p_97795_, p_97796_, p_97797_, p_97798_);
        super.renderTooltip(p_97795_, p_97796_, p_97797_);
    }

    @Override
    protected void renderLabels(PoseStack p_97808_, int p_97809_, int p_97810_) {
        //TODO: nothing
    }

    @Override
    public void renderBackground(PoseStack stack, float p_97788_, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderUtils.renderCenter(176, 187, this.width, this.height, stack, UI_WIN_MAIN);
        RenderSystem.disableBlend();
    }
}
