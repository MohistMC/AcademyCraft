package com.mohistmc.academy.skill.ability.meltdowner;

import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import net.minecraft.server.level.ServerPlayer;

public class MeltdownerEffect implements SkillEffect {

    @Override
    public String getId() {
        return "meltdowner";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        // TODO: 实现原子崩坏技能
    }
}
