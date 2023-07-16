package com.mohistmc.academy.client.block.gui;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.utils.RenderUtils;
import com.mohistmc.academy.world.menu.WindBaseMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.mohistmc.academy.gui.AcademyBaseUI.PARENT_BACKGROUND;
import static com.mohistmc.academy.gui.AcademyBaseUI.UI_INV;

@OnlyIn(Dist.CLIENT)
public class WindBaseGui extends AbstractContainerScreen<WindBaseMenu> {

    private static final ResourceLocation UI_WIN_BASE = new ResourceLocation(AcademyCraft.MODID, "textures/guis/ui/ui_windbase.png");
    private static final ResourceLocation IC_WIN_BASE = new ResourceLocation(AcademyCraft.MODID, "textures/guis/icons/icon_wind_base.png");
    private static final ResourceLocation IC_WIN_MIDDLE = new ResourceLocation(AcademyCraft.MODID, "textures/guis/icons/icon_wind_middle.png");
    private static final ResourceLocation IC_WIN_MAIN = new ResourceLocation(AcademyCraft.MODID, "textures/guis/icons/icon_wind_main.png");
    private final Inventory inv;

    public WindBaseGui(WindBaseMenu menu, Inventory inv, Component p_97743_) {
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
        this.renderTooltip(p_97795_, p_97796_, p_97797_);
    }

    @Override
    protected void renderLabels(PoseStack p_97808_, int p_97809_, int p_97810_) {
        //TODO: nothing
    }

    @Override
    protected void renderBg(PoseStack stack, float p_97788_, int p_97789_, int p_97790_) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        RenderUtils.renderCenter(176, 187, this.width, this.height, stack, PARENT_BACKGROUND);
        RenderUtils.renderCenter(176, 187, this.width, this.height, stack, UI_INV);
        RenderUtils.renderCenter(176, 187, this.width, this.height, stack, UI_WIN_BASE);

        RenderUtils.renderCenterTop(0, 49, 24, 24, this.width, (this.height - 187) / 2, stack, IC_WIN_BASE);// 基座
        RenderUtils.renderCenterTop(0, 31, 24, 24, this.width, (this.height - 187) / 2, stack, IC_WIN_MIDDLE);// 中部
        RenderUtils.renderCenterTop(0, 13, 24, 24, this.width, (this.height - 187) / 2, stack, IC_WIN_MAIN);// 头部


        RenderSystem.disableBlend();
    }
}
