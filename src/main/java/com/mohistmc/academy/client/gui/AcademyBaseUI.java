package com.mohistmc.academy.client.gui;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.capability.AcademyNode;
import com.mohistmc.academy.capability.IFEnergyStorage;
import com.mohistmc.academy.network.ConnectToNodePacket;
import com.mohistmc.academy.network.DisconnectFromNodePacket;
import com.mohistmc.academy.network.NodeListSyncPacket;
import com.mohistmc.academy.network.RequestNodesPacket;
import com.mohistmc.academy.utils.RenderUtils;
import com.mohistmc.academy.world.block.entity.SolarGenBlockEntity;
import com.mohistmc.academy.world.block.entity.WindGenBaseBlockEntity;
import com.mohistmc.academy.world.block.entity.WindGenMainBlockEntity;
import com.mohistmc.academy.world.menu.AcademyMenu;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;

public abstract class AcademyBaseUI<T extends AcademyMenu> extends AbstractContainerScreen<T> {

    public static final int GUI_WIDTH = 176;
    public static final int GUI_HEIGHT = 187;

    private static final ResourceLocation PARENT_BACKGROUND = ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/guis/parent/parent_background.png");

    private static final ResourceLocation IC_INV = ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/guis/icons/icon_inv.png");
    private static final ResourceLocation IC_WIRELESS = ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/guis/icons/icon_wireless.png");
    private static final ResourceLocation IC_TOMATRIX = ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/guis/icons/icon_tomatrix.png");
    private static final ResourceLocation IC_MATRIX = ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/guis/icons/icon_matrix.png");
    private static final ResourceLocation IC_UNCONNECTED = ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/guis/icons/icon_unconnected.png");
    private static final ResourceLocation IC_CONNECTED = ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/guis/icons/icon_connected.png");
    private static final ResourceLocation IC_KEY = ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/guis/icons/icon_key.png");
    private static final ResourceLocation ELEMENT_BG_300_32 = ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/guis/element/element_background300x32.png");
    private static final ResourceLocation ELEMENT_BG_300_32_I = ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/guis/element/element_background300x32_input.png");
    private static final ResourceLocation BTN_ARROW_UP = ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/guis/button/button_arrowupb.png");
    private static final ResourceLocation BTN_ARROW_DOWN = ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/guis/button/button_arrowdownb.png");

    // ==================== 静态节点列表缓存（从 server 同步） ====================

    /** 最近一次收到的节点列表 NBT */
    private static CompoundTag pendingNodeData = null;

    /**
     * 由 NodeListSyncPacket 调用，在渲染线程安全读取。
     */
    public static void receiveNodeList(CompoundTag data) {
        pendingNodeData = data;
    }

