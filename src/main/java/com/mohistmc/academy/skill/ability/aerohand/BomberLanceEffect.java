package com.mohistmc.academy.skill.ability.aerohand;

import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import net.minecraft.server.level.ServerPlayer;

public class BomberLanceEffect implements SkillEffect {

    @Override
    public String getId() {
        return "bomber_lance";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        // TODO: 实现轰炸长矛技能
    }
}
