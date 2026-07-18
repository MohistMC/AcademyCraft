package com.mohistmc.academy.client.block.gui;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.client.gui.AcademyBaseUI;
import com.mohistmc.academy.network.InitMatrixPacket;
import com.mohistmc.academy.utils.RenderUtils;
import com.mohistmc.academy.world.block.entity.MatrixBlockEntity;
import com.mohistmc.academy.world.menu.MatrixMenu;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class MatrixGui extends AcademyBaseUI<MatrixMenu> {

    private static final ResourceLocation UI_MATRIX = ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/guis/ui/ui_matrix.png");

    // SSID输入状态
    private boolean editingSsid = false;
    private final StringBuilder ssidInput = new StringBuilder();
    private boolean editingPassword = false;
    private final StringBuilder passwordInput = new StringBuilder();

    public MatrixGui(MatrixMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        setRenderEnergyTree(false);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        // 由renderBackground处理
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderUtils.renderCenter(176, 187, this.width, this.height, graphics, UI_MATRIX);

        if (this.menu.pos == null) return;

        BlockEntity be = this.inv.player.level().getBlockEntity(this.menu.pos);
        if (!(be instanceof MatrixBlockEntity matrixBe)) return;

        int guiLeft = (this.width - 176) / 2;
        int guiTop = (this.height - 187) / 2;

        // 显示矩阵状态
        String title = matrixBe.isInitialized() ? "§a[已初始化] " + matrixBe.getSSID() : "§c[未初始化]";
        graphics.drawString(this.font, title, guiLeft + 8, guiTop + 5, 0xFF00bcd4);

        // SSID编辑
        String ssidText = "SSID: " + (editingSsid ? ssidInput.toString() + "▌" : matrixBe.getSSID().isEmpty() ? "点击编辑" : matrixBe.getSSID());
        graphics.drawString(this.font, ssidText, guiLeft + 8, guiTop + 20, 0xFFcccccc);

        // 密码编辑
        String pwText = "密码: ";
        if (editingPassword) {
            pwText += "*".repeat(passwordInput.length()) + "▌";
        } else {
            pwText += matrixBe.getPassword().isEmpty() ? "无" : "****";
        }
        graphics.drawString(this.font, pwText, guiLeft + 8, guiTop + 32, 0xFFcccccc);

        // 显示矩阵参数
        graphics.drawString(this.font, "容量: " + matrixBe.getCapacity(), guiLeft + 8, guiTop + 48, 0xFFaaaaaa);
        graphics.drawString(this.font, "带宽: " + (int) matrixBe.getBandwidth() + " IF/t", guiLeft + 8, guiTop + 60, 0xFFaaaaaa);
        graphics.drawString(this.font, "范围: " + (int) matrixBe.getRange() + " 格", guiLeft + 8, guiTop + 72, 0xFFaaaaaa);

        // 所有者信息
        if (matrixBe.getOwnerUUID() != null) {
            graphics.drawString(this.font, "所有者: " + matrixBe.getOwnerUUID().toString().substring(0, 8) + "...", guiLeft + 8, guiTop + 84, 0xFF888888);
        }

        // INIT按钮（当未初始化时显示）
        if (!matrixBe.isInitialized()) {
            int btnX = guiLeft + 62;
            int btnY = guiTop + 100;
            boolean hovered = mouseX >= btnX && mouseX <= btnX + 50 && mouseY >= btnY && mouseY <= btnY + 18;
            graphics.fill(btnX, btnY, btnX + 50, btnY + 18, hovered ? 0xFF2ecc71 : 0xFF27ae60);
            graphics.drawString(this.font, "§lINIT", btnX + 12, btnY + 5, 0xFFFFFFFF);
        }

        RenderSystem.disableBlend();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.menu.pos == null) return super.mouseClicked(mouseX, mouseY, button);

        BlockEntity be = this.inv.player.level().getBlockEntity(this.menu.pos);
        if (!(be instanceof MatrixBlockEntity matrixBe)) return super.mouseClicked(mouseX, mouseY, button);

        int guiLeft = (this.width - 176) / 2;
        int guiTop = (this.height - 187) / 2;

        // 检查INIT按钮点击
        if (!matrixBe.isInitialized()) {
            int btnX = guiLeft + 62;
            int btnY = guiTop + 100;
            if (mouseX >= btnX && mouseX <= btnX + 50 && mouseY >= btnY && mouseY <= btnY + 18) {
                // 发送初始化数据包
                PacketDistributor.sendToServer(new InitMatrixPacket(
                        this.menu.pos,
                        ssidInput.isEmpty() ? "Unnamed" : ssidInput.toString(),
                        passwordInput.toString()
                ));
                return true;
            }
        }

        // 点击SSID区域开始编辑
        if (mouseX >= guiLeft + 8 && mouseX <= guiLeft + 160 && mouseY >= guiTop + 18 && mouseY <= guiTop + 28) {
            editingSsid = true;
            editingPassword = false;
            return true;
        }
        // 点击密码区域开始编辑
        if (mouseX >= guiLeft + 8 && mouseX <= guiLeft + 160 && mouseY >= guiTop + 30 && mouseY <= guiTop + 40) {
            editingPassword = true;
            editingSsid = false;
            return true;
        }

        // 点击其他区域取消编辑
        editingSsid = false;
        editingPassword = false;

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!editingSsid && !editingPassword) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        StringBuilder target = editingSsid ? ssidInput : passwordInput;

        if (keyCode == 259) { // Backspace
            if (!target.isEmpty()) target.deleteCharAt(target.length() - 1);
            return true;
        }
        if (keyCode == 257 || keyCode == 335) { // Enter
            editingSsid = false;
            editingPassword = false;
            return true;
        }
        if (keyCode == 256) { // Escape
            editingSsid = false;
            editingPassword = false;
            return true;
        }

        // 输入字符
        if (target.length() < 24) {
            for (int i = 48; i <= 90; i++) {
                if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), i)) {
                    InputConstants.Key key = InputConstants.getKey(keyCode, scanCode);
                    String name = key.getName().replace("key.keyboard.", "");
                    if (name.length() == 1) {
                        target.append(name);
                    }
                    return true;
                }
            }
            for (int i = 320; i <= 329; i++) {
                if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), i)) {
                    InputConstants.Key key = InputConstants.getKey(keyCode, scanCode);
                    String name = key.getName().replace("key.keyboard.keypad.", "");
                    if (name.length() == 1) {
                        target.append(name);
                    }
                    return true;
                }
            }
        }

        return true;
    }
}
