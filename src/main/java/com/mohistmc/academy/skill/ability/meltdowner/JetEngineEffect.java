package com.mohistmc.academy.skill.ability.meltdowner;

import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import com.mohistmc.academy.client.effect.EffectHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import com.mohistmc.academy.world.AcademySounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;

import static com.mohistmc.academy.utils.MathUtils.lerpf;

/**
 * 喷射引擎 —— 向后方喷射，获得高速飞行效果
 */
public class JetEngineEffect implements SkillEffect {

    @Override
    public String getId() {
        return "jet_engine";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        float exp = data.getProficiency(getId());
        double force = lerpf(1.5f, 3.0f, exp);
        int duration = (int) lerpf(100, 200, exp);
        int amplifier = (int) lerpf(1, 3, exp);

        ServerLevel level = player.serverLevel();
        Vec3 lookVec = player.getLookAngle();
        Vec3 thrust = lookVec.scale(-force);

        // 喷射粒子
        for (int i = 0; i < 10; i++) {
            EffectHelper.meltdownBurst(level, player.getX(), player.getY(), player.getZ(), 1, thrust.x * 0.5);
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                AcademySounds.MD_SIMPLE_CHARGE, SoundSource.PLAYERS, 1.0f, 0.5f);

        // 向后喷射推力
        player.setDeltaMovement(player.getDeltaMovement().add(thrust));
        player.hurtMarked = true;

        // 速度提升效果
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, duration, amplifier));
        player.addEffect(new MobEffectInstance(MobEffects.JUMP, duration, amplifier));

        if (!data.isDevMode()) {
            data.addProficiency(getId(), 0.005f);
        }
    }
}
