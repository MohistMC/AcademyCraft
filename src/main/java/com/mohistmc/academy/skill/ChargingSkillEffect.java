package com.mohistmc.academy.skill;

import net.minecraft.server.level.ServerPlayer;

public interface ChargingSkillEffect extends SkillEffect {

    void onChargingStart(ServerPlayer player, PlayerAbilityData data);

    boolean onChargingTick(ServerPlayer player, PlayerAbilityData data, int ticks);

    void onChargingRelease(ServerPlayer player, PlayerAbilityData data, int ticks);

    void onChargingAbort(ServerPlayer player, PlayerAbilityData data);

    int getMinChargeTicks();

    int getMaxChargeTicks();
}
