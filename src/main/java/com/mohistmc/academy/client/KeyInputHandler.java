package com.mohistmc.academy.client;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.client.gui.DataTerminalGui;
import com.mohistmc.academy.client.gui.SkillSlotGui;
import com.mohistmc.academy.network.ToggleAbilityPacket;
import com.mohistmc.academy.network.UseSkillPacket;
import com.mohistmc.academy.skill.AcademyAttachments;
import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.Skill;
import com.mohistmc.academy.skill.SkillPreset;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = AcademyCraft.MODID, value = Dist.CLIENT)
public class KeyInputHandler {

    public static final KeyMapping OPEN_SKILL_SLOT = new KeyMapping(
            "key.academy.skill_slot",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_N,
            "key.categories.academy"
    );

    public static final KeyMapping TOGGLE_ABILITY = new KeyMapping(
            "key.academy.toggle_ability",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_M,
            "key.categories.academy"
    );

    public static final KeyMapping SKILL_1 = new KeyMapping(
            "key.academy.skill_1",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "key.categories.academy"
    );

    public static final KeyMapping SKILL_2 = new KeyMapping(
            "key.academy.skill_2",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_T,
            "key.categories.academy"
    );

    public static final KeyMapping SKILL_3 = new KeyMapping(
            "key.academy.skill_3",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_Y,
            "key.categories.academy"
    );

    public static final KeyMapping SKILL_4 = new KeyMapping(
            "key.academy.skill_4",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_U,
            "key.categories.academy"
    );

    public static final KeyMapping SWITCH_PRESET = new KeyMapping(
            "key.academy.switch_preset",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            "key.categories.academy"
    );

    public static final KeyMapping OPEN_TERMINAL = new KeyMapping(
            "key.academy.open_terminal",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_ALT,
            "key.categories.academy"
    );

    private static final KeyMapping[] SKILL_KEYS = { SKILL_1, SKILL_2, SKILL_3, SKILL_4 };

    public static KeyMapping[] getSkillKeys() {
        return SKILL_KEYS;
    }

    public static void register(RegisterKeyMappingsEvent event) {
        event.register(OPEN_SKILL_SLOT);
        event.register(TOGGLE_ABILITY);
        event.register(SKILL_1);
        event.register(SKILL_2);
        event.register(SKILL_3);
        event.register(SKILL_4);
        event.register(SWITCH_PRESET);
        event.register(OPEN_TERMINAL);
    }

    @SubscribeEvent
    public static void onKeyInput(net.neoforged.neoforge.client.event.InputEvent.Key event) {
        if (event.getAction() != GLFW.GLFW_PRESS) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        if (OPEN_TERMINAL.consumeClick()) {
            if (mc.screen == null) {
                PlayerAbilityData data = mc.player.getData(AcademyAttachments.PLAYER_ABILITY);
                if (data.isTerminalInstalled()) {
                    mc.setScreen(new DataTerminalGui());
                } else {
                    mc.player.displayClientMessage(Component.literal("§7[数据终端] §c尚未安装数据终端，请使用数据终端安装。"), true);
                }
            }
            return;
        }

        if (OPEN_SKILL_SLOT.consumeClick()) {
            if (mc.screen == null) {
                mc.setScreen(new SkillSlotGui());
            }
            return;
        }

        if (mc.screen != null) return;

        PlayerAbilityData data = mc.player.getData(AcademyAttachments.PLAYER_ABILITY);
        if (!data.hasAbility()) return;

        if (TOGGLE_ABILITY.consumeClick()) {
            PacketDistributor.sendToServer(ToggleAbilityPacket.INSTANCE);
            return;
        }

        if (!data.isAbilityActive()) {
            for (KeyMapping key : SKILL_KEYS) {
                if (key.consumeClick()) {
                    mc.player.displayClientMessage(Component.literal("§c能力未激活，按 " + TOGGLE_ABILITY.getTranslatedKeyMessage().getString() + " 开启"), true);
                    return;
                }
            }
            if (SWITCH_PRESET.consumeClick()) {
                mc.player.displayClientMessage(Component.literal("§c能力未激活"), true);
                return;
            }
            return;
        }

        for (int i = 0; i < SKILL_KEYS.length; i++) {
            if (SKILL_KEYS[i].consumeClick()) {
                SkillPreset preset = data.getCurrentPreset();
                String skillId = preset.getSlot(i);
                if (skillId != null) {
                    Skill skill = data.getSlotSkill(data.getCurrentPresetIndex(), i);
                    if (skill != null && data.canUseSkill(skill)) {
                        PacketDistributor.sendToServer(new UseSkillPacket(i));
                    } else {
                        mc.player.displayClientMessage(Component.literal("§c技能无法使用"), true);
                    }
                } else {
                    mc.player.displayClientMessage(Component.literal("§7槽位 " + (i + 1) + " 未装备技能"), true);
                }
                return;
            }
        }

        if (SWITCH_PRESET.consumeClick()) {
            int next = (data.getCurrentPresetIndex() + 1) % PlayerAbilityData.PRESET_COUNT;
            data.setCurrentPreset(next);
            data.syncTo(mc.player);
            mc.player.displayClientMessage(Component.literal("§a切换预设: " + (next + 1)), true);
        }
    }
}
