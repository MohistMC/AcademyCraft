package com.mohistmc.academy.client.gui;

import com.mohistmc.academy.client.KeyInputHandler;
import com.mohistmc.academy.network.SetSkillSlotPacket;
import com.mohistmc.academy.skill.AcademyAttachments;
import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.Skill;
import com.mohistmc.academy.skill.SkillPreset;
import com.mohistmc.academy.skill.SkillRegistry;
import com.mohistmc.academy.skill.SkillType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

@OnlyIn(Dist.CLIENT)
public class SkillSlotGui extends Screen {

    private static final int GUI_WIDTH = 280;
    private static final int GUI_HEIGHT = 180;
    private static final int SLOT_SIZE = 48;
    private static final int SLOT_GAP = 12;
    private static final int TAB_WIDTH = 50;
    private static final int TAB_HEIGHT = 18;
    private static final int TAB_GAP = 4;
    private static final int DROPDOWN_ITEM_HEIGHT = 16;
    private static final int DROPDOWN_WIDTH = 100;
    private static final int DROPDOWN_PADDING = 2;

    private static final int COLOR_BG = 0xCC101020;
    private static final int COLOR_SLOT_BG = 0xFF2c3e50;
    private static final int COLOR_SLOT_BORDER = 0xFF34495e;
    private static final int COLOR_SLOT_ACTIVE = 0xFF3498db;
    private static final int COLOR_SLOT_HOVER = 0x44FFFFFF;
    private static final int COLOR_TAB_BG = 0xFF34495e;
    private static final int COLOR_TAB_ACTIVE = 0xFF2ecc71;
    private static final int COLOR_TEXT_WHITE = 0xFFFFFFFF;
    private static final int COLOR_TEXT_GRAY = 0xFF999999;
    private static final int COLOR_TEXT_GREEN = 0xFF2ecc71;
    private static final int COLOR_DROPDOWN_BG = 0xEE1a1a2e;
    private static final int COLOR_DROPDOWN_BORDER = 0xFF3498db;
    private static final int COLOR_DROPDOWN_HOVER = 0xFF34495e;
    private static final int COLOR_DROPDOWN_CLEAR = 0xFFe74c3c;

    private int guiLeft;
    private int guiTop;
    private int viewPreset;
    private int hoveredSlot = -1;
    private int hoveredTab = -1;

    private int dropdownSlot = -1;
    private int dropdownX;
    private int dropdownY;
    private List<DropdownEntry> dropdownEntries = new ArrayList<>();
    private int hoveredDropdownItem = -1;

    public SkillSlotGui() {
        super(Component.literal("技能槽"));
    }

    @Override
    protected void init() {
        super.init();
        this.guiLeft = (this.width - GUI_WIDTH) / 2;
        this.guiTop = (this.height - GUI_HEIGHT) / 2;
        this.dropdownSlot = -1;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            PlayerAbilityData data = mc.player.getData(AcademyAttachments.PLAYER_ABILITY);
            this.viewPreset = data.getCurrentPresetIndex();
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 300);

        graphics.fill(guiLeft, guiTop, guiLeft + GUI_WIDTH, guiTop + GUI_HEIGHT, COLOR_BG);

        hoveredSlot = -1;
        hoveredTab = -1;
        hoveredDropdownItem = -1;

        drawTabs(graphics, mouseX, mouseY);
        drawSlots(graphics, mouseX, mouseY);
        drawHint(graphics);

        if (dropdownSlot >= 0) {
            drawDropdown(graphics, mouseX, mouseY);
        }

        if (hoveredSlot >= 0 && dropdownSlot < 0) {
            drawSlotTooltip(graphics, mouseX, mouseY);
        }

