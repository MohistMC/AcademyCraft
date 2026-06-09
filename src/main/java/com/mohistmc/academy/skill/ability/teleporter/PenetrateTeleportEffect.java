package com.mohistmc.academy.skill.ability.teleporter;

import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import net.minecraft.server.level.ServerPlayer;

public class PenetrateTeleportEffect implements SkillEffect {

    @Override
    public String getId() {
        return "penetrate_teleport";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        // TODO: 实现穿透传送技能
    }
}
