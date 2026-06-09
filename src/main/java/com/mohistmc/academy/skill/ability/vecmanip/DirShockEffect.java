package com.mohistmc.academy.skill.ability.vecmanip;

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

import java.util.List;

import static com.mohistmc.academy.utils.MathUtils.lerpf;

/**
 * 定向冲击 —— 对前方扇形区域实体造成伤害并击退
 */
public class DirShockEffect implements SkillEffect {

    private static final double RANGE = 8.0;
    private static final double ANGLE = Math.toRadians(60);

    @Override
    public String getId() {
        return "dir_shock";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        float exp = data.getProficiency(getId());
        float damage = lerpf(6.0f, 12.0f, exp);
        float knockback = lerpf(0.5f, 1.5f, exp);

        ServerLevel level = player.serverLevel();
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();

        List<Entity> entities = level.getEntities(player, player.getBoundingBox().inflate(RANGE), Entity::isAlive);
        for (Entity e : entities) {
            if (e == player) continue;
            Vec3 toEntity = e.position().subtract(eyePos).normalize();
            double angle = Math.acos(lookVec.dot(toEntity));
            if (angle < ANGLE && e instanceof LivingEntity living) {
                // 伤害
                living.hurt(player.damageSources().playerAttack(player), damage);
                // 击退
                Vec3 knock = lookVec.scale(knockback);
                living.setDeltaMovement(living.getDeltaMovement().add(knock));
                living.hurtMarked = true;
                // 粒子
                level.sendParticles(ParticleTypes.CRIT,
                        e.getX(), e.getY() + e.getBbHeight() / 2, e.getZ(),
                        5, 0.3, 0.3, 0.3, 0.1);
            }
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0f, 1.0f);

        if (!data.isDevMode()) {
            data.addProficiency(getId(), 0.005f);
        }
    }
}
