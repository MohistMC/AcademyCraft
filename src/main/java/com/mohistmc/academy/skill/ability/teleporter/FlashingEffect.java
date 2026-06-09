package com.mohistmc.academy.skill.ability.teleporter;

import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import net.minecraft.server.level.ServerPlayer;

public class FlashingEffect implements SkillEffect {

    @Override
    public String getId() {
        return "flashing";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        // TODO: 实现闪烁技能
    }
}
