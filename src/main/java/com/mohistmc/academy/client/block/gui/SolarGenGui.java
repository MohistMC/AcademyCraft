package com.mohistmc.academy.client.block.gui;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.client.gui.AcademyBaseUI;
import com.mohistmc.academy.utils.RenderUtils;
import com.mohistmc.academy.world.block.entity.SolarGenBlockEntity;
import com.mohistmc.academy.world.menu.SolarGenMenu;
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
public class SolarGenGui extends AcademyBaseUI<SolarGenMenu> {

    private static final ResourceLocation UI_SOLAR_GEN = ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/guis/ui/ui_solargen.png");
    private static final ResourceLocation EFFECT_SOLAR = ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/guis/effect/effect_solar.png");

    // effect_solar.png 尺寸 104x210，垂直排列3个图标，每帧 104x70
    private static final int TEXTURE_WIDTH = 104;
    private static final int TEXTURE_HEIGHT = 210;
    private static final int FRAME_WIDTH = 104;
    private static final int FRAME_HEIGHT = 70;
    // 显示尺寸（缩放到约一半）
    private static final int DRAW_WIDTH = 52;
    private static final int DRAW_HEIGHT = 35;

    public SolarGenGui(SolarGenMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        setRenderEnergyTree(true);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics p_97808_, int p_97809_, int p_97810_) {
        //TODO: nothing
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderUtils.renderCenter(176, 187, this.width, this.height, graphics, UI_SOLAR_GEN);

        // 根据天气和时间渲染状态图标
        renderWeatherIcon(graphics);

        RenderSystem.disableBlend();
    }

    /**
     * 根据当前世界的天气和时间渲染对应的状态图标
     * effect_solar.png 包含3个垂直排列的图标：白天(0)、夜晚(1)、雨天(2)
     */
    private void renderWeatherIcon(GuiGraphics graphics) {
        if (this.inv == null || this.menu.pos == null) return;

        BlockEntity be = this.inv.player.level().getBlockEntity(this.menu.pos);
        if (!(be instanceof SolarGenBlockEntity solarBe)) return;

        var status = solarBe.getStatus();
        int iconIndex = status.ordinal();

        int guiLeft = (this.width - 176) / 2;
        int guiTop = (this.height - 187) / 2;
        int x = guiLeft + (176 - DRAW_WIDTH) / 2;
        int y = guiTop + 25;

        int vOffset = iconIndex * FRAME_HEIGHT;
        graphics.blit(EFFECT_SOLAR, x, y, DRAW_WIDTH, DRAW_HEIGHT, 0, vOffset, FRAME_WIDTH, FRAME_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }
}
