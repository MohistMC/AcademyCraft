package com.mohistmc.academy.client;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.network.UseSkillPacket;
import com.mohistmc.academy.skill.AcademyAttachments;
import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.Skill;
import com.mohistmc.academy.skill.SkillRegistry;
import com.mohistmc.academy.skill.SkillType;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.List;
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

/**
 * @author Mgazul
 * @date 2026/5/30 21:53
 */
@EventBusSubscriber(modid = AcademyCraft.MODID, value = Dist.CLIENT)
public class KeyInputHandler {

    public static final KeyMapping USE_SKILL = new KeyMapping(
            "key.academy.use_skill",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "key.categories.academy"
    );

    public static void register(RegisterKeyMappingsEvent event) {
        event.register(USE_SKILL);
    }

    @SubscribeEvent
    public static void onKeyInput(net.neoforged.neoforge.client.event.InputEvent.Key event) {
        if (event.getAction() != GLFW.GLFW_PRESS) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) return;

        if (USE_SKILL.consumeClick()) {
            PlayerAbilityData data = mc.player.getData(AcademyAttachments.PLAYER_ABILITY);
            if (!data.hasAbility()) return;

            Skill activeSkill = findActiveSkill(data);
            if (activeSkill != null) {
                PacketDistributor.sendToServer(new UseSkillPacket(activeSkill.getId()));
            } else {
                mc.player.displayClientMessage(Component.literal("§7没有可用的主动技能"), true);
            }
        }
    }

    private static Skill findActiveSkill(PlayerAbilityData data) {
        List<Skill> skills = SkillRegistry.getSkillsByCategory(data.getCurrentAbility());
        for (Skill skill : skills) {
            if (skill.getType() != SkillType.ACTIVE) continue;
            if (!data.hasLearnedSkill(skill.getId())) continue;
            if (skill.getBaseCpCost() <= 0) continue;
            if (data.canUseSkill(skill)) {
                return skill;
            }
        }
        return null;
    }
}
