package com.mohistmc.academy.client;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.client.gui.DataTerminalGui;
import com.mohistmc.academy.client.gui.SkillSlotGui;
import com.mohistmc.academy.client.sound.ClientSoundUtils;
import com.mohistmc.academy.world.AcademySounds;
import com.mohistmc.academy.network.SkillKeyDownPacket;
import com.mohistmc.academy.network.SkillKeyUpPacket;
import com.mohistmc.academy.network.ToggleAbilityPacket;
import com.mohistmc.academy.network.UseSkillPacket;
import com.mohistmc.academy.skill.AcademyAttachments;
import com.mohistmc.academy.skill.ChargingSkillEffect;
import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.Skill;
import com.mohistmc.academy.skill.SkillEffect;
import com.mohistmc.academy.skill.SkillPreset;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
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

    // 记录哪些技能槽位正在蓄力
    private static final boolean[] CHARGING_SLOTS = new boolean[SKILL_KEYS.length];

    // 记录上一帧的 isDown() 状态，用于边缘检测（按下/抬起）
    private static final boolean[] WAS_DOWN = new boolean[SKILL_KEYS.length];

    public static KeyMapping[] getSkillKeys() {
        return SKILL_KEYS;
    }

    /**
     * 由 SyncChargingStatePacket 调用，用于同步服务端自动释放后的客户端状态
     */
    public static void resetChargingSlot(int slotIndex) {
        if (slotIndex >= 0 && slotIndex < CHARGING_SLOTS.length) {
            CHARGING_SLOTS[slotIndex] = false;
        }
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

    /**
     * 处理非技能按键（终端、技能槽、切换能力、切换预设）。
     * 技能按键的充能逻辑已完全移至 onClientTick 进行边缘检测，
     * 避免 consumeClick() 在长按期间因 GLFW_REPEAT 累积导致状态不同步。
     */
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
            ClientSoundUtils.playClient(AcademySounds.ABILITY_PRESET_CONFIRM, SoundSource.MASTER, 0.4f, 1.0f);
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

        // 非充能技能：仍使用 consumeClick() 获得即时响应
        for (int i = 0; i < SKILL_KEYS.length; i++) {
            if (SKILL_KEYS[i].consumeClick()) {
                Skill skill = data.getSlotSkill(data.getCurrentPresetIndex(), i);
                if (skill != null && skill.getEffect() instanceof ChargingSkillEffect) {
                    // 充能技能由 onClientTick 边缘检测统一处理，这里只 consume 掉 click 防止残留
                    return;
                }
                String skillId = data.getCurrentPreset().getSlot(i);
                if (skillId != null && skill != null && data.canUseSkill(skill)) {
                    PacketDistributor.sendToServer(new UseSkillPacket(i));
                } else if (skillId == null) {
                    mc.player.displayClientMessage(Component.literal("§7槽位 " + (i + 1) + " 未装备技能"), true);
                } else {
                    mc.player.displayClientMessage(Component.literal("§c技能无法使用"), true);
                    ClientSoundUtils.playClient(AcademySounds.ABILITY_DENY, SoundSource.MASTER, 0.4f, 1.0f);
                }
                return;
            }
        }

        if (SWITCH_PRESET.consumeClick()) {
            int next = (data.getCurrentPresetIndex() + 1) % PlayerAbilityData.PRESET_COUNT;
            data.setCurrentPreset(next);
            data.syncTo(mc.player);
            mc.player.displayClientMessage(Component.literal("§a切换预设: " + (next + 1)), true);
            ClientSoundUtils.playClient(AcademySounds.ABILITY_PRESET_SWITCH, SoundSource.MASTER, 0.5f, 1.0f);
        }
    }

    /**
     * 客户端 tick：通过 isDown() 边缘检测统一处理所有技能按键的按下/抬起。
     * 使用边缘检测替代 consumeClick()，彻底避免 GLFW_REPEAT 累积导致的
     * "技能释放后仍在蓄力" 问题。
     */
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (mc.screen != null) return;

        PlayerAbilityData data = mc.player.getData(AcademyAttachments.PLAYER_ABILITY);
        if (!data.isAbilityActive()) return;

        for (int i = 0; i < SKILL_KEYS.length; i++) {
            boolean down = SKILL_KEYS[i].isDown();

            // 上升沿：按键按下
            if (down && !WAS_DOWN[i]) {
                Skill skill = data.getSlotSkill(data.getCurrentPresetIndex(), i);
                String skillId = data.getCurrentPreset().getSlot(i);
                if (skillId != null && skill != null && data.canUseSkill(skill)) {
                    if (skill.getEffect() instanceof ChargingSkillEffect) {
                        if (!CHARGING_SLOTS[i]) {
                            CHARGING_SLOTS[i] = true;
                            PacketDistributor.sendToServer(new SkillKeyDownPacket(i));
                        }
                    }
                    // 非充能技能已在 onKeyInput 中处理
                }
            }

            // 下降沿：按键抬起
            if (!down && WAS_DOWN[i]) {
                if (CHARGING_SLOTS[i]) {
                    CHARGING_SLOTS[i] = false;
                    Skill skill = data.getSlotSkill(data.getCurrentPresetIndex(), i);
                    if (skill != null && skill.getEffect() instanceof ChargingSkillEffect) {
                        PacketDistributor.sendToServer(new SkillKeyUpPacket(i));
                    }
                }
            }

            WAS_DOWN[i] = down;
        }
    }
}
