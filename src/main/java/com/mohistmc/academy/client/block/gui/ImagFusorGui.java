package com.mohistmc.academy.client.block.gui;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.client.gui.AcademyBaseUI;
import com.mohistmc.academy.utils.RenderUtils;
import com.mohistmc.academy.world.block.entity.ImagFusorBlockEntity;
import com.mohistmc.academy.world.menu.ImagFusorMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ImagFusorGui extends AcademyBaseUI<ImagFusorMenu> {

    private static final ResourceLocation UI_IMAG_FUSOR = ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/guis/ui/ui_imagfusor.png");
    private final Inventory inv;

    public ImagFusorGui(ImagFusorMenu menu, Inventory inv, Component title) {
        super(menu, inv, title, WirelessState.DEFAULT);
        this.inv = inv;
        setRenderWireless(false);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        if (this.menu.pos == null) return;
        BlockEntity be = inv.player.level().getBlockEntity(this.menu.pos);
        if (be instanceof ImagFusorBlockEntity fusor) {
            int fluid = fusor.getFluidAmount();
            int max = fusor.getMaxFluid();
            String text = fluid + "/" + max + " mB";
            graphics.drawString(this.font, text, this.leftPos + 8, this.topPos + 70, 0x404040, false);
        }
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderUtils.renderCenter(176, 187, this.width, this.height, graphics, UI_IMAG_FUSOR);
        RenderSystem.disableBlend();
    }
}
