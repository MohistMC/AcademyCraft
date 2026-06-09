package com.mohistmc.academy.skill.ability.vecmanip;

import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import net.minecraft.server.level.ServerPlayer;

public class StormWingEffect implements SkillEffect {

    @Override
    public String getId() {
        return "storm_wing";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        // TODO: 实现风暴之翼技能
    }
}
