package com.mohistmc.academy.skill.ability.vecmanip;

import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import static com.mohistmc.academy.utils.MathUtils.lerpf;

/**
 * 风暴之翼 —— 获得短暂飞行能力
 */
public class StormWingEffect implements SkillEffect {

    @Override
    public String getId() {
        return "storm_wing";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        float exp = data.getProficiency(getId());
        int duration = (int) lerpf(100, 300, exp);

        ServerLevel level = player.serverLevel();

        level.sendParticles(ParticleTypes.CLOUD,
                player.getX(), player.getY(), player.getZ(),
                30, 0.5, 0.5, 0.5, 0.1);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENDER_DRAGON_FLAP, SoundSource.PLAYERS, 1.0f, 1.5f);

        // 缓落效果模拟飞行
        player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, duration, 0));
        player.addEffect(new MobEffectInstance(MobEffects.JUMP, duration, 2));

        if (!data.isDevMode()) {
            data.addProficiency(getId(), 0.005f);
        }
    }
}
