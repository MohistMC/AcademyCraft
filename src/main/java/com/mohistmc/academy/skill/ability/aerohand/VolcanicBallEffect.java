package com.mohistmc.academy.skill.ability.aerohand;

import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import static com.mohistmc.academy.utils.MathUtils.lerpf;

/**
 * 火山球 —— 向前方发射火球，命中后爆炸
 */
public class VolcanicBallEffect implements SkillEffect {

    @Override
    public String getId() {
        return "volcanic_ball";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        float exp = data.getProficiency(getId());
        float damage = lerpf(8.0f, 15.0f, exp);
        double range = lerpf(10.0f, 18.0f, exp);
        float radius = lerpf(2.0f, 3.5f, exp);

        ServerLevel level = player.serverLevel();
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();
        Vec3 impactPos = eyePos.add(lookVec.scale(range));

        // 飞行粒子
        for (double d = 1.0; d <= range; d += 0.5) {
            Vec3 pos = eyePos.add(lookVec.scale(d));
            level.sendParticles(ParticleTypes.FLAME,
                    pos.x, pos.y, pos.z,
                    1, 0.1, 0.1, 0.1, 0.01);
        }

        // 爆炸粒子
        level.sendParticles(ParticleTypes.LAVA,
                impactPos.x, impactPos.y, impactPos.z,
                (int) (radius * 5), radius / 2, radius / 2, radius / 2, 0.1);

        level.playSound(null, impactPos.x, impactPos.y, impactPos.z,
                SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1.0f, 0.5f);

        AABB area = new AABB(
                impactPos.x - radius, impactPos.y - radius, impactPos.z - radius,
                impactPos.x + radius, impactPos.y + radius, impactPos.z + radius
        );
        for (Entity e : level.getEntities(player, area, Entity::isAlive)) {
            if (e instanceof LivingEntity living) {
                float distancedDamage = damage * (float) (1.0 - Math.min(e.distanceToSqr(impactPos.x, impactPos.y, impactPos.z) / (radius * radius), 1.0));
                living.hurt(player.damageSources().playerAttack(player), Math.max(distancedDamage, 1));
                living.setRemainingFireTicks(100);
            }
        }

        if (!data.isDevMode()) {
            data.addProficiency(getId(), 0.005f);
        }
    }
}
