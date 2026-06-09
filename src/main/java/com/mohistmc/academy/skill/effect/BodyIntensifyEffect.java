package com.mohistmc.academy.skill.effect;

import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class BodyIntensifyEffect implements SkillEffect {

    @Override
    public String getId() {
        return "body_intensify";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        float proficiency = data.getProficiency(getId());
        int duration = (int) (100 + proficiency * 200);
        int amplifier = (int) (proficiency * 2);

        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, duration, amplifier));
        player.addEffect(new MobEffectInstance(MobEffects.JUMP, duration, amplifier));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, duration, amplifier));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, duration, amplifier));
    }
}