        graphics.pose().popPose();
    }

    private void drawTabs(GuiGraphics graphics, int mouseX, int mouseY) {
        int tabStartX = guiLeft + 10;
        int tabY = guiTop + 8;

        for (int i = 0; i < PlayerAbilityData.PRESET_COUNT; i++) {
            int tabX = tabStartX + i * (TAB_WIDTH + TAB_GAP);
            boolean isHovered = mouseX >= tabX && mouseX < tabX + TAB_WIDTH
                    && mouseY >= tabY && mouseY < tabY + TAB_HEIGHT;
            if (isHovered) hoveredTab = i;

            boolean isActive = (i == viewPreset);
            int bgColor = isActive ? COLOR_TAB_ACTIVE : COLOR_TAB_BG;
            if (isHovered && !isActive) bgColor = 0xFF4a6a7e;

            graphics.fill(tabX, tabY, tabX + TAB_WIDTH, tabY + TAB_HEIGHT, bgColor);
            String text = "预设 " + (i + 1);
            int tw = this.font.width(text);
            graphics.drawString(this.font, text, tabX + (TAB_WIDTH - tw) / 2, tabY + 5, COLOR_TEXT_WHITE);
        }
    }

    private void drawSlots(GuiGraphics graphics, int mouseX, int mouseY) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        PlayerAbilityData data = mc.player.getData(AcademyAttachments.PLAYER_ABILITY);
        SkillPreset preset = data.getPreset(viewPreset);

        int totalWidth = SkillPreset.SLOT_COUNT * SLOT_SIZE + (SkillPreset.SLOT_COUNT - 1) * SLOT_GAP;
        int startX = guiLeft + (GUI_WIDTH - totalWidth) / 2;
        int slotY = guiTop + 50;

        KeyMapping[] keys = KeyInputHandler.getSkillKeys();

        for (int i = 0; i < SkillPreset.SLOT_COUNT; i++) {
            int slotX = startX + i * (SLOT_SIZE + SLOT_GAP);
            boolean isHovered = mouseX >= slotX && mouseX < slotX + SLOT_SIZE
                    && mouseY >= slotY && mouseY < slotY + SLOT_SIZE;
            if (isHovered && dropdownSlot < 0) hoveredSlot = i;

            boolean isOpen = (dropdownSlot == i);
            graphics.fill(slotX, slotY, slotX + SLOT_SIZE, slotY + SLOT_SIZE, COLOR_SLOT_BG);

            int borderColor = isOpen ? COLOR_DROPDOWN_BORDER : (isHovered ? COLOR_SLOT_ACTIVE : COLOR_SLOT_BORDER);
            graphics.fill(slotX, slotY, slotX + SLOT_SIZE, slotY + 1, borderColor);
            graphics.fill(slotX, slotY + SLOT_SIZE - 1, slotX + SLOT_SIZE, slotY + SLOT_SIZE, borderColor);
            graphics.fill(slotX, slotY, slotX + 1, slotY + SLOT_SIZE, borderColor);
            graphics.fill(slotX + SLOT_SIZE - 1, slotY, slotX + SLOT_SIZE, slotY + SLOT_SIZE, borderColor);

            if (isHovered && !isOpen) {
                graphics.fill(slotX + 1, slotY + 1, slotX + SLOT_SIZE - 1, slotY + SLOT_SIZE - 1, COLOR_SLOT_HOVER);
            }

            String skillId = preset.getSlot(i);
            if (skillId != null) {
                Skill skill = SkillRegistry.getSkill(skillId);
                if (skill != null) {
                    String name = Component.translatable(skill.getTranslationKey()).getString();
                    int maxChars = SLOT_SIZE / 5;
                    if (name.length() > maxChars) {
                        name = name.substring(0, maxChars - 1) + "..";
                    }
                    int tw = this.font.width(name);
                    graphics.drawString(this.font, name, slotX + (SLOT_SIZE - tw) / 2, slotY + (SLOT_SIZE - 8) / 2, COLOR_TEXT_WHITE);

                    float prof = data.getProficiency(skillId);
                    int profBarW = (int) ((SLOT_SIZE - 8) * prof);
                    graphics.fill(slotX + 4, slotY + SLOT_SIZE - 6, slotX + 4 + profBarW, slotY + SLOT_SIZE - 4, COLOR_TEXT_GREEN);
                }
            } else {
                String empty = "空";
                int tw = this.font.width(empty);
                graphics.drawString(this.font, empty, slotX + (SLOT_SIZE - tw) / 2, slotY + (SLOT_SIZE - 8) / 2, COLOR_TEXT_GRAY);
            }

            String keyLabel = i < keys.length ? keys[i].getTranslatedKeyMessage().getString() : "?";
            String display = "§7[" + keyLabel + "]";
            int kw = this.font.width(display);
            graphics.drawString(this.font, display, slotX + (SLOT_SIZE - kw) / 2, slotY + SLOT_SIZE + 4, COLOR_TEXT_GRAY);
        }
    }

    private void drawDropdown(GuiGraphics graphics, int mouseX, int mouseY) {
        int totalHeight = dropdownEntries.size() * DROPDOWN_ITEM_HEIGHT + DROPDOWN_PADDING * 2;

        graphics.fill(dropdownX - 1, dropdownY - 1, dropdownX + DROPDOWN_WIDTH + 1, dropdownY + totalHeight + 1, COLOR_DROPDOWN_BORDER);
        graphics.fill(dropdownX, dropdownY, dropdownX + DROPDOWN_WIDTH, dropdownY + totalHeight, COLOR_DROPDOWN_BG);

        int itemY = dropdownY + DROPDOWN_PADDING;
        for (int i = 0; i < dropdownEntries.size(); i++) {
            DropdownEntry entry = dropdownEntries.get(i);
            int ix = dropdownX + DROPDOWN_PADDING;
            int iy = itemY + i * DROPDOWN_ITEM_HEIGHT;
            boolean isHovered = mouseX >= ix && mouseX < ix + DROPDOWN_WIDTH - DROPDOWN_PADDING * 2
                    && mouseY >= iy && mouseY < iy + DROPDOWN_ITEM_HEIGHT;
            if (isHovered) hoveredDropdownItem = i;

            if (isHovered) {
                graphics.fill(ix, iy, ix + DROPDOWN_WIDTH - DROPDOWN_PADDING * 2, iy + DROPDOWN_ITEM_HEIGHT, COLOR_DROPDOWN_HOVER);
            }

            String label = entry.skillId == null ? "§c清空槽位" : entry.displayName;
            int textColor = entry.skillId == null ? COLOR_DROPDOWN_CLEAR : COLOR_TEXT_WHITE;
            int maxW = DROPDOWN_WIDTH - DROPDOWN_PADDING * 4;
            if (this.font.width(label) > maxW) {
                while (this.font.width(label + "..") > maxW && !label.isEmpty()) {
                    label = label.substring(0, label.length() - 1);
                }
                label += "..";
            }
            graphics.drawString(this.font, label, ix + 4, iy + 4, textColor);
        }
    }

    private void drawHint(GuiGraphics graphics) {
        String hint = "左键: 选择技能  右键: 清除";
        int tw = this.font.width(hint);
        graphics.drawString(this.font, hint, guiLeft + (GUI_WIDTH - tw) / 2, guiTop + GUI_HEIGHT - 16, COLOR_TEXT_GRAY);
    }

    private void drawSlotTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        PlayerAbilityData data = mc.player.getData(AcademyAttachments.PLAYER_ABILITY);
        SkillPreset preset = data.getPreset(viewPreset);

        String skillId = preset.getSlot(hoveredSlot);
        List<Component> tooltip = new ArrayList<>();

        if (skillId != null) {
            Skill skill = SkillRegistry.getSkill(skillId);
            if (skill != null) {
                tooltip.add(Component.translatable(skill.getTranslationKey()));
                tooltip.add(Component.literal("§f等级: " + skill.getLevel() + "  类型: " + (skill.getType() == SkillType.PASSIVE ? "被动" : "主动")));
                if (skill.getBaseCpCost() > 0) {
                    tooltip.add(Component.literal("§b计算力: " + (int) skill.getBaseCpCost() + "  §c过载: " + (int) skill.getBaseOverload()));
                }
                float prof = data.getProficiency(skillId);
                tooltip.add(Component.literal("§e熟练度: " + String.format("%.1f%%", prof * 100)));
                tooltip.add(Component.empty());
                tooltip.add(Component.literal("§7左键选择 / 右键清除"));
            }
        } else {
            tooltip.add(Component.literal("§7空槽位"));
            tooltip.add(Component.literal("§7左键选择技能"));
        }

        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 400);
        graphics.renderTooltip(this.font, tooltip, Optional.empty(), mouseX, mouseY);
        graphics.pose().popPose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return super.mouseClicked(mouseX, mouseY, button);

        if (dropdownSlot >= 0) {
            if (hoveredDropdownItem >= 0 && hoveredDropdownItem < dropdownEntries.size()) {
                DropdownEntry entry = dropdownEntries.get(hoveredDropdownItem);
                PlayerAbilityData data = mc.player.getData(AcademyAttachments.PLAYER_ABILITY);
                if (entry.skillId == null) {
                    data.clearSlot(viewPreset, dropdownSlot);
                    PacketDistributor.sendToServer(new SetSkillSlotPacket(viewPreset, dropdownSlot, ""));
                } else {
                    data.setSlot(viewPreset, dropdownSlot, entry.skillId);
                    PacketDistributor.sendToServer(new SetSkillSlotPacket(viewPreset, dropdownSlot, entry.skillId));
                }
                mc.player.setData(AcademyAttachments.PLAYER_ABILITY, data);
            }
            dropdownSlot = -1;
            return true;
        }

        if (hoveredTab >= 0) {
            viewPreset = hoveredTab;
            return true;
        }

        if (hoveredSlot >= 0) {
            if (button == 1) {
                PlayerAbilityData data = mc.player.getData(AcademyAttachments.PLAYER_ABILITY);
                data.clearSlot(viewPreset, hoveredSlot);
                mc.player.setData(AcademyAttachments.PLAYER_ABILITY, data);
                PacketDistributor.sendToServer(new SetSkillSlotPacket(viewPreset, hoveredSlot, ""));
                return true;
            }

            if (button == 0) {
                openDropdown(hoveredSlot, mc);
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void openDropdown(int slotIndex, Minecraft mc) {
        PlayerAbilityData data = mc.player.getData(AcademyAttachments.PLAYER_ABILITY);
        SkillPreset preset = data.getPreset(viewPreset);
        String currentSkillId = preset.getSlot(slotIndex);

        Set<String> usedSkills = new HashSet<>();
        for (int i = 0; i < SkillPreset.SLOT_COUNT; i++) {
            if (i == slotIndex) continue;
            String sid = preset.getSlot(i);
            if (sid != null) usedSkills.add(sid);
        }

        dropdownEntries.clear();
        dropdownEntries.add(new DropdownEntry(null, "清空槽位"));

        List<String> learned = getLearnedActiveSkills(data);
        for (String skillId : learned) {
            if (usedSkills.contains(skillId)) continue;
            Skill skill = SkillRegistry.getSkill(skillId);
            if (skill == null) continue;
            String name = Component.translatable(skill.getTranslationKey()).getString();
            dropdownEntries.add(new DropdownEntry(skillId, name));
        }

        if (currentSkillId != null && usedSkills.contains(currentSkillId)) {
            Skill skill = SkillRegistry.getSkill(currentSkillId);
            if (skill != null) {
                String name = Component.translatable(skill.getTranslationKey()).getString();
                dropdownEntries.add(new DropdownEntry(currentSkillId, name + " (当前)"));
            }
        }

        int totalWidth = SkillPreset.SLOT_COUNT * SLOT_SIZE + (SkillPreset.SLOT_COUNT - 1) * SLOT_GAP;
        int startX = guiLeft + (GUI_WIDTH - totalWidth) / 2;
        int slotY = guiTop + 50;
        dropdownX = startX + slotIndex * (SLOT_SIZE + SLOT_GAP) + SLOT_SIZE / 2 - DROPDOWN_WIDTH / 2;
        dropdownY = slotY + SLOT_SIZE + 14;

        dropdownX = Math.max(guiLeft + 2, Math.min(dropdownX, guiLeft + GUI_WIDTH - DROPDOWN_WIDTH - 2));
        int totalDropdownHeight = dropdownEntries.size() * DROPDOWN_ITEM_HEIGHT + DROPDOWN_PADDING * 2;
        if (dropdownY + totalDropdownHeight > guiTop + GUI_HEIGHT) {
            dropdownY = slotY - totalDropdownHeight;
        }

        dropdownSlot = slotIndex;
    }

    private List<String> getLearnedActiveSkills(PlayerAbilityData data) {
        List<String> result = new ArrayList<>();
        if (!data.hasAbility()) return result;
        for (Skill skill : SkillRegistry.getSkillsByCategory(data.getCurrentAbility())) {
            if (skill.getType() != SkillType.ACTIVE) continue;
            if (skill.getBaseCpCost() <= 0) continue;
            if (data.hasLearnedSkill(skill.getId())) {
                result.add(skill.getId());
            }
        }
        return result;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private record DropdownEntry(String skillId, String displayName) {
    }
}
