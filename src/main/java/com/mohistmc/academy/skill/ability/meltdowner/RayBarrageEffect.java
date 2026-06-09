package com.mohistmc.academy.skill.ability.meltdowner;

import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import net.minecraft.server.level.ServerPlayer;

public class RayBarrageEffect implements SkillEffect {

    @Override
    public String getId() {
        return "ray_barrage";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        // TODO: 实现射线弹幕技能
    }
}
