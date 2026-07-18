package com.mohistmc.academy.skill.ability.telekinesis;

import com.mohistmc.academy.client.effect.EffectHelper;
import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import static com.mohistmc.academy.utils.MathUtils.lerpf;

/**
 * 过载思维 —— 短时间内极大增强念力能力，获得多种增益效果
 */
public class OverloadThinkingEffect implements SkillEffect {

    @Override
    public String getId() {
        return "overload_thinking";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        float exp = data.getProficiency(getId());
        int duration = (int) lerpf(300, 600, exp);
        int amplifier = (int) lerpf(1, 3, exp);

        ServerLevel level = player.serverLevel();

        EffectHelper.glowBurst(level, player.getX(), player.getY() + player.getBbHeight() / 2, player.getZ(), 50, 0.15f, 0xAAFFFFFF, 10, 1.0);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0f, 1.5f);

        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, duration, amplifier));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, duration, amplifier));
        player.addEffect(new MobEffectInstance(MobEffects.JUMP, duration, amplifier));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, duration / 2, amplifier));

        if (!data.isDevMode()) {
            data.addProficiency(getId(), 0.005f);
        }
    }
}
