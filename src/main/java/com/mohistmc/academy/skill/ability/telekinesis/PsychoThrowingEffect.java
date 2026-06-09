package com.mohistmc.academy.skill.ability.telekinesis;

import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import net.minecraft.server.level.ServerPlayer;

public class PsychoThrowingEffect implements SkillEffect {

    @Override
    public String getId() {
        return "psycho_throwing";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        // TODO: 实现念力投掷技能
    }
}
