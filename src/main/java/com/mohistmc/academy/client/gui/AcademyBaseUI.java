package com.mohistmc.academy.client.gui;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.capability.AcademyNode;
import com.mohistmc.academy.capability.IFEnergyStorage;
import com.mohistmc.academy.network.ConnectToNodePacket;
import com.mohistmc.academy.network.DisconnectFromNodePacket;
import com.mohistmc.academy.network.NodeConfigPacket;
import com.mohistmc.academy.network.NodeListSyncPacket;
import com.mohistmc.academy.network.RequestNodesPacket;
import com.mohistmc.academy.utils.RenderUtils;
import com.mohistmc.academy.world.block.entity.BaseNodeBlockEntity;
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

    // ==================== UI 状态枚举 ====================

    /** 无线 UI 面板状态 */
    public enum WirelessState {
        /** 默认背包页面 */
        DEFAULT,
        /** 节点列表面板（发电机/耗能设备 连接节点用） */
        WIFI,
        /** 节点方块信息面板（储能节点，可修改名字和密码） */
        NODE
    }

    // ==================== 常量 ====================

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
            BlockPos pos = tag.contains("pos") ? BlockPos.of(tag.getLong("pos")) : null;
            int load = tag.contains("load") ? tag.getInt("load") : 0;
            int capacity = tag.contains("capacity") ? tag.getInt("capacity") : 0;
            result.add(new NodeEntry(name, needAuth, pos, load, capacity));
        }
        return result;
    }

    /** 获取当前已连接的节点索引（-1 表示未连接） */
    private static int getConnectedIndex() {
        if (pendingNodeData == null || !pendingNodeData.contains("connectedIndex")) return -1;
        return pendingNodeData.getInt("connectedIndex");
    }

    /** 清空缓存（在请求新列表前调用） */
    public static void clearNodeCache() {
        pendingNodeData = null;
    }

    /** 节点列表条目 */
    private record NodeEntry(String name, boolean needAuth, BlockPos pos, int load, int capacity) {}

    // ==================== 实例字段 ====================

    public final Inventory inv;
    protected WirelessState wirelessState = WirelessState.DEFAULT;
    /** 记录初始面板类型，用于侧边栏切换时恢复到正确面板 */
    private final WirelessState initialState;
    private boolean renderBg = true;
    private boolean renderWireless = true;
    public int activeNode = -1;
    /** 用户是否主动点击侧边栏切换面板 */
    private boolean panelActive = false;
    private int waitPass = -1;
    private StringBuilder inputPass = new StringBuilder();
    private StringBuilder renameInput = new StringBuilder();
    /** NODE 面板专用密码缓冲区 */
    private final StringBuilder nodePassInput = new StringBuilder();
    /** NODE 面板：当前聚焦的输入框（true=节点名，false=密码） */
    private boolean nodeFocusName = true;
    /** NODE 面板：节点名输入框是否已初始化（防止清空后自动回填） */
    private boolean renameInitialized = false;
    /** 客户端平滑显示的能量值（插值过渡，避免一顿一顿） */
    private double smoothEnergy = 0;
    private boolean renderEnergyTree = false;
    private boolean nodesRequested = false;

    // 缓存的服务端节点列表（实例级）
    private final List<NodeEntry> serverNodes = new ArrayList<>();

    public AcademyBaseUI(T t, Inventory inv, Component title, WirelessState initialState) {
        super(t, inv, title);
        this.inv = inv;
        this.initialState = initialState;
        this.wirelessState = initialState;
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

    // ==================== 辅助方法 ====================

    /** 检查当前方块是否为节点方块（储能） */
    private boolean isNodeBlock() {
        if (this.menu == null || this.menu.pos == null || this.inv == null) return false;
        BlockEntity be = this.inv.player.level().getBlockEntity(this.menu.pos);
        return be instanceof BaseNodeBlockEntity;
    }

    /** 获取当前节点方块实体，若不是节点则返回 null */
    private BaseNodeBlockEntity getNodeBlockEntity() {
        if (!isNodeBlock() || this.menu == null || this.menu.pos == null) return null;
        return (BaseNodeBlockEntity) this.inv.player.level().getBlockEntity(this.menu.pos);
    }

    /** 获取当前节点名称（从方块实体读取） */
    private String getThisNodeName() {
        BaseNodeBlockEntity be = getNodeBlockEntity();
        return be != null ? be.getNodeName() : "Unnamed";
    }

    /** 本地更新节点名称（即时反馈，不等服务端同步） */
    private void updateLocalNodeName(int index, String newName) {
        if (index < 0 || index >= serverNodes.size()) return;
        NodeEntry old = serverNodes.get(index);
        serverNodes.set(index, new NodeEntry(newName, old.needAuth(), old.pos(), old.load(), old.capacity()));
    }

    /** 取消所有输入状态 */
    private void cancelAllInputModes() {
        waitPass = -1;
        inputPass = new StringBuilder();
        renameInput = new StringBuilder();
        nodePassInput.setLength(0);
        nodeFocusName = true;
        renameInitialized = false;
    }

    // ==================== 渲染 ====================

    @Override
    public void renderBg(GuiGraphics var1, float var2, int var3, int var4) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // 无面板激活时渲染默认背包页面
        if (!panelActive) {
            this.renderBackground(var1, var3, var4, var2);
        }

        renderEnergyTreePanel(var1);
        RenderSystem.disableBlend();
    }

    @Override
    public void render(GuiGraphics stack, int mouseX, int mouseY, float p_97798_) {
        // WIFI/NODE 状态：请求节点列表
        if ((this.wirelessState == WirelessState.WIFI || this.wirelessState == WirelessState.NODE)
                && !this.nodesRequested && this.menu.pos != null) {
            this.nodesRequested = true;
            clearNodeCache();
            PacketDistributor.sendToServer(new RequestNodesPacket(this.menu.pos));
        }

        // 从静态缓存读取节点数据
        if ((this.wirelessState == WirelessState.WIFI || this.wirelessState == WirelessState.NODE)
                && this.serverNodes.isEmpty()) {
            this.serverNodes.addAll(getCachedNodes());
            int ci = getConnectedIndex();
            if (ci >= 0 && ci < this.serverNodes.size()) {
                this.activeNode = ci;
            }
        }

        // 无面板激活时渲染基础容器（背包/物品栏），面板激活时隐藏以免叠加
        if (!panelActive) {
            super.render(stack, mouseX, mouseY, p_97798_);
            super.renderTooltip(stack, mouseX, mouseY);
        }
        super.renderBackground(stack, mouseX, mouseY, p_97798_);
        this.renderBg(stack, p_97798_, mouseX, mouseY);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // ====== 左侧图标按钮 ======

        // 背包图标
        float invAlpha = this.isHoveringButton(((this.width - GUI_WIDTH) / 2) - 20, ((this.height - GUI_HEIGHT) / 2), 18, 18, mouseX, mouseY)
                || wirelessState == WirelessState.DEFAULT ? 1 : 0.8f;
        RenderSystem.setShaderColor(1, 1, 1, invAlpha);
        RenderUtils.renderCenterTop(-(GUI_WIDTH / 2) - 10, 0, 18, 18, this.width, (this.height - GUI_HEIGHT) / 2, stack, IC_INV);

        if (this.renderWireless) {
            // 无线/WIFI 图标（节点方块和发电机统一使用同一个图标）
            float sidebarAlpha = this.isHoveringButton(((this.width - GUI_WIDTH) / 2) - 20, ((this.height - GUI_HEIGHT) / 2) + 20, 18, 18, mouseX, mouseY)
                    || wirelessState == WirelessState.WIFI || wirelessState == WirelessState.NODE ? 1 : 0.8f;
            RenderSystem.setShaderColor(1, 1, 1, sidebarAlpha);
            RenderUtils.renderCenterTop(-(GUI_WIDTH / 2) - 10, 20, 18, 18, this.width, (this.height - GUI_HEIGHT) / 2, stack, IC_WIRELESS);

            // ====== WIFI 面板 ======
            if (this.wirelessState == WirelessState.WIFI && panelActive) {
                renderWifiPanel(stack, mouseX, mouseY);
            }

            // ====== NODE 面板 ======
            if (this.wirelessState == WirelessState.NODE && panelActive) {
                renderNodePanel(stack, mouseX, mouseY);
            }
        }
        RenderSystem.disableBlend();
    }

    /** 渲染 WIFI 节点列表面板 */
    private void renderWifiPanel(GuiGraphics stack, int mouseX, int mouseY) {
        // WIFI 面板背景
        RenderUtils.renderCenter(GUI_WIDTH, GUI_HEIGHT, this.width, this.height, stack, PARENT_BACKGROUND);
        RenderUtils.renderCenterTop(-(GUI_WIDTH / 2) + 20, 10, 18, 18, this.width, (this.height - GUI_HEIGHT) / 2, stack, IC_TOMATRIX);
        RenderUtils.renderCenterTop(0, 37, 160, 16, this.width, (this.height - GUI_HEIGHT) / 2, stack, ELEMENT_BG_300_32);
        RenderUtils.renderCenterTop(-(160 / 2) + 16, 39, 11, 11, this.width, (this.height - GUI_HEIGHT) / 2, stack, IC_MATRIX);

        String connectedName = activeNode != -1 && activeNode < serverNodes.size()
                ? "已连接"
                : "未连接";
        RenderUtils.renderText(stack, connectedName, ((this.width - GUI_WIDTH) / 2) + 13, ((this.height - GUI_HEIGHT) / 2) + 30);
        RenderUtils.renderText(stack, "可用", ((this.width - GUI_WIDTH) / 2) + 13, ((this.height - GUI_HEIGHT) / 2) + 55);

        // 上下翻页按钮（确保Blend开启，因为箭头纹理是alpha-mask纯白纹理）
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
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

        // 可用节点列表
        int availIndex = 0;
        for (int i = 0; i < serverNodes.size(); i++) {
            if (availIndex >= 8) break;
            if (i == activeNode) continue;

            NodeEntry node = serverNodes.get(i);
            RenderSystem.setShaderColor(1, 1, 1, 0.7f);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            if (node.needAuth()) {
                RenderUtils.renderCenterTop(-8, 65 + (availIndex * 13), 11, 11, this.width, (this.height - GUI_HEIGHT) / 2, stack, IC_KEY);
                RenderUtils.renderCenterTop(-5, 62 + (availIndex * 13), 150, 16, this.width, (this.height - GUI_HEIGHT) / 2, stack, ELEMENT_BG_300_32_I);
            } else {
                RenderUtils.renderCenterTop(-5, 62 + (availIndex * 13), 150, 16, this.width, (this.height - GUI_HEIGHT) / 2, stack, ELEMENT_BG_300_32);
            }
            RenderUtils.renderCenterTop(-(160 / 2) + 16 - 4, 65 + (availIndex * 13), 11, 11, this.width, (this.height - GUI_HEIGHT) / 2, stack, IC_MATRIX);

            float cnAlpha = this.isHoveringButton(((this.width - GUI_WIDTH) / 2) + (160 / 2) * 2 - 16 - 6, ((this.height - GUI_HEIGHT) / 2) + 65 + (availIndex * 13), 15, 15, mouseX, mouseY) ? 1 : 0.7f;
            RenderSystem.setShaderColor(1, 1, 1, cnAlpha);
            RenderUtils.renderCenterTop((160 / 2) - 16 - 6, 65 + (availIndex * 13), 11, 11, this.width, (this.height - GUI_HEIGHT) / 2, stack, IC_UNCONNECTED);
            RenderSystem.disableBlend();
            RenderUtils.renderText(stack, node.name(), ((this.width - GUI_WIDTH) / 2) + 32 - 4, ((this.height - GUI_HEIGHT) / 2) + 67 + (availIndex * 13));

            if (waitPass == i) {
                int pwX = this.width / 2 - this.font.width(inputPass.toString()) / 2;
                RenderUtils.renderText(stack, inputPass.toString(), pwX, ((this.height - GUI_HEIGHT) / 2) + 67 + (availIndex * 13));
            }

            availIndex++;
        }
    }

    /** 渲染 NODE 节点信息面板 */
    private void renderNodePanel(GuiGraphics stack, int mouseX, int mouseY) {
        RenderUtils.renderCenter(GUI_WIDTH, GUI_HEIGHT, this.width, this.height, stack, PARENT_BACKGROUND);
        int guiTop = (this.height - GUI_HEIGHT) / 2;
        int panelLeft = (this.width - GUI_WIDTH) / 2;

        // ====== 头部 ======
        RenderUtils.renderText(stack, "节点信息", panelLeft + 13, guiTop + 12);

        // ====== 节点名输入框 ======
        if (!renameInitialized) {
            renameInput = new StringBuilder(getThisNodeName());
            renameInitialized = true;
        }
        renderNodeRow(stack, 37, "节点名: " + renameInput.toString(),
                ELEMENT_BG_300_32, nodeFocusName);

        // ====== 密码输入框 ======
        renderNodeRow(stack, 56, "密码: " + nodePassInput.toString(),
                ELEMENT_BG_300_32, !nodeFocusName);

        // ====== 确认按钮行（文字居中对齐） ======
        boolean hoverSave = isHoveringButton(panelLeft + 40, guiTop + 78, 96, 16, mouseX, mouseY);
        RenderSystem.setShaderColor(1, 1, 1, hoverSave ? 1.0f : 0.7f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderUtils.renderCenterTop(0, 78, 96, 16, this.width, guiTop, stack, ELEMENT_BG_300_32);
        RenderSystem.disableBlend();
        int saveTextX = this.width / 2 - this.font.width("保存节点设置") / 2;
        RenderUtils.renderText(stack, "保存节点设置", saveTextX, guiTop + 82);

        // ====== 节点信息区 ======
        BaseNodeBlockEntity nodeBe = getNodeBlockEntity();
        int baseY = guiTop + 100;

        // 已连接数 / 容量
        int load = 0;
        int cap = 0;
        if (this.menu.pos != null) {
            for (NodeEntry entry : serverNodes) {
                if (entry.pos() != null && entry.pos().equals(this.menu.pos)) {
                    load = entry.load();
                    cap = entry.capacity();
                    break;
                }
            }
        }
        if (cap == 0 && nodeBe != null) cap = nodeBe.getCapacity();

        renderNodeRow(stack, baseY - guiTop, "已连接: " + load + "/" + cap,
                ELEMENT_BG_300_32, false);

        // 能量
        if (nodeBe != null) {
            int energy = (int) nodeBe.getEnergy();
            int maxEnergy = (int) Math.max(1, nodeBe.getMaxEnergy());
            // 能量值平滑插值（客户端视觉过渡）
            double target = energy;
            if (Math.abs(smoothEnergy - target) > 0.5) {
                smoothEnergy += (target - smoothEnergy) * 0.15;
            } else {
                smoothEnergy = target;
            }
            int displayEnergy = (int) Math.round(smoothEnergy);

            renderNodeRow(stack, baseY + 16 - guiTop,
                    "能量: ",
                    ELEMENT_BG_300_32, false);

            int barX = panelLeft + 36;
            int barY = baseY + 21;
            int barW = 120;
            int barH = 6;
            stack.fill(barX, barY, barX + barW, barY + barH, 0xFF2a2a3a);
            int filled = (int) ((long) displayEnergy * barW / maxEnergy);
            if (filled > 0) {
                stack.fill(barX, barY, barX + filled, barY + barH, 0xFFf1c40f);
                // 文字居中在整个进度条（黑色背景+黄色填充）上显示
                String barText = displayEnergy + "/" + maxEnergy + " IF";
                var pose = stack.pose();
                pose.pushPose();
                pose.translate(barX + barW / 2.0f, barY + barH / 2.0f, 0.0f);
                pose.scale(0.5f, 0.5f, 1.0f);
                int tw = this.font.width(barText);
                this.font.drawInBatch(barText, (float)(-tw / 2), (float)(-3), 0xFFFFFFFF, true,
                        pose.last().pose(), this.minecraft.renderBuffers().bufferSource(),
                        net.minecraft.client.gui.Font.DisplayMode.NORMAL, 0, 15728880);
                pose.popPose();
            }

            renderNodeRow(stack, baseY + 38 - guiTop,
                    "带宽: " + (int) nodeBe.getBandwidth() + " IF/t  " + "范围: " + (int) nodeBe.getRange() + " 格",
                    ELEMENT_BG_300_32, false);
        }
    }

    /** 在 NODE 面板中渲染一个带背景条的标准行（文字距背景左边 ~20px，匹配 WIFI 面板样式） */
    private void renderNodeRow(GuiGraphics stack, int yOffset, String text, ResourceLocation bg, boolean hovered) {
        int guiTop = (this.height - GUI_HEIGHT) / 2;
        int panelLeft = (this.width - GUI_WIDTH) / 2;
        RenderSystem.setShaderColor(hovered ? 1.0f : 0.5f, hovered ? 1.0f : 0.5f, hovered ? 1.0f : 0.5f, 1.0f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderUtils.renderCenterTop(0, yOffset, 160, 16, this.width, guiTop, stack, bg);
        RenderSystem.disableBlend();
        RenderUtils.renderText(stack, text, panelLeft + 13, guiTop + yOffset + 4);
    }

    private void renderEnergyTreePanel(GuiGraphics graphics) {
        if (!renderEnergyTree || this.menu == null || this.menu.pos == null || this.inv == null) return;
        if (panelActive && this.wirelessState == WirelessState.WIFI) return;

        BlockEntity be = this.inv.player.level().getBlockEntity(this.menu.pos);

        int current, max;
        if (be instanceof IFEnergyStorage storage) {
            current = storage.getEnergyStored();
            max = Math.max(1, storage.getMaxEnergyStored());
        } else if (be instanceof BaseNodeBlockEntity nodeBe) {
            current = (int) nodeBe.getEnergy();
            max = (int) Math.max(1, nodeBe.getMaxEnergy());
        } else {
            return;
        }

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

    // ==================== 输入处理 ====================

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int p_97750_) {
        // 背包图标 -> DEFAULT
        if (this.isHoveringButton(((this.width - GUI_WIDTH) / 2) - 20, ((this.height - GUI_HEIGHT) / 2), 18, 18, mouseX, mouseY)) {
            switchState(WirelessState.DEFAULT);
            panelActive = false;
        }
        if (this.renderWireless) {
            // 侧边栏图标：优先根据当前 wirelessState 决定，回退到 initialState
            if (this.isHoveringButton(((this.width - GUI_WIDTH) / 2) - 20, ((this.height - GUI_HEIGHT) / 2) + 20, 18, 18, mouseX, mouseY)) {
                WirelessState targetState;
                if (this.wirelessState == WirelessState.NODE || this.wirelessState == WirelessState.WIFI) {
                    targetState = this.wirelessState;
                } else {
                    targetState = this.initialState;
                }
                switchState(targetState);
                panelActive = true;
                if (targetState == WirelessState.WIFI) {
                    this.nodesRequested = false;
                }
            }

            if (panelActive) {
                switch (this.wirelessState) {
                    case WIFI -> handleWifiClick(mouseX, mouseY);
                    case NODE -> handleNodeClick(mouseX, mouseY);
                    case DEFAULT -> {}
                }
            }
        }
        if (this.wirelessState == WirelessState.DEFAULT)
            return super.mouseClicked(mouseX, mouseY, p_97750_);
        return true;
    }

    /** WIFI 面板点击处理 */
    private void handleWifiClick(double mouseX, double mouseY) {
        // 断开当前连接
        if (this.isHoveringButton(((this.width - GUI_WIDTH) / 2) + (160 / 2) * 2 - 16, ((this.height - GUI_HEIGHT) / 2) + 39, 15, 15, mouseX, mouseY)) {
            if (activeNode != -1 && this.menu.pos != null) {
                PacketDistributor.sendToServer(new DisconnectFromNodePacket(this.menu.pos));
                activeNode = -1;
                cancelAllInputModes();
            }
        }

        // 点击节点连接
        int availIndex = 0;
        for (int i = 0; i < serverNodes.size(); i++) {
            if (availIndex >= 8) break;
            if (i == activeNode) continue;

            NodeEntry node = serverNodes.get(i);
            if (this.isHoveringButton(((this.width - GUI_WIDTH) / 2) + (160 / 2) * 2 - 16 - 6, ((this.height - GUI_HEIGHT) / 2) + 65 + (availIndex * 13), 15, 15, mouseX, mouseY)) {
                if (node.needAuth()) {
                    waitPass = i;
                    inputPass = new StringBuilder();
                } else if (this.menu.pos != null && node.pos() != null) {
                    PacketDistributor.sendToServer(new ConnectToNodePacket(this.menu.pos, node.pos(), java.util.Optional.empty()));
                    activeNode = i;
                }
            }
            if (node.needAuth() && this.isHoveringButton(((this.width - GUI_WIDTH) / 2) - 10, ((this.height - GUI_HEIGHT) / 2) + 62 + (availIndex * 13), 150, 16, mouseX, mouseY)) {
                waitPass = i;
                inputPass = new StringBuilder();
            }

            availIndex++;
        }
    }

    /** NODE 面板点击处理 */
    private void handleNodeClick(double mouseX, double mouseY) {
        int guiTop = (this.height - GUI_HEIGHT) / 2;
        int panelLeft = (this.width - GUI_WIDTH) / 2;

        // "保存节点设置"按钮 -> 同时保存节点名和密码
        if (this.isHoveringButton(panelLeft + 40, guiTop + 78, 96, 16, mouseX, mouseY)) {
            System.out.println("[AcademyDebug] NodePanel: save button clicked");
            if (this.menu.pos != null && isNodeBlock()) {
                System.out.println("[AcademyDebug] NodePanel: sending NodeConfigPacket pos=" + this.menu.pos
                        + " name=" + renameInput.toString()
                        + " password=" + nodePassInput.toString());
                PacketDistributor.sendToServer(new NodeConfigPacket(
                        this.menu.pos,
                        java.util.Optional.of(renameInput.toString()),
                        java.util.Optional.of(nodePassInput.toString())
                ));
                for (int i = 0; i < serverNodes.size(); i++) {
                    if (serverNodes.get(i).pos() != null && serverNodes.get(i).pos().equals(this.menu.pos)) {
                        updateLocalNodeName(i, renameInput.toString());
                        break;
                    }
                }
                System.out.println("[AcademyDebug] NodePanel: packet sent");
            } else {
                System.out.println("[AcademyDebug] NodePanel: save FAILED menu.pos=" + this.menu.pos + " isNodeBlock=" + isNodeBlock());
            }
            return;
        }

        // 点击节点名输入框 -> 聚焦节点名
        if (this.isHoveringButton(panelLeft, guiTop + 37, 160, 16, mouseX, mouseY)) {
            nodeFocusName = true;
            return;
        }

        // 点击密码输入框 -> 聚焦密码
        if (this.isHoveringButton(panelLeft, guiTop + 56, 160, 16, mouseX, mouseY)) {
            nodeFocusName = false;
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.wirelessState == WirelessState.DEFAULT || !panelActive) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        // Escape 键：取消输入状态
        if (keyCode == 256) {
            if (waitPass != -1) {
                waitPass = -1;
                inputPass = new StringBuilder();
                return true;
            }
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        // 将 keyPressed 事件中的键码转为单个字符，仅触发一次
        String keyChar = null;
        if (keyCode >= 48 && keyCode <= 90) { // 字母 A-Z + 数字 0-9
            InputConstants.Key key = InputConstants.getKey(keyCode, scanCode);
            keyChar = key.getName().replace("key.keyboard.", "");
        } else if (keyCode >= 320 && keyCode <= 329) { // 小键盘数字
            InputConstants.Key key = InputConstants.getKey(keyCode, scanCode);
            keyChar = key.getName().replace("key.keyboard.keypad.", "");
        } else if (keyCode == 32) { // 空格
            keyChar = " ";
        }

        // ====== NODE 面板输入 ======
        if (this.wirelessState == WirelessState.NODE) {
            if (keyCode == 257 || keyCode == 335) { // Enter
                if (this.menu.pos != null && isNodeBlock()) {
                    System.out.println("[AcademyDebug] NodePanel: Enter save name=" + renameInput + " password=" + nodePassInput);
                    PacketDistributor.sendToServer(new NodeConfigPacket(
                            this.menu.pos,
                            java.util.Optional.of(renameInput.toString()),
                            java.util.Optional.of(nodePassInput.toString())
                    ));
                    for (int i = 0; i < serverNodes.size(); i++) {
                        if (serverNodes.get(i).pos() != null && serverNodes.get(i).pos().equals(this.menu.pos)) {
                            updateLocalNodeName(i, renameInput.toString());
                            break;
                        }
                    }
                }
                return true;
            }
            if (keyCode == 259) { // Backspace
                if (nodeFocusName && !renameInput.isEmpty()) {
                    renameInput.deleteCharAt(renameInput.length() - 1);
                } else if (!nodeFocusName && !nodePassInput.isEmpty()) {
                    nodePassInput.deleteCharAt(nodePassInput.length() - 1);
                }
                return true;
            }
            // 字符输入（仅触发一次，长按不会重复）
            if (keyChar != null) {
                StringBuilder targetBuf = nodeFocusName ? renameInput : nodePassInput;
                int maxLen = nodeFocusName ? 20 : 11;
                if (targetBuf.length() < maxLen) {
                    targetBuf.append(keyChar);
                }
                return true;
            }
            return true;
        }

        // ====== WIFI 面板输入 ======
        if (this.wirelessState == WirelessState.WIFI) {
            if (keyCode == 259) { // Backspace
                if (!inputPass.isEmpty()) {
                    inputPass.deleteCharAt(inputPass.length() - 1);
                }
                return true;
            }
            if (keyCode == 257 || keyCode == 335) { // Enter
                if (waitPass != -1 && waitPass < serverNodes.size()) {
                    NodeEntry targetNode = serverNodes.get(waitPass);
                    if (this.menu.pos != null && targetNode.pos() != null) {
                        PacketDistributor.sendToServer(new ConnectToNodePacket(
                                this.menu.pos, targetNode.pos(),
                                java.util.Optional.of(inputPass.toString())
                        ));
                        activeNode = waitPass;
                    }
                    waitPass = -1;
                    inputPass = new StringBuilder();
                }
                return true;
            }
            if (keyChar != null && inputPass.length() < 11) {
                inputPass.append(keyChar);
                return true;
            }
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    /** 切换面板状态 */
    private void switchState(WirelessState newState) {
        if (this.wirelessState != newState) {
            this.wirelessState = newState;
            cancelAllInputModes();
        }
    }
}
