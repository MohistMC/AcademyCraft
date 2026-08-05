package com.mohistmc.academy.skill;

import net.minecraft.server.level.ServerPlayer;

public interface SkillEffect {

    String getId();

    void execute(ServerPlayer player, PlayerAbilityData data);

    /** 返回技能冷却 tick 数(基于熟练度),默认 40 tick(2秒),子类可覆盖。 */
    default int getCooldownTicks(float proficiency) {
        return 40;
    }
}
