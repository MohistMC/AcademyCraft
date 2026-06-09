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
 * 矢量反射 —— 获得伤害反弹护盾
 */
public class VecReflectionEffect implements SkillEffect {

    @Override
    public String getId() {
        return "vec_reflection";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        float exp = data.getProficiency(getId());
        int duration = (int) lerpf(150, 300, exp);
        int amplifier = (int) lerpf(1, 3, exp);

        ServerLevel level = player.serverLevel();

        level.sendParticles(ParticleTypes.ENCHANTED_HIT,
                player.getX(), player.getY() + player.getBbHeight() / 2, player.getZ(),
                30, 0.5, 0.5, 0.5, 0.1);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0f, 1.0f);

        // 伤害吸收 + 荆棘效果
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, duration, amplifier));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, duration, amplifier));

        if (!data.isDevMode()) {
            data.addProficiency(getId(), 0.005f);
        }
    }
}
