package com.mohistmc.academy.gui;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.utils.RenderUtils;
import com.mohistmc.academy.world.menu.AcademyMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public abstract class AcademyBaseUI<T extends AcademyMenu> extends AbstractContainerScreen<T> {

    private static final ResourceLocation PARENT_BACKGROUND = new ResourceLocation(AcademyCraft.MODID, "textures/guis/parent/parent_background.png");
    private static final ResourceLocation UI_INV = new ResourceLocation(AcademyCraft.MODID, "textures/guis/ui/ui_inventory.png");

    private static final ResourceLocation IC_INV = new ResourceLocation(AcademyCraft.MODID, "textures/guis/icons/icon_inv.png");
    private static final ResourceLocation IC_WIRELESS = new ResourceLocation(AcademyCraft.MODID, "textures/guis/icons/icon_wireless.png");
    private static final ResourceLocation IC_TOMATRIX = new ResourceLocation(AcademyCraft.MODID, "textures/guis/icons/icon_tomatrix.png");
    private static final ResourceLocation IC_MATRIX = new ResourceLocation(AcademyCraft.MODID, "textures/guis/icons/icon_matrix.png");
    private static final ResourceLocation IC_UNCONNECTED = new ResourceLocation(AcademyCraft.MODID, "textures/guis/icons/icon_unconnected.png");
    private static final ResourceLocation IC_CONNECTED = new ResourceLocation(AcademyCraft.MODID, "textures/guis/icons/icon_connected.png");
    private static final ResourceLocation ELEMENT_BG_300_32 = new ResourceLocation(AcademyCraft.MODID, "textures/guis/element/element_background300x32.png");
    private static final ResourceLocation ELEMENT_BG_300_32_I = new ResourceLocation(AcademyCraft.MODID, "textures/guis/element/element_background300x32_input.png");
    private static final ResourceLocation BTN_ARROW_UP = new ResourceLocation(AcademyCraft.MODID, "textures/guis/button/button_arrowupb.png");
    private static final ResourceLocation BTN_ARROW_DOWN = new ResourceLocation(AcademyCraft.MODID, "textures/guis/button/button_arrowdownb.png");
    private boolean wireless = false;
    private boolean renderInv = true;
    private boolean renderBg = true;
    private boolean renderWireless = true;

    protected AcademyBaseUI(T t, Inventory inv, Component p_97743_) {
        super(t, inv, p_97743_);
    }

    public void setRenderInv(boolean renderInv) {
        this.renderInv = renderInv;
    }

    public void setRenderBg(boolean renderBg) {
        this.renderBg = renderBg;
    }

    public void setRenderWireless(boolean wireless) {
        this.renderWireless = wireless;
    }

    @Override
    public void renderBg(PoseStack stack, float p_97788_, int p_97789_, int p_97790_) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        if (renderBg)
            RenderUtils.renderCenter(176, 187, this.width, this.height, stack, PARENT_BACKGROUND);
        if (this.renderInv && !this.wireless)
            RenderUtils.renderCenter(176, 187, this.width, this.height, stack, UI_INV);
        RenderSystem.disableBlend();
        if (!this.wireless) {
            RenderSystem.setShaderColor(1, 1, 1, 1);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            this.renderBackground(stack, p_97788_, p_97789_, p_97790_);
            RenderSystem.disableBlend();
        } else {

            RenderSystem.setShaderColor(1, 1, 1, 0.7f);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderUtils.renderCenter(176, 187, this.width, this.height, stack, PARENT_BACKGROUND);
            RenderUtils.renderCenterTop(-(176 / 2) + 20, 10, 18, 18, this.width, (this.height - 187) / 2, stack, IC_TOMATRIX);
            RenderUtils.renderCenterTop(0, 37, 160, 16, this.width, (this.height - 187) / 2, stack, ELEMENT_BG_300_32);
            RenderUtils.renderCenterTop(-(160 / 2) + 16, 39, 11, 11, this.width, (this.height - 187) / 2, stack, IC_MATRIX);
            RenderUtils.renderCenterTop((160 / 2) - 16, 39, 11, 11, this.width, (this.height - 187) / 2, stack, IC_UNCONNECTED);

            // TODO: 鼠标悬浮这俩按钮上高亮/点击
            RenderUtils.renderCenterTop((160 / 2) - 5, 65, 15, 15, this.width, (this.height - 187) / 2, stack, BTN_ARROW_UP);
            RenderUtils.renderCenterTop((160 / 2) - 5, 65 + (7 * 13), 15, 15, this.width, (this.height - 187) / 2, stack, BTN_ARROW_DOWN);

            RenderSystem.disableBlend();
            RenderUtils.renderText(stack, "Connected", ((this.width - 176) / 2) + 13, ((this.height - 187) / 2) + 30);
            RenderUtils.renderText(stack, "未连接", ((this.width - 176) / 2) + 32, ((this.height - 187) / 2) + 41);
            RenderUtils.renderText(stack, "Available", ((this.width - 176) / 2) + 13, ((this.height - 187) / 2) + 55);
            addOrSetNode(stack, "TestNode", 0, null);
            addOrSetNode(stack, "TestNode1", 1, null);
        }

    }


    private void addOrSetNode(PoseStack stack, String name, int index, BlockPos pos) {
        // TODO: 记录节点所在位置，限制绘制行数
        RenderSystem.setShaderColor(1, 1, 1, 0.7f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderUtils.renderCenterTop(-5, 62 + (index * 13), 150, 16, this.width, (this.height - 187) / 2, stack, ELEMENT_BG_300_32);
        RenderUtils.renderCenterTop(-(160 / 2) + 16 - 4, 65 + (index * 13), 11, 11, this.width, (this.height - 187) / 2, stack, IC_MATRIX);
        RenderUtils.renderCenterTop((160 / 2) - 16 - 6, 65 + (index * 13), 11, 11, this.width, (this.height - 187) / 2, stack, IC_UNCONNECTED);
        RenderSystem.disableBlend();
        RenderUtils.renderText(stack, name, ((this.width - 176) / 2) + 32 - 4, ((this.height - 187) / 2) + 67 + (index * 13));

    }


    public abstract void renderBackground(PoseStack stack, float p_97788_, int mouseX, int mouseY);

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float p_97798_) {
        if (!this.wireless)
            super.render(stack, mouseX, mouseY, p_97798_);
        this.renderBg(stack, p_97798_, mouseX, mouseY);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        if (this.isHoveringButton(((this.width - 176) / 2) - 20, ((this.height - 187) / 2), 18, 18, mouseX, mouseY) || !wireless) {
            RenderSystem.setShaderColor(1, 1, 1, 1);
        } else {
            RenderSystem.setShaderColor(1, 1, 1, 0.8f);

        }
        if (this.renderWireless) {
            if (this.isHoveringButton(((this.width - 176) / 2) - 20, ((this.height - 187) / 2) + 20, 18, 18, mouseX, mouseY) || wireless) {
                //System.out.println("wireless");
                RenderSystem.setShaderColor(1, 1, 1, 1);
            } else {
                RenderSystem.setShaderColor(1, 1, 1, 0.8f);
            }
            // TODO: 高亮列表连接按钮
            RenderUtils.renderCenterTop(-(176 / 2) - 10, 20, 18, 18, this.width, (this.height - 187) / 2, stack, IC_WIRELESS);
        }
        RenderUtils.renderCenterTop(-(176 / 2) - 10, 0, 18, 18, this.width, (this.height - 187) / 2, stack, IC_INV);
        RenderSystem.disableBlend();
    }


    public boolean isHoveringButton(int x, int y, int w, int h, double mx, double my) {
        //System.out.println("mx: " + mx + " x:" + x + " cx:" + (x + w) + " w:" + w + " h:" + h);
        //System.out.println("my: " + my + " y:" + y + " cx:" + (y + h) + " w:" + w + " h:" + h);
        return ((x + w) > mx && mx > x) && ((y + h) > my && my > y);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int p_97750_) {
        // TODO: 列表连接按钮点击
        if (this.isHoveringButton(((this.width - 176) / 2) - 20, ((this.height - 187) / 2), 18, 18, mouseX, mouseY)) {
            this.wireless = false;
        }
        if (this.renderWireless) {
            if (this.isHoveringButton(((this.width - 176) / 2) - 20, ((this.height - 187) / 2) + 20, 18, 18, mouseX, mouseY)) {
                this.wireless = true;
            }
        }
        if (!this.wireless)
            return super.mouseClicked(mouseX, mouseY, p_97750_);
        return true;
    }
}
