package com.mohistmc.academy.skill;

import net.minecraft.server.level.ServerPlayer;

/**
 * @author Mgazul
 * @date 2026/5/30 22:01
 */
public interface SkillEffect {

    String getId();

    void execute(ServerPlayer player, PlayerAbilityData data);
}
