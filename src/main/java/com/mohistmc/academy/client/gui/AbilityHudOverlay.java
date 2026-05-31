package com.mohistmc.academy.client.gui;

import com.mohistmc.academy.client.KeyInputHandler;
import com.mohistmc.academy.skill.AcademyAttachments;
import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.Skill;
import com.mohistmc.academy.skill.SkillPreset;
import com.mohistmc.academy.skill.SkillRegistry;
import com.mohistmc.academy.AcademyCraft;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = AcademyCraft.MODID, value = Dist.CLIENT)
public class AbilityHudOverlay {

    private static final int COLOR_BG = 0xAA101020;
    private static final int COLOR_BAR_BG = 0xCC2c3e50;
    private static final int COLOR_ACTIVE = 0xFF2ecc71;
    private static final int COLOR_INACTIVE = 0xFFe74c3c;
    private static final int COLOR_CP = 0xFF3498db;
    private static final int COLOR_OVERLOAD = 0xFFe67e22;
    private static final int COLOR_TEXT_WHITE = 0xFFFFFFFF;
    private static final int COLOR_TEXT_GRAY = 0xFF999999;
    private static final int COLOR_PROF = 0xFF2ecc71;

    private static final int SLOT_HEIGHT = 18;
    private static final int HUD_WIDTH = 60;
    private static final int BAR_HEIGHT = 4;
    private static final int MARGIN = 6;

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiLayerEvent.Post event) {
        if (!event.getName().equals(VanillaGuiLayers.HOTBAR)) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null || mc.options.hideGui) return;

        PlayerAbilityData data = mc.player.getData(AcademyAttachments.PLAYER_ABILITY);
        if (!data.hasAbility()) return;

        GuiGraphics g = event.getGuiGraphics();
        int screenW = mc.getWindow().getGuiScaledWidth();
        int screenH = mc.getWindow().getGuiScaledHeight();

        int hudX = screenW - HUD_WIDTH - MARGIN;
        int hudY = screenH / 2 - 50;

        boolean active = data.isAbilityActive();
        int statusColor = active ? COLOR_ACTIVE : COLOR_INACTIVE;
        String statusText = active ? "能力: 开启" : "能力: 关闭";

        g.fill(hudX - 2, hudY - 2, hudX + HUD_WIDTH + 2, hudY + 10, COLOR_BG);
        g.fill(hudX - 2, hudY - 2, hudX + HUD_WIDTH + 2, hudY - 1, statusColor);
        g.drawString(mc.font, statusText, hudX + 2, hudY + 1, statusColor);

        int cpBarY = hudY + 13;
        g.fill(hudX, cpBarY, hudX + HUD_WIDTH, cpBarY + BAR_HEIGHT * 2 + 2, COLOR_BG);
        int cpW = (int) (HUD_WIDTH * (data.getCurrentCp() / data.getMaxCp()));
        g.fill(hudX + 1, cpBarY + 1, hudX + 1 + cpW, cpBarY + 1 + BAR_HEIGHT, COLOR_CP);
        String cpText = "CP: " + (int) data.getCurrentCp() + "/" + (int) data.getMaxCp();
        g.drawString(mc.font, cpText, hudX + 2, cpBarY + BAR_HEIGHT + 3, COLOR_TEXT_WHITE);

        int olBarY = cpBarY + BAR_HEIGHT + 13;
        int olW = (int) (HUD_WIDTH * (data.getCurrentOverload() / data.getMaxOverload()));
        g.fill(hudX + 1, olBarY, hudX + 1 + olW, olBarY + BAR_HEIGHT, COLOR_OVERLOAD);
        String olText = "过载: " + (int) data.getCurrentOverload() + "/" + (int) data.getMaxOverload();
        g.drawString(mc.font, olText, hudX + 2, olBarY + BAR_HEIGHT + 2, COLOR_TEXT_GRAY);

        if (!active) return;

        SkillPreset preset = data.getCurrentPreset();
        KeyMapping[] keys = KeyInputHandler.getSkillKeys();
        int slotY = olBarY + BAR_HEIGHT + 14;

        g.fill(hudX - 2, slotY - 2, hudX + HUD_WIDTH + 2, slotY + SLOT_HEIGHT * SkillPreset.SLOT_COUNT + 2, COLOR_BG);

        for (int i = 0; i < SkillPreset.SLOT_COUNT; i++) {
            int iy = slotY + i * SLOT_HEIGHT;
            String skillId = preset.getSlot(i);

            String keyLabel = i < keys.length ? keys[i].getTranslatedKeyMessage().getString() : "?";
            g.drawString(mc.font, "§7" + keyLabel, hudX + 2, iy + 5, COLOR_TEXT_GRAY);

            if (skillId != null) {
                Skill skill = SkillRegistry.getSkill(skillId);
                if (skill != null) {
                    ResourceLocation icon = skill.getIconLocation();
                    int iconSize = 16;
                    int iconX = hudX + 22;
                    int iconY = iy + (SLOT_HEIGHT - iconSize) / 2;
                    g.blit(icon, iconX, iconY, 0, 0, iconSize, iconSize, iconSize, iconSize);

                    float prof = data.getProficiency(skillId);
                    int profW = (int) ((HUD_WIDTH - 24) * prof);
                    g.fill(hudX + 22, iy + SLOT_HEIGHT - 1, hudX + 22 + profW, iy + SLOT_HEIGHT, COLOR_PROF);
                }
            }
        }

        String presetText = "预设 " + (data.getCurrentPresetIndex() + 1) + "/" + PlayerAbilityData.PRESET_COUNT;
        int ptY = slotY + SLOT_HEIGHT * SkillPreset.SLOT_COUNT + 4;
        g.drawString(mc.font, presetText, hudX + (HUD_WIDTH - mc.font.width(presetText)) / 2, ptY, COLOR_TEXT_GRAY);
    }
}
