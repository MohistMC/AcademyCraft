package com.mohistmc.academy.skill.ability.telekinesis;

import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import net.minecraft.server.level.ServerPlayer;

public class PsychoNeedlingEffect implements SkillEffect {

    @Override
    public String getId() {
        return "psycho_needling";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        // TODO: 实现念力针技能
    }
}