    /** 获取缓存的节点列表，若没有则返回空列表 */
    private static List<NodeEntry> getCachedNodes() {
        List<NodeEntry> result = new ArrayList<>();
        if (pendingNodeData == null) return result;
        if (!pendingNodeData.contains("nodes")) return result;
        ListTag list = pendingNodeData.getList("nodes", 10);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag tag = list.getCompound(i);
            String name = tag.getString("name");
            boolean needAuth = tag.getBoolean("needAuth");
            boolean isMatrix = tag.getBoolean("isMatrix");
            result.add(new NodeEntry(name, needAuth, isMatrix));
        }
        return result;
    }

    /** 清空缓存（在请求新列表前调用） */
    public static void clearNodeCache() {
        pendingNodeData = null;
    }

    /** 节点列表条目 */
    private record NodeEntry(String name, boolean needAuth, boolean isMatrix) {}

    // ==================== 实例字段 ====================

    public final Inventory inv;
    private boolean wireless = false;
    private boolean renderBg = true;
    private boolean renderWireless = true;
    public int activeNode = -1;
    private int waitPass = -1;
    private StringBuilder inputPass = new StringBuilder();
    private boolean renderEnergyTree = false;
    private boolean nodesRequested = false; // 是否已向服务端请求过节点

    // 缓存的服务端节点列表（实例级）
    private final List<NodeEntry> serverNodes = new ArrayList<>();

    public AcademyBaseUI(T t, Inventory inv, Component p_97743_) {
        super(t, inv, p_97743_);
        this.inv = inv;
    }

    public void setRenderBg(boolean renderBg) {
        this.renderBg = renderBg;
    }

    public void setRenderWireless(boolean wireless) {
        this.renderWireless = wireless;
    }

    public void setRenderEnergyTree(boolean renderEnergyTree) {
        this.renderEnergyTree = renderEnergyTree;
    }

    @Override
    public void renderBg(GuiGraphics var1, float var2, int var3, int var4) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        if (!this.wireless) {
            // 背包页面
            this.renderBackground(var1, var3, var4, var2);
        } else {
            // 无线 UI
            RenderUtils.renderCenter(GUI_WIDTH, GUI_HEIGHT, this.width, this.height, var1, PARENT_BACKGROUND);
            RenderUtils.renderCenterTop(-(GUI_WIDTH / 2) + 20, 10, 18, 18, this.width, (this.height - GUI_HEIGHT) / 2, var1, IC_TOMATRIX);

            RenderUtils.renderCenterTop(0, 37, 160, 16, this.width, (this.height - GUI_HEIGHT) / 2, var1, ELEMENT_BG_300_32);
            RenderUtils.renderCenterTop(-(160 / 2) + 16, 39, 11, 11, this.width, (this.height - GUI_HEIGHT) / 2, var1, IC_MATRIX);

            RenderUtils.renderText(var1, "Connected", ((this.width - GUI_WIDTH) / 2) + 13, ((this.height - GUI_HEIGHT) / 2) + 30);
            RenderUtils.renderText(var1, "Available", ((this.width - GUI_WIDTH) / 2) + 13, ((this.height - GUI_HEIGHT) / 2) + 55);
        }

        renderEnergyTreePanel(var1);
        RenderSystem.disableBlend();
    }

    @Override
    public void render(GuiGraphics stack, int mouseX, int mouseY, float p_97798_) {
        // 当切换到无线面板时，请求节点列表
        if (this.wireless && !this.nodesRequested && this.menu.pos != null) {
            this.nodesRequested = true;
            clearNodeCache();
            PacketDistributor.sendToServer(new RequestNodesPacket(this.menu.pos));
        }

        // 从静态缓存读取节点数据
        if (this.serverNodes.isEmpty()) {
            this.serverNodes.addAll(getCachedNodes());
        }

        if (!this.wireless)
            super.render(stack, mouseX, mouseY, p_97798_);
        super.renderBackground(stack, mouseX, mouseY, p_97798_);
        super.renderTooltip(stack, mouseX, mouseY);
        this.renderBg(stack, p_97798_, mouseX, mouseY);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // 背包图标(左上1)
        float invAlpha = this.isHoveringButton(((this.width - GUI_WIDTH) / 2) - 20, ((this.height - GUI_HEIGHT) / 2), 18, 18, mouseX, mouseY) || !wireless ? 1 : 0.8f;
        RenderSystem.setShaderColor(1, 1, 1, invAlpha);
        RenderUtils.renderCenterTop(-(GUI_WIDTH / 2) - 10, 0, 18, 18, this.width, (this.height - GUI_HEIGHT) / 2, stack, IC_INV);

        if (this.renderWireless) {
            float wlAlpha = this.isHoveringButton(((this.width - GUI_WIDTH) / 2) - 20, ((this.height - GUI_HEIGHT) / 2) + 20, 18, 18, mouseX, mouseY) || wireless ? 1 : 0.8f;
            RenderSystem.setShaderColor(1, 1, 1, wlAlpha);
            RenderUtils.renderCenterTop(-(GUI_WIDTH / 2) - 10, 20, 18, 18, this.width, (this.height - GUI_HEIGHT) / 2, stack, IC_WIRELESS);

            if (this.wireless) {
                // 上下翻页按钮
                float upAlpha = this.isHoveringButton(((this.width - GUI_WIDTH) / 2) + (160 / 2) * 2 - 5, ((this.height - GUI_HEIGHT) / 2) + 65, 15, 15, mouseX, mouseY) ? 1 : 0.8f;
                RenderSystem.setShaderColor(1, 1, 1, upAlpha);
                RenderUtils.renderCenterTop((160 / 2) - 5, 65, 15, 15, this.width, (this.height - GUI_HEIGHT) / 2, stack, BTN_ARROW_UP);

                float downAlpha = this.isHoveringButton(((this.width - GUI_WIDTH) / 2) + (160 / 2) * 2 - 5, ((this.height - GUI_HEIGHT) / 2) + 65 + (7 * 13), 15, 15, mouseX, mouseY) ? 1 : 0.8f;
                RenderSystem.setShaderColor(1, 1, 1, downAlpha);
                RenderUtils.renderCenterTop((160 / 2) - 5, 65 + (7 * 13), 15, 15, this.width, (this.height - GUI_HEIGHT) / 2, stack, BTN_ARROW_DOWN);

                // 当前连接状态
                float disconnectAlpha = this.isHoveringButton(((this.width - GUI_WIDTH) / 2) + (160 / 2) * 2 - 16, ((this.height - GUI_HEIGHT) / 2) + 39, 15, 15, mouseX, mouseY) ? 1 : 0.8f;
                RenderSystem.setShaderColor(1, 1, 1, disconnectAlpha);
                if (activeNode != -1) {
                    RenderUtils.renderCenterTop((160 / 2) - 16, 39, 11, 11, this.width, (this.height - GUI_HEIGHT) / 2, stack, IC_CONNECTED);
                    RenderSystem.disableBlend();
                    String nodeName = activeNode < serverNodes.size() ? serverNodes.get(activeNode).name() : "Node" + activeNode;
                    RenderUtils.renderText(stack, nodeName, ((this.width - GUI_WIDTH) / 2) + 32, ((this.height - GUI_HEIGHT) / 2) + 41);
                } else {
                    RenderUtils.renderCenterTop((160 / 2) - 16, 39, 11, 11, this.width, (this.height - GUI_HEIGHT) / 2, stack, IC_UNCONNECTED);
                    RenderSystem.disableBlend();
                    RenderUtils.renderText(stack, "未连接", ((this.width - GUI_WIDTH) / 2) + 32, ((this.height - GUI_HEIGHT) / 2) + 41);
                }

                // 节点列表
                for (int i = 0; i < serverNodes.size(); i++) {
                    if (i >= 8) break;
                    NodeEntry node = serverNodes.get(i);
                    RenderSystem.setShaderColor(1, 1, 1, 0.7f);
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();

                    if (node.needAuth()) {
                        RenderUtils.renderCenterTop(-8, 65 + (i * 13), 11, 11, this.width, (this.height - GUI_HEIGHT) / 2, stack, IC_KEY);
                        RenderUtils.renderCenterTop(-5, 62 + (i * 13), 150, 16, this.width, (this.height - GUI_HEIGHT) / 2, stack, ELEMENT_BG_300_32_I);
                    } else {
                        RenderUtils.renderCenterTop(-5, 62 + (i * 13), 150, 16, this.width, (this.height - GUI_HEIGHT) / 2, stack, ELEMENT_BG_300_32);
                    }
                    RenderUtils.renderCenterTop(-(160 / 2) + 16 - 4, 65 + (i * 13), 11, 11, this.width, (this.height - GUI_HEIGHT) / 2, stack, IC_MATRIX);

                    float cnAlpha = this.isHoveringButton(((this.width - GUI_WIDTH) / 2) + (160 / 2) * 2 - 16 - 6, ((this.height - GUI_HEIGHT) / 2) + 65 + (i * 13), 15, 15, mouseX, mouseY) ? 1 : 0.7f;
                    RenderSystem.setShaderColor(1, 1, 1, cnAlpha);
                    if (activeNode == i) {
                        RenderUtils.renderCenterTop((160 / 2) - 16 - 6, 65 + (i * 13), 11, 11, this.width, (this.height - GUI_HEIGHT) / 2, stack, IC_CONNECTED);
                    } else {
                        RenderUtils.renderCenterTop((160 / 2) - 16 - 6, 65 + (i * 13), 11, 11, this.width, (this.height - GUI_HEIGHT) / 2, stack, IC_UNCONNECTED);
                    }
                    RenderSystem.disableBlend();
                    RenderUtils.renderText(stack, node.name(), ((this.width - GUI_WIDTH) / 2) + 32 - 4, ((this.height - GUI_HEIGHT) / 2) + 67 + (i * 13));

                    if (waitPass == i) {
                        StringBuilder sb = new StringBuilder();
                        for (int qw = 0; qw < inputPass.length(); qw++) {
                            sb.append("*");
                        }
                        RenderUtils.renderText(stack, sb.toString(), ((this.width - GUI_WIDTH) / 2) + 85, ((this.height - GUI_HEIGHT) / 2) + 67 + (i * 13));
                    }
                }
            }
        }
        RenderSystem.disableBlend();
    }

    private void renderEnergyTreePanel(GuiGraphics graphics) {
        if (!renderEnergyTree || this.menu == null || this.menu.pos == null || this.inv == null) return;
        if (this.wireless) return;

        BlockEntity be = this.inv.player.level().getBlockEntity(this.menu.pos);
        if (!(be instanceof IFEnergyStorage storage)) return;

        int current = storage.getEnergyStored();
        int max = Math.max(1, storage.getMaxEnergyStored());

        int guiLeft = (this.width - GUI_WIDTH) / 2;
        int guiTop = (this.height - GUI_HEIGHT) / 2;

        int barX = guiLeft + GUI_WIDTH - 20;
        int barY = guiTop + 24;

        // 背景
        graphics.fill(barX, barY, barX + 4, barY + 44, 0xFF2a2a3a);

        // 填充（从下到上）
        int filled = (int) ((long) current * 44 / max);
        graphics.fill(barX, barY + 44 - filled, barX + 4, barY + 44, 0xFFf1c40f);

        // IF 数值
        String energyText = current + "/" + max + " IF";
        graphics.drawString(this.font, energyText, barX + (4 - this.font.width(energyText)) / 2, barY - 10, 0xFFcccccc);

        // IF/t 速率
        String rateText = getEnergyRateText(be);
        graphics.drawString(this.font, rateText, barX + (4 - this.font.width(rateText)) / 2, barY + 46, 0xFFaaaaaa);
    }

    private String getEnergyRateText(BlockEntity be) {
        if (be instanceof SolarGenBlockEntity solarBe) {
            return switch (solarBe.getStatus()) {
                case STRONG -> "3 IF/t";
                case WEAK -> "0.6 IF/t";
                case STOPPED -> "0 IF/t";
            };
        } else if (be instanceof WindGenBaseBlockEntity windBe) {
            return windBe.isValidMain() ? "1 IF/t" : "0 IF/t";
        } else if (be instanceof WindGenMainBlockEntity) {
            return "1 IF/t";
        }
        return "0 IF/t";
    }

    public boolean isHoveringButton(int x, int y, int w, int h, double mx, double my) {
        return ((x + w) > mx && mx > x) && ((y + h) > my && my > y);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int p_97750_) {
        if (this.isHoveringButton(((this.width - GUI_WIDTH) / 2) - 20, ((this.height - GUI_HEIGHT) / 2), 18, 18, mouseX, mouseY)) {
            this.wireless = false;
        }
        if (this.renderWireless) {
            if (this.isHoveringButton(((this.width - GUI_WIDTH) / 2) - 20, ((this.height - GUI_HEIGHT) / 2) + 20, 18, 18, mouseX, mouseY)) {
                this.wireless = true;
                this.nodesRequested = false;
            }
            if (wireless) {
                // 翻页按钮
                // 由于使用服务器数据，简化翻页逻辑
                if (this.isHoveringButton(((this.width - GUI_WIDTH) / 2) + (160 / 2) * 2 - 5, ((this.height - GUI_HEIGHT) / 2) + 65, 15, 15, mouseX, mouseY)) {
                    // 上翻页 - 简化处理
                }
                if (this.isHoveringButton(((this.width - GUI_WIDTH) / 2) + (160 / 2) * 2 - 5, ((this.height - GUI_HEIGHT) / 2) + 65 + (7 * 13), 15, 15, mouseX, mouseY)) {
                    // 下翻页 - 简化处理
                }
                // 断开当前连接
                if (this.isHoveringButton(((this.width - GUI_WIDTH) / 2) + (160 / 2) * 2 - 16, ((this.height - GUI_HEIGHT) / 2) + 39, 15, 15, mouseX, mouseY)) {
                    if (activeNode != -1 && this.menu.pos != null) {
                        PacketDistributor.sendToServer(new DisconnectFromNodePacket(this.menu.pos));
                        activeNode = -1;
                    }
                }
                // 点击节点连接
                for (int i = 0; i < serverNodes.size(); i++) {
                    if (i >= 8) break;
                    NodeEntry node = serverNodes.get(i);
                    if (this.isHoveringButton(((this.width - GUI_WIDTH) / 2) + (160 / 2) * 2 - 16 - 6, ((this.height - GUI_HEIGHT) / 2) + 65 + (i * 13), 15, 15, mouseX, mouseY) && activeNode != i) {
                        if (node.needAuth()) {
                            waitPass = i;
                            inputPass = new StringBuilder();
                        } else if (this.menu.pos != null) {
                            // 直接连接（服务器端验证）
                            PacketDistributor.sendToServer(new ConnectToNodePacket(this.menu.pos, BlockPos.ZERO, java.util.Optional.empty()));
                            activeNode = i;
                        }
                    }
                    if (node.needAuth() && this.isHoveringButton(((this.width - GUI_WIDTH) / 2) - 10, ((this.height - GUI_HEIGHT) / 2) + 62 + (i * 13), 150, 16, mouseX, mouseY) && activeNode != i) {
                        waitPass = i;
                        inputPass = new StringBuilder();
                    }
                }
            }
        }
        if (!this.wireless)
            return super.mouseClicked(mouseX, mouseY, p_97750_);
        return true;
    }

    @Override
    public boolean keyPressed(int p_97765_, int p_97766_, int p_97767_) {
        if (!this.wireless) {
            return super.keyPressed(p_97765_, p_97766_, p_97767_);
        }
        if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 256)) {
            return super.keyPressed(p_97765_, p_97766_, p_97767_);
        }
        if (inputPass.length() < 11) {
            for (int i = 48; i <= 90; i++) {
                if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), i)) {
                    InputConstants.Key key = InputConstants.getKey(p_97765_, p_97766_);
                    inputPass.append(key.getName().replace("key.keyboard.", ""));
                }
            }
            for (int i = 320; i <= 329; i++) {
                if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), i)) {
                    InputConstants.Key key = InputConstants.getKey(p_97765_, p_97766_);
                    inputPass.append(key.getName().replace("key.keyboard.keypad.", ""));
                }
            }
        }

        if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 259)) {
            if (!inputPass.isEmpty()) {
                inputPass.deleteCharAt(inputPass.length() - 1);
            }
        }
        if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 335) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 257)) {
            if (waitPass != -1) {
                // 密码输入完成，发送带密码的连接请求
                if (this.menu.pos != null) {
                    PacketDistributor.sendToServer(new ConnectToNodePacket(
                            this.menu.pos, BlockPos.ZERO,
                            java.util.Optional.of(inputPass.toString())
                    ));
                    activeNode = waitPass;
                }
                waitPass = -1;
                inputPass = new StringBuilder();
            }
        }
        return true;
    }
}
