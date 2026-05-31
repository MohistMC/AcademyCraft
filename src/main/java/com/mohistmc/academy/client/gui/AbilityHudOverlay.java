package com.mohistmc.academy.client.gui;

import com.mohistmc.academy.client.KeyInputHandler;
import com.mohistmc.academy.skill.AcademyAttachments;
import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.Skill;
import com.mohistmc.academy.skill.SkillPreset;
import com.mohistmc.academy.skill.SkillRegistry;
import com.mohistmc.academy.AcademyCraft;
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

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = AcademyCraft.MODID, value = Dist.CLIENT)
public class AbilityHudOverlay {

    // 参考旧代码 KeyHintUI 的贴图资源定义方式
    private static final ResourceLocation TEX_BACK = ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/guis/key_hint/back.png");
    private static final ResourceLocation TEX_ICON_BACK = ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/guis/key_hint/icon_back.png");


    private static final int SLOT_HEIGHT = 18;
    private static final int HUD_WIDTH = 40;
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

        boolean active = data.isAbilityActive();

        // ============ 技能槽位区域（上下居中） ============
        if (!active) return;

        SkillPreset preset = data.getCurrentPreset();
        KeyMapping[] keys = KeyInputHandler.getSkillKeys();

        // 收集有技能的槽位索引
        List<Integer> activeSlots = new ArrayList<>();
        for (int i = 0; i < SkillPreset.SLOT_COUNT; i++) {
            if (preset.getSlot(i) != null) {
                activeSlots.add(i);
            }
        }

        int slotX = screenW - HUD_WIDTH - MARGIN;

        // 没有安装任何技能时，只显示预设切换文字
        if (activeSlots.isEmpty()) {
            String presetText = "预设 " + (data.getCurrentPresetIndex() + 1) + "/" + PlayerAbilityData.PRESET_COUNT;
            int ptY = screenH / 2;
            g.drawString(mc.font, presetText, slotX + (HUD_WIDTH - mc.font.width(presetText)) / 2, ptY, 0xFF999999);
            return;
        }

        // 槽位间距
        int slotSpacing = 2;

        // 根据实际有技能的槽位数计算区域高度和居中位置（包含间距）
        int slotAreaHeight = activeSlots.size() * SLOT_HEIGHT + (activeSlots.size() - 1) * slotSpacing;
        int slotY = screenH / 2 - slotAreaHeight / 2;

        for (int idx = 0; idx < activeSlots.size(); idx++) {
            int i = activeSlots.get(idx);
            int iy = slotY + idx * (SLOT_HEIGHT + slotSpacing);
            String skillId = preset.getSlot(i);

            g.blit(TEX_BACK, slotX, iy, 0, 0, HUD_WIDTH, SLOT_HEIGHT, HUD_WIDTH, SLOT_HEIGHT);

            String keyLabel = i < keys.length ? keys[i].getTranslatedKeyMessage().getString() : "?";
            g.drawString(mc.font, "§7" + keyLabel, slotX + 2, iy + 5, 0xFF999999);

            if (skillId != null) {
                Skill skill = SkillRegistry.getSkill(skillId);
                if (skill != null) {
                    ResourceLocation icon = skill.getIconLocation();

                    // 图标背景稍大（18x18），技能图标在内部居中（16x16）
                    int bgSize = 18;
                    int iconSize = 16;
                    int iconX = slotX + 22;
                    int iconY = iy + (SLOT_HEIGHT - iconSize) / 2;

                    // 背景偏移：让 16x16 图标居中在 18x18 背景内
                    int bgX = iconX - (bgSize - iconSize) / 2;
                    int bgY = iconY - (bgSize - iconSize) / 2;

                    g.blit(TEX_ICON_BACK, bgX, bgY, 0, 0, bgSize, bgSize, bgSize, bgSize);


                    // 绘制技能图标（在背景中居中）
                    g.blit(icon, iconX, iconY, 0, 0, iconSize, iconSize, iconSize, iconSize);
                }
            }
        }
    }
}
