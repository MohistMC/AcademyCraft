package com.mohistmc.academy.skill.ability.aerohand;

import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

import static com.mohistmc.academy.utils.MathUtils.lerpf;

/**
 * 空气冷却 —— 治疗自身与周围友方，并熄灭火焰
 */
public class AirCoolingEffect implements SkillEffect {

    @Override
    public String getId() {
        return "air_cooling";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        float exp = data.getProficiency(getId());
        float healAmount = lerpf(4.0f, 10.0f, exp);
        float radius = lerpf(3.0f, 6.0f, exp);

        ServerLevel level = player.serverLevel();

        level.sendParticles(ParticleTypes.SNOWFLAKE,
                player.getX(), player.getY() + player.getBbHeight() / 2, player.getZ(),
                30, radius / 2, 0.5, radius / 2, 0.1);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.PLAYER_HURT_FREEZE, SoundSource.PLAYERS, 1.0f, 1.5f);

        AABB area = new AABB(
                player.getX() - radius, player.getY() - 1, player.getZ() - radius,
                player.getX() + radius, player.getY() + 2, player.getZ() + radius
        );

        for (Entity e : level.getEntities(player, area, Entity::isAlive)) {
            if (e instanceof LivingEntity living) {
                living.heal(healAmount);
                living.clearFire();
                living.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 200, 0));
            }
        }

        if (!data.isDevMode()) {
            data.addProficiency(getId(), 0.005f);
        }
    }
}
