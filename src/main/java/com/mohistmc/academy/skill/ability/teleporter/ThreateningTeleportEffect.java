package com.mohistmc.academy.skill.ability.teleporter;

import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import net.minecraft.server.level.ServerPlayer;

public class ThreateningTeleportEffect implements SkillEffect {

    @Override
    public String getId() {
        return "threatening_teleport";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        // TODO: 实现威胁传送技能
    }
}
