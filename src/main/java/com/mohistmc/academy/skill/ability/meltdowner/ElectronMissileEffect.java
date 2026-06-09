package com.mohistmc.academy.skill.ability.meltdowner;

import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import net.minecraft.server.level.ServerPlayer;

public class ElectronMissileEffect implements SkillEffect {

    @Override
    public String getId() {
        return "electron_missile";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        // TODO: 实现电子导弹技能
    }
}
