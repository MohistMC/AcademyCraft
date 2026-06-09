package com.mohistmc.academy.skill.ability.meltdowner;

import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import com.mohistmc.academy.client.sound.AcademySounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import static com.mohistmc.academy.utils.MathUtils.lerpf;

/**
 * 电子导弹 —— 发射追踪导弹，命中后爆炸
 */
public class ElectronMissileEffect implements SkillEffect {

    @Override
    public String getId() {
        return "electron_missile";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        float exp = data.getProficiency(getId());
        float damage = lerpf(25.0f, 40.0f, exp);
        float radius = lerpf(3.0f, 5.0f, exp);

        ServerLevel level = player.serverLevel();
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();

        // 模拟导弹飞行轨迹
        Vec3 missilePos = eyePos.add(lookVec.scale(5));
        for (int step = 0; step < 20; step++) {
            missilePos = missilePos.add(lookVec.scale(0.5));
            level.sendParticles(ParticleTypes.FIREWORK,
                    missilePos.x, missilePos.y, missilePos.z,
                    1, 0.05, 0.05, 0.05, 0.01);
        }

        // 爆炸效果
        level.sendParticles(ParticleTypes.EXPLOSION,
                missilePos.x, missilePos.y, missilePos.z,
                (int) (radius * 3), radius / 2, radius / 2, radius / 2, 0.1);

        level.playSound(null, missilePos.x, missilePos.y, missilePos.z,
                AcademySounds.MD_BALLSHOOT, SoundSource.PLAYERS, 1.0f, 0.5f);

        // AOE伤害
        AABB area = new AABB(
                missilePos.x - radius, missilePos.y - radius, missilePos.z - radius,
                missilePos.x + radius, missilePos.y + radius, missilePos.z + radius
        );
        for (Entity e : level.getEntities(player, area, Entity::isAlive)) {
            if (e instanceof LivingEntity living) {
                float distancedDamage = damage * (float) (1.0 - Math.min(e.distanceToSqr(missilePos.x, missilePos.y, missilePos.z) / (radius * radius), 1.0));
                living.hurt(player.damageSources().playerAttack(player), Math.max(distancedDamage, 1));
            }
        }

        if (!data.isDevMode()) {
            data.addProficiency(getId(), 0.005f);
        }
    }
}
