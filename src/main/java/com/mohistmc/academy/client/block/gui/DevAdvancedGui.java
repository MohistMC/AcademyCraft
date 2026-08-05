package com.mohistmc.academy.client.block.gui;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.client.gui.AcademyBaseUI;
import com.mohistmc.academy.network.ConsoleCommandPacket;
import com.mohistmc.academy.utils.RenderUtils;
import com.mohistmc.academy.world.block.entity.DevAdvancedBlockEntity;
import com.mohistmc.academy.world.menu.DevAdvancedMenu;
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

/**
 * 高级能力开发机 —— 控制台界面。
 */
@OnlyIn(Dist.CLIENT)
public class DevAdvancedGui extends AcademyBaseUI<DevAdvancedMenu> {

    private static final ResourceLocation UI_DEV_ADVANCED = ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/guis/ui/ui_developerright.png");

    private final StringBuilder consoleInput = new StringBuilder();
    private boolean inputActive = false;

    public DevAdvancedGui(DevAdvancedMenu menu, Inventory inv, Component title) {
        super(menu, inv, title, WirelessState.DEFAULT);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        // 由renderBackground处理
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderUtils.renderCenter(176, 187, this.width, this.height, graphics, UI_DEV_ADVANCED);

        if (this.menu.pos == null) return;

        int guiLeft = (this.width - 176) / 2;
        int guiTop = (this.height - 187) / 2;

        // 标题
        graphics.drawString(this.font, "§l高级能力开发机", guiLeft + 8, guiTop + 5, 0xFF00bcd4);

        // 控制台区域
        int consoleX = guiLeft + 8;
        int consoleY = guiTop + 48;
        int consoleW = 140;
        int consoleH = 50;
        graphics.fill(consoleX, consoleY, consoleX + consoleW, consoleY + consoleH, 0xCC000000);

        // 控制台内容
        BlockEntity be = this.inv.player.level().getBlockEntity(this.menu.pos);
        if (be instanceof DevAdvancedBlockEntity dev) {
            String status = dev.isReadyForReset() ? "§a[就绪] 可以重置" : "§e[待机] 放入线圈和因子";
            graphics.drawString(this.font, status, consoleX + 4, consoleY + 4, 0xFFcccccc);

            boolean hasCoil = dev.hasCoil();
            boolean hasFactor = dev.hasFactor();
            String coilStatus = hasCoil ? "§a✓ 线圈" : "§c✗ 线圈";
            String factorStatus = hasFactor ? "§a✓ 因子" : "§c✗ 因子";
            graphics.drawString(this.font, coilStatus, consoleX + 4, consoleY + 16, 0xFFcccccc);
            graphics.drawString(this.font, factorStatus, consoleX + 60, consoleY + 16, 0xFFcccccc);
        }

        // 输入行
        int inputY = consoleY + consoleH + 4;
        String prompt = "> " + consoleInput.toString() + (inputActive ? "▌" : "");
        graphics.drawString(this.font, prompt, consoleX + 4, inputY, 0xFF00ff00);

        // 提示文字
        graphics.drawString(this.font, "§7输入 'learn' 学习技能 | 'reset' 重置能力", consoleX + 4, inputY + 14, 0xFF888888);

        RenderSystem.disableBlend();

        // 右侧能量信息面板
        renderEnergyInfoPanel(graphics);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int guiLeft = (this.width - 176) / 2;
        int guiTop = (this.height - 187) / 2;

        // 点击控制台区域激活输入
        int consoleX = guiLeft + 8;
        int consoleY = guiTop + 48;
        if (mouseX >= consoleX && mouseX <= consoleX + 140 && mouseY >= consoleY && mouseY <= consoleY + 80) {
            inputActive = true;
            return true;
        }

        inputActive = false;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!inputActive) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        if (keyCode == 259) { // Backspace
            if (!consoleInput.isEmpty()) consoleInput.deleteCharAt(consoleInput.length() - 1);
            return true;
        }
        if (keyCode == 257 || keyCode == 335) { // Enter
            if (!consoleInput.isEmpty()) {
                String cmd = consoleInput.toString().trim().toLowerCase();
                if (this.menu.pos != null) {
                    PacketDistributor.sendToServer(new ConsoleCommandPacket(this.menu.pos, cmd));
                }
                consoleInput.setLength(0);
            }
            return true;
        }
        if (keyCode == 256) { // Escape
            inputActive = false;
            return true;
        }

        // 字符输入
        if (consoleInput.length() < 24) {
            for (int i = 48; i <= 90; i++) {
                if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), i)) {
                    InputConstants.Key key = InputConstants.getKey(keyCode, scanCode);
                    String name = key.getName().replace("key.keyboard.", "");
                    if (name.length() == 1) {
                        consoleInput.append(name);
                    }
                    return true;
                }
            }
        }

        return true;
    }
}
