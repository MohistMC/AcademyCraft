package com.mohistmc.academy.client.block.gui;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.client.gui.AcademyBaseUI;
import com.mohistmc.academy.utils.RenderUtils;
import com.mohistmc.academy.world.block.entity.PhaseGenBlockEntity;
import com.mohistmc.academy.world.menu.PhaseGenMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class PhaseGenGui extends AcademyBaseUI<PhaseGenMenu> {

    private static final ResourceLocation UI_PHASE_GEN = ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/guis/ui/ui_phasegen.png");

    public PhaseGenGui(PhaseGenMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        setRenderEnergyTree(true);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics p_97808_, int p_97809_, int p_97810_) {
        // 由 renderBackground 处理
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderUtils.renderCenter(176, 187, this.width, this.height, graphics, UI_PHASE_GEN);

        // 渲染进度条
        renderProgressBar(graphics);

        RenderSystem.disableBlend();
    }

    private void renderProgressBar(GuiGraphics graphics) {
        if (this.inv == null || this.menu.pos == null) return;

        BlockEntity be = this.inv.player.level().getBlockEntity(this.menu.pos);
        if (!(be instanceof PhaseGenBlockEntity phaseBe)) return;

        int guiLeft = (this.width - 176) / 2;
        int guiTop = (this.height - 187) / 2;

        // 进度条位置 (参考其他机器的布局)
        int barX = guiLeft + 76;
        int barY = guiTop + 40;
        int barWidth = 24;
        int barHeight = 30;

        // 背景
        graphics.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF2a2a3a);

        // 填充 - 从下到上
        if (phaseBe.getProgress() > 0 && phaseBe.getProcessTicks() > 0) {
            int filled = (int) ((long) phaseBe.getProgress() * barHeight / phaseBe.getProcessTicks());
            graphics.fill(barX, barY + barHeight - filled, barX + barWidth, barY + barHeight, 0xFF00bcd4);
        }
    }
}
