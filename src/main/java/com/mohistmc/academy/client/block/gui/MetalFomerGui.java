package com.mohistmc.academy.client.block.gui;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.client.gui.AcademyBaseUI;
import com.mohistmc.academy.client.gui.InfoArea;
import com.mohistmc.academy.crafting.MetalFormerRecipes.Mode;
import com.mohistmc.academy.network.MetalFormerActionMessage;
import com.mohistmc.academy.utils.RenderUtils;
import com.mohistmc.academy.world.block.entity.MetalFomerBlockEntity;
import com.mohistmc.academy.world.menu.MetalFomerMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Locale;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 金属成型机 GUI —— 模式选择、进度条、能量显示、无线节点页。
 * 坐标与 ui_metalformer.png 纹理对齐(模式图标中心 88,16.5 / 进度条 62,49 / 能量区 8,29)。
 */
@OnlyIn(Dist.CLIENT)
public class MetalFomerGui extends AcademyBaseUI<MetalFomerMenu> {

    private static final ResourceLocation UI_METAL_FORMER = ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/guis/ui/ui_metalformer.png");
    private static final ResourceLocation TEX_PROGRESS = ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/guis/progress_metalformer.png");

    /** 与 Mode 枚举顺序对应：PLATE, INCISE, ETCH, REFINE（alpha-mask 白色剪影，渲染时染金色） */
    private static final ResourceLocation[] MODE_ICONS = {
            ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/guis/icons/icon_former_plate.png"),
            ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/guis/icons/icon_former_incise.png"),
            ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/guis/icons/icon_former_etch.png"),
            ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/guis/icons/icon_former_refine.png"),
    };
    private static final ResourceLocation BTN_LEFT = ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/guis/button/button_arrowlefta.png");
    private static final ResourceLocation BTN_RIGHT = ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/guis/button/button_arrowrighta.png");

    // 纹理对齐坐标(渲染坐标)
    private static final int ICON_X = 76, ICON_Y = 5, ICON_SIZE = 24, ICON_SRC = 48;
    private static final int BTN_Y = 9, BTN_SIZE = 16, BTN_SRC = 32;
    private static final int BTN_L_X = 60, BTN_R_X = 100;
    private static final int PROG_X = 60, PROG_Y = 47, PROG_W = 57, PROG_H = 15;
    private static final int PROG_TEX_W = 114, PROG_TEX_H = 30;
    /** 模式图标染金色 (f1c40f) */
    private static final float GOLD_R = 0.945f, GOLD_G = 0.765f, GOLD_B = 0.06f;

    /** 客户端平滑进度(插值过渡,避免逐格跳动) */
    private double smoothProgress = 0;

    public MetalFomerGui(MetalFomerMenu menu, Inventory inv, Component title) {
        super(menu, inv, title, WirelessState.WIFI);
        setRenderWireless(true);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        // 悬停模式图标时显示模式名
        if (isHoveringButton(this.leftPos + ICON_X, this.topPos + ICON_Y, ICON_SIZE, ICON_SIZE, mouseX, mouseY)) {
            Mode mode = menu.getMode();
            String txt = Component.translatable(
                    "gui.academy.metal_former.mode." + mode.name().toLowerCase(Locale.ROOT)).getString();
            int tw = this.font.width(txt);
            int x = ICON_X + 6 - tw / 2;
            int y = ICON_Y + ICON_SIZE + 4;
            graphics.fill(this.leftPos + x - 6, this.topPos + y,
                    this.leftPos + x + tw + 6, this.topPos + y + this.font.lineHeight + 4, 0x80000000);
            graphics.drawString(this.font, txt, this.leftPos + x, this.topPos + y + 2, 0xCCFFFFFF, false);
        }
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderUtils.renderCenter(176, 187, this.width, this.height, graphics, UI_METAL_FORMER);
        RenderSystem.disableBlend();

        drawModeSelector(graphics, mouseX, mouseY);
        drawProgress(graphics);

        // 右侧能量信息面板
        renderEnergyInfoPanel(graphics);
    }

    @Override
    protected void renderEnergyInfoPanel(GuiGraphics graphics) {
        if (this.menu.pos == null) return;
        // 用 Menu 的 ContainerData 同步数据(每 tick 服务端同步)，客户端 BE 的 energy 不实时
        InfoArea info = new InfoArea();
        info.histogram(InfoArea.histEnergy(menu.getEnergy(), (int) MetalFomerBlockEntity.MAX_ENERGY));
        Mode mode = menu.getMode();
        info.property("模式", Component.translatable(
                "gui.academy.metal_former.mode." + mode.name().toLowerCase(Locale.ROOT)).getString());
        info.draw(graphics, this.leftPos, this.topPos);
    }

    private void drawModeSelector(GuiGraphics gg, int mouseX, int mouseY) {
        Mode mode = menu.getMode();
        // 模式图标：白色剪影染金色
        drawIcon(gg, MODE_ICONS[mode.ordinal()],
                this.leftPos + ICON_X, this.topPos + ICON_Y, ICON_SIZE,
                GOLD_R, GOLD_G, GOLD_B, 1.0f, ICON_SRC);
        // 左右切换按钮
        boolean hL = isHoveringButton(this.leftPos + BTN_L_X, this.topPos + BTN_Y, BTN_SIZE, BTN_SIZE, mouseX, mouseY);
        boolean hR = isHoveringButton(this.leftPos + BTN_R_X, this.topPos + BTN_Y, BTN_SIZE, BTN_SIZE, mouseX, mouseY);
        drawIcon(gg, BTN_LEFT, this.leftPos + BTN_L_X, this.topPos + BTN_Y, BTN_SIZE, hL ? 1.0f : 0.8f, BTN_SRC);
        drawIcon(gg, BTN_RIGHT, this.leftPos + BTN_R_X, this.topPos + BTN_Y, BTN_SIZE, hR ? 1.0f : 0.8f, BTN_SRC);
    }

    private void drawProgress(GuiGraphics gg) {
        double target = Math.max(0, Math.min(1, menu.getProgress()));
        if (target <= 0) {
            smoothProgress = 0;
            return;
        }
        // 平滑插值过渡,避免进度逐格跳动
        if (Math.abs(smoothProgress - target) > 0.005) {
            smoothProgress += (target - smoothProgress) * 0.3;
        } else {
            smoothProgress = target;
        }

        int w = (int) Math.round(PROG_W * smoothProgress);
        if (w <= 0) return;
        // 纹理采样宽与绘制宽严格成比例(纹理为 2x),避免取整导致拉伸抖动
        int uWidth = w * PROG_TEX_W / PROG_W;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        gg.blit(TEX_PROGRESS, this.leftPos + PROG_X, this.topPos + PROG_Y, w, PROG_H,
                0f, 0f, uWidth, PROG_TEX_H, PROG_TEX_W, PROG_TEX_H);
        RenderSystem.disableBlend();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 机器页（面板未激活）时响应模式切换按钮
        if (!this.panelActive) {
            if (isHoveringButton(this.leftPos + BTN_L_X, this.topPos + BTN_Y, BTN_SIZE, BTN_SIZE, mouseX, mouseY)) {
                if (this.menu.pos != null) MetalFormerActionMessage.send(this.menu.pos, -1);
                return true;
            }
            if (isHoveringButton(this.leftPos + BTN_R_X, this.topPos + BTN_Y, BTN_SIZE, BTN_SIZE, mouseX, mouseY)) {
                if (this.menu.pos != null) MetalFormerActionMessage.send(this.menu.pos, 1);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
