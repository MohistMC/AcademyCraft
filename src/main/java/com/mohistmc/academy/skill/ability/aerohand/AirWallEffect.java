package com.mohistmc.academy.skill.ability.aerohand;

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
 * 空气墙 —— 生成空气护盾，击退周围敌人并获得抗性
 */
public class AirWallEffect implements SkillEffect {

    @Override
    public String getId() {
        return "air_wall";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        float exp = data.getProficiency(getId());
        int duration = (int) lerpf(200, 400, exp);
        int amplifier = (int) lerpf(1, 3, exp);

        ServerLevel level = player.serverLevel();

        EffectHelper.windBurst(level, player.getX(), player.getY() + player.getBbHeight() / 2, player.getZ(), 40, 1.0);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1.0f, 0.5f);

        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, duration, amplifier));
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, duration, amplifier));

        if (!data.isDevMode()) {
            data.addProficiency(getId(), 0.005f);
        }
    }
}
