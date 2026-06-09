package com.mohistmc.academy.skill.ability.vecmanip;

import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import net.minecraft.server.level.ServerPlayer;

public class GroundShockEffect implements SkillEffect {

    @Override
    public String getId() {
        return "ground_shock";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        // TODO: 实现地面冲击技能
    }
}
