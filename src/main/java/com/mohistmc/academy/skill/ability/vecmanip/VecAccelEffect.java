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
import net.minecraft.world.phys.Vec3;

import static com.mohistmc.academy.utils.MathUtils.lerpf;

/**
 * 矢量加速 —— 获得高速移动与跳跃能力
 */
public class VecAccelEffect implements SkillEffect {

    @Override
    public String getId() {
        return "vec_accel";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        float exp = data.getProficiency(getId());
        int duration = (int) lerpf(200, 400, exp);
        int amplifier = (int) lerpf(2, 5, exp);

        ServerLevel level = player.serverLevel();

        level.sendParticles(ParticleTypes.SWEEP_ATTACK,
                player.getX(), player.getY() + player.getBbHeight() / 2, player.getZ(),
                20, 0.5, 0.5, 0.5, 0.1);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENDER_DRAGON_FLAP, SoundSource.PLAYERS, 1.0f, 1.5f);

        // 速度提升
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, duration, amplifier));
        player.addEffect(new MobEffectInstance(MobEffects.JUMP, duration, amplifier));

        // 向前冲刺
        Vec3 lookVec = player.getLookAngle();
        Vec3 dash = lookVec.scale(lerpf(1.5f, 3.0f, exp));
        player.setDeltaMovement(player.getDeltaMovement().add(dash));
        player.hurtMarked = true;

        if (!data.isDevMode()) {
            data.addProficiency(getId(), 0.005f);
        }
    }
}
