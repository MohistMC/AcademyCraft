package com.mohistmc.academy.client.gui;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.client.KeyInputHandler;
import com.mohistmc.academy.skill.AcademyAttachments;
import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.Skill;
import com.mohistmc.academy.skill.SkillPreset;
import com.mohistmc.academy.skill.SkillRegistry;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

/**
 * 技能 HUD — 参照旧版 KeyHintUI，仅显示按键+图标+冷却遮罩。
 *
 * @author Mgazul
 */
@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = AcademyCraft.MODID, value = Dist.CLIENT)
public class AbilityHudOverlay {

    private static final ResourceLocation TEX_ICON_BACK =
            ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/guis/key_hint/icon_back.png");

    private static final int ICON_SIZE = 18;
    private static final int SLOT_SPACING = 6;
    private static final int MARGIN = 12;

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiLayerEvent.Post event) {
        if (!event.getName().equals(VanillaGuiLayers.HOTBAR)) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null || mc.options.hideGui) return;

        PlayerAbilityData data = mc.player.getData(AcademyAttachments.PLAYER_ABILITY);
        if (!data.hasAbility() || !data.isAbilityActive()) return;

        GuiGraphics g = event.getGuiGraphics();
        int screenW = mc.getWindow().getGuiScaledWidth();
        int screenH = mc.getWindow().getGuiScaledHeight();

        SkillPreset preset = data.getCurrentPreset();
        KeyMapping[] keys = KeyInputHandler.getSkillKeys();

        // 收集有技能的槽位
        List<Integer> activeSlots = new ArrayList<>();
        for (int i = 0; i < SkillPreset.SLOT_COUNT; i++) {
            if (preset.getSlot(i) != null) activeSlots.add(i);
        }
        if (activeSlots.isEmpty()) return;

        int totalH = activeSlots.size() * (ICON_SIZE + 6) - 6;
        int x = screenW - ICON_SIZE - MARGIN;
        int y = screenH / 2 - totalH / 2;

        for (int idx = 0; idx < activeSlots.size(); idx++) {
            int slot = activeSlots.get(idx);
            int iy = y + idx * (ICON_SIZE + 6 + SLOT_SPACING);
            String skillId = preset.getSlot(slot);

            // 按键标签（左侧小字）
            String keyLabel = slot < keys.length
                    ? keys[slot].getTranslatedKeyMessage().getString() : "?";
            int labelW = mc.font.width(keyLabel);
            int labelX = x - labelW - 6;
            int labelY = iy + (ICON_SIZE - mc.font.lineHeight) / 2;
            g.drawString(mc.font, keyLabel, labelX, labelY, 0xFF4488CC, false);

            // 图标背景
            g.blit(TEX_ICON_BACK, x, iy, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);

            // 图标 + 冷却遮罩
            if (skillId != null) {
                Skill skill = SkillRegistry.getSkill(skillId);
                if (skill != null) {
                    ResourceLocation icon = skill.getIconLocation();
                    // 图标在背景内居中 (1px padding)
                    int iconPad = 1;
                    g.blit(icon, x + iconPad, iy + iconPad, 0, 0, 16, 16, 16, 16);

                    // 冷却遮罩 — 原版风格：从上往下覆盖，随冷却减少向上收缩
                    int cdTicks = data.getCooldownTicks(skillId);
                    if (cdTicks > 0) {
                        int maxCd = data.getMaxCooldownTicks(skillId);
                        float cdProgress = maxCd > 0 ? (float) cdTicks / maxCd : 0;
                        // cdProgress: 0=冷却完毕, 1=冷却刚开始
                        // 遮罩从顶部向下覆盖，高度 = 剩余冷却比例
                        int cdH = (int) (ICON_SIZE * cdProgress);
                        if (cdH > 0) {
                            // 从顶部 (iy) 向下覆盖 cdH 高度
                            g.fill(x, iy, x + ICON_SIZE, iy + cdH, 0xAA000000);
                        }
                    }
                }
            }
        }
    }
}
