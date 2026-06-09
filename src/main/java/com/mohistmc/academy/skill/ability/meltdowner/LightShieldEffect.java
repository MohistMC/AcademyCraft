package com.mohistmc.academy.skill.ability.meltdowner;

import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import com.mohistmc.academy.client.effect.EffectHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import com.mohistmc.academy.client.sound.AcademySounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import static com.mohistmc.academy.utils.MathUtils.lerpf;

/**
 * 光盾 —— 生成能量护盾，获得伤害抗性与抗性提升
 */
public class LightShieldEffect implements SkillEffect {

    @Override
    public String getId() {
        return "light_shield";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        float exp = data.getProficiency(getId());
        int duration = (int) lerpf(100, 300, exp);
        int amplifier = (int) lerpf(0, 2, exp);

        ServerLevel level = player.serverLevel();

        EffectHelper.glowBurst(level, player.getX(), player.getY() + player.getBbHeight() / 2, player.getZ(), 30, 0.3f, 0x88AADDFF, 15, 0.5);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                AcademySounds.MD_SHIELD_STARTUP, SoundSource.PLAYERS, 1.0f, 1.5f);

        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, duration, amplifier));
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, duration, amplifier));

        if (!data.isDevMode()) {
            data.addProficiency(getId(), 0.005f);
        }
    }
}
